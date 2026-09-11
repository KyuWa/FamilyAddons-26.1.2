"""Create a GitHub release and upload one jar.
usage: gh_release.py <token> <owner/repo> <tag> <name> <jar path> <asset name> <notes file>
"""
import json, sys, urllib.request

tok, repo, tag, name, jar, asset_name, notes = sys.argv[1:8]
body = open(notes, encoding="utf-8").read().strip().replace("\n", "\r\n")

def call(url, data=None, ctype="application/json"):
    req = urllib.request.Request(url, data=data, method="POST" if data else "GET")
    req.add_header("Authorization", "Bearer " + tok)
    req.add_header("Accept", "application/vnd.github+json")
    req.add_header("Content-Type", ctype)
    with urllib.request.urlopen(req) as r:
        return json.load(r)

rel = call(f"https://api.github.com/repos/{repo}/releases", json.dumps({
    "tag_name": tag, "target_commitish": "master", "name": name,
    "body": body, "draft": False, "prerelease": False}).encode())
print("release:", rel["html_url"])
asset = call(rel["upload_url"].split("{")[0] + "?name=" + asset_name,
             open(jar, "rb").read(), "application/java-archive")
print("asset:", asset["name"], asset["size"], asset["state"])
