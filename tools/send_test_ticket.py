"""Send a fake ticket to the mod (same wire format as bot.py) and listen for the claim.

Usage: python fake_ticket.py          -> sends one Infernal x3 ticket, waits 60s for a claim
"""
import asyncio, json, socket

MOD_HOST, MOD_PORT, CLAIM_PORT = "127.0.0.1", 25570, 25571

def send(data):
    with socket.socket() as s:
        s.settimeout(2)
        s.connect((MOD_HOST, MOD_PORT))
        s.sendall((json.dumps(data) + "\n").encode())
    print("[test] sent", data["action"])

async def claim_listener(reader, writer):
    line = await reader.readline()
    print("[test] claim received:", line.decode().strip())
    writer.close()

async def main():
    server = await asyncio.start_server(claim_listener, MOD_HOST, CLAIM_PORT)
    print(f"[test] claim listener on {CLAIM_PORT}")
    send({"action": "ticket", "server": "Kuudra Gang", "ign": "TestPlayer", "tier": "Infernal",
          "runs": "3", "channel_id": "123456789012345678", "server_id": "1035208186745081928",
          "message_id": "987654321098765432"})
    async with server:
        try:
            await asyncio.wait_for(server.serve_forever(), 60)
        except asyncio.TimeoutError:
            print("[test] no claim within 60s")
    send({"action": "ticket_closed", "server": "Kuudra Gang", "tier": "Infernal", "ign": "TestPlayer",
          "channel_id": "123456789012345678"})

asyncio.run(main())
