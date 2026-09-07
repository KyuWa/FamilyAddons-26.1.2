// FamilyAddons presence worker — "who is running the mod right now / ever".
//
// The mod sends a heartbeat on server join and once a minute after that:
//   POST /beat   header X-FA-Secret: <FA_SECRET>
//                body  {"uuid":"...","name":"KyoWaa","version":"1.1.5","mc":"26.1.2"}
//
// Two read endpoints, protected by a SEPARATE key that never ships in the jar:
//   GET /online  header X-Admin-Key: <ADMIN_KEY>   -> players seen in the last ~2.5 min
//   GET /users   header X-Admin-Key: <ADMIN_KEY>   -> everyone ever seen, newest first
//
// Storage: KV namespace bound as PRESENCE.
//   u:<uuid>  {uuid,name,version,mc,first,last,beats}   permanent record
//   o:<uuid>  "1" with a 150 s TTL                     online marker
//
// Deploy (from this folder):
//   npx wrangler kv namespace create PRESENCE      (put the id in wrangler.toml)
//   npx wrangler secret put FA_SECRET              (= KeyFetcher.SECRET_TOKEN)
//   npx wrangler secret put ADMIN_KEY              (anything long and random)
//   npx wrangler deploy

const ONLINE_TTL_S = 150;
const UUID_RE = /^[0-9a-f]{8}-?[0-9a-f]{4}-?[0-9a-f]{4}-?[0-9a-f]{4}-?[0-9a-f]{12}$/i;
const NAME_RE = /^[A-Za-z0-9_]{1,16}$/;

function json(obj, status = 200) {
  return new Response(JSON.stringify(obj), {
    status,
    headers: { "content-type": "application/json; charset=utf-8", "cache-control": "no-store" },
  });
}

function normUuid(u) {
  const hex = String(u).replace(/-/g, "").toLowerCase();
  return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`;
}

async function listAll(kv, prefix) {
  const keys = [];
  let cursor;
  do {
    const page = await kv.list({ prefix, cursor });
    keys.push(...page.keys.map((k) => k.name));
    cursor = page.list_complete ? undefined : page.cursor;
  } while (cursor);
  return keys;
}

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    // ── Heartbeat from the mod ──────────────────────────────────────────
    if (url.pathname === "/beat" && request.method === "POST") {
      if (!env.FA_SECRET || request.headers.get("X-FA-Secret") !== env.FA_SECRET) {
        return json({ error: "unauthorized" }, 401);
      }
      let body;
      try { body = await request.json(); } catch { return json({ error: "bad json" }, 400); }
      if (!body || !UUID_RE.test(body.uuid || "") || !NAME_RE.test(body.name || "")) {
        return json({ error: "bad uuid/name" }, 400);
      }
      const uuid = normUuid(body.uuid);
      const version = String(body.version || "?").slice(0, 32);
      const mc = String(body.mc || "?").slice(0, 16);
      const now = Date.now();

      const key = "u:" + uuid;
      const prev = await env.PRESENCE.get(key, "json");
      const rec = {
        uuid,
        name: body.name,
        version,
        mc,
        first: prev?.first ?? now,
        last: now,
        beats: (prev?.beats ?? 0) + 1,
      };
      await env.PRESENCE.put(key, JSON.stringify(rec));
      await env.PRESENCE.put("o:" + uuid, "1", { expirationTtl: ONLINE_TTL_S });
      return new Response(null, { status: 204 });
    }

    // ── Owner read endpoints ────────────────────────────────────────────
    if (request.method === "GET" && (url.pathname === "/online" || url.pathname === "/users")) {
      if (!env.ADMIN_KEY || request.headers.get("X-Admin-Key") !== env.ADMIN_KEY) {
        return json({ error: "unauthorized" }, 401);
      }
      const onlineKeys = await listAll(env.PRESENCE, "o:");
      const online = new Set(onlineKeys.map((k) => k.slice(2)));

      const wanted = url.pathname === "/online"
        ? [...online]
        : (await listAll(env.PRESENCE, "u:")).map((k) => k.slice(2));

      const users = [];
      for (const uuid of wanted) {
        const rec = await env.PRESENCE.get("u:" + uuid, "json");
        if (rec) users.push({ ...rec, online: online.has(uuid) });
      }
      users.sort((a, b) => b.last - a.last);
      return json({ now: Date.now(), count: users.length, online: online.size, users });
    }

    return json({ error: "not found" }, 404);
  },
};
