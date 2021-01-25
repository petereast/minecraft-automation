# minecraft-automation
A tool to automate provisioning and de-provisioning a minecraft server.

## Context
- Spinning up a virtual server that is powerful enough to run minecraft for more than 2 players is expensive if it's running the whole time.
- Storing a world in block storage is more efficient, but capturing a snapshot of that volume and deleting the volume is yet more efficient.

# Iterations
## Milestone 1: Create empty servers
- [x] Create a server and run a fresh instance of Minecraft on it.
- [ ] Tear down that server.

## Milestone 2: Create servers with existing worlds in them
- [ ] Create a volume
- [ ] Mount that volume to a new server, and run Minecraft using that volume as the CWD
- [ ] Tear down the server but keep the volume

## Step 3: Store the worlds as images when not in use
- [ ] Create an image of the volume
- [ ] Destroy the volume
- [ ] Restore a volume from an image

## Step 4: Create a WebUI for this
- [ ] Create a WebServer (using server-side rendered pages)
  - [ ] Needs some very basic user management (I don't want people spending all my money on Minecraft servers!)
- [ ] Connect that WebServer to the existing library
- [ ] Store the state of different Minecraft servers & implement account limits e.t.c

## Step 5: Profit
