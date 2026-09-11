# Release / deploy helpers

- `gh_release.py <token> <owner/repo> <tag> <name> <jar> <asset name> <notes.txt>` creates a GitHub release and uploads one jar. The token comes from `git credential fill` (never stored here).
- `deploy_when_closed.ps1` waits for Minecraft to exit, then copies both `-dev` jars into the Modrinth profiles. `deploy_public_when_closed.ps1` does the same with the public jars.
- `send_test_ticket.py` pushes a fake carry ticket into the mod's local listener (Discord Tickets, dev only).

Release rule: notes first, then `go`; only the public jar is uploaded; version bump lives in `FamilyAddons.VERSION`.
