// FamilyAddons key worker (key.kyowa.uk) — Hypixel API proxy.
//
// The Hypixel API key never leaves this worker. The mod asks for the data it
// needs and the worker makes the upstream call with the key it holds as a
// secret:
//   GET /profiles?uuid=<32 hex>   header X-FA-Secret: <FA_SECRET>
//       -> Hypixel /v2/skyblock/profiles for that uuid, verbatim
//
// Protection, since the X-FA-Secret token is shipped inside the jar and is
// therefore only a speed bump:
//   - responses are cached per uuid for CACHE_S seconds (edge cache), so a
//     player refreshing constantly costs Hypixel one call per window;
//   - on top of that, at most RL_MAX requests per uuid per RL_WINDOW_S
//     (per-isolate memory, good enough to stop a loop).
//
// The old "GET / -> the key itself" route is gone on purpose and answers 410.
//
// Deploy (from this folder):
//   npx wrangler secret put HYPIXEL_API_KEY -c key.wrangler.toml
//   npx wrangler secret put FA_SECRET       -c key.wrangler.toml   (= KeyFetcher.SECRET_TOKEN)
//   npx wrangler deploy -c key.wrangler.toml

const CACHE_S = 45;
const RL_MAX = 10;
const RL_WINDOW_S = 60;
const UUID_RE = /^[0-9a-f]{32}$/;

const hits = new Map(); // uuid -> [timestamps]

function json(obj, status = 200, extra = {}) {
  return new Response(JSON.stringify(obj), {
    status,
    headers: { "content-type": "application/json; charset=utf-8", "cache-control": "no-store", ...extra },
  });
}

function rateLimited(uuid) {
  const now = Date.now();
  const arr = (hits.get(uuid) || []).filter((t) => now - t < RL_WINDOW_S * 1000);
  if (arr.length >= RL_MAX) { hits.set(uuid, arr); return true; }
  arr.push(now);
  hits.set(uuid, arr);
  if (hits.size > 5000) hits.clear(); // never let a flood of uuids grow the map forever
  return false;
}

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);

    if (url.pathname === "/profiles" && request.method === "GET") {
      if (!env.FA_SECRET || request.headers.get("X-FA-Secret") !== env.FA_SECRET) {
        return json({ success: false, cause: "unauthorized" }, 401);
      }
      if (!env.HYPIXEL_API_KEY) return json({ success: false, cause: "worker has no key" }, 500);
      const uuid = String(url.searchParams.get("uuid") || "").replace(/-/g, "").toLowerCase();
      if (!UUID_RE.test(uuid)) return json({ success: false, cause: "bad uuid" }, 400);

      const cache = caches.default;
      const cacheKey = new Request(`https://key.kyowa.uk/cache/profiles/${uuid}`);
      const cached = await cache.match(cacheKey);
      if (cached) return cached;

      if (rateLimited(uuid)) return json({ success: false, cause: "rate limited, try again in a minute" }, 429);

      const upstream = await fetch(`https://api.hypixel.net/v2/skyblock/profiles?uuid=${uuid}`, {
        headers: { "API-Key": env.HYPIXEL_API_KEY, "User-Agent": "FamilyAddons-key-worker" },
      });
      const body = await upstream.text();
      const resp = new Response(body, {
        status: upstream.status,
        headers: { "content-type": "application/json; charset=utf-8", "cache-control": `public, max-age=${CACHE_S}` },
      });
      if (upstream.status === 200) ctx.waitUntil(cache.put(cacheKey, resp.clone()));
      return resp;
    }

    if (url.pathname === "/" || url.pathname === "") {
      return json({ error: "this endpoint no longer serves the key; update FamilyAddons" }, 410);
    }

    return json({ error: "not found" }, 404);
  },
};
