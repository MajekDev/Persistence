# Persistence
Persistence is a simple plugin that allows players to interact with blocks, like doors and gates, and vehicles, like horses and minecarts, and have the block/vehicle return to it's previous state after a configured length of time. 

### Currently supported blocks/vehicles
- Doors
- Trapdoors
- Fence gates
- Item frames
- Levers
- All vehicles ([What's a vehicle?](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/Vehicle.html))

### How to use
Place the downloaded jar in your plugins folder and restart your server. The config file will now generate and you'll need to set the name of the world you want Persistence to affect. This is where you can also change the length of time before a block/vehicle reverts and a few other config options.

Default config.yml:
```yml
# Persistence by Majekdor | Created 2/1/2021 | Updated 2/2/2021
# Need help? Join my discord: https://discord.gg/CGgvDUz

# Name of the world where persistence should be active (usually your lobby/hub world)
lobby-world-name: "world"

# Cooldowns (in seconds) for every block/entity persistence affects
# Set to -1 to disabled persistence for that block/entity (plugin won't revert it)
door-cooldown: 30
trapdoor-cooldown: 30
fence-gate-cooldown: 30
lever-cooldown: 30
item-frame-cooldown: 30

# Cooldown (in seconds) for the amount of time between a player dismounting a vehicle and the vehicle returning
# Set to -1 to disable vehicles teleporting to previous location
vehicle-teleport-back: 10

# When this is set to true a vehicle will always return to it's original location, even if it's been mounted/dismounted
# If this is false the vehicle will return to the last location it was mounted
always-return-original: true

# Reset all blocks/entities that have been interacted with when there are no players in the world
reset-interactables-on-leave: true

# By default persistence only resets changes made by players in survival or adventure
# If this is true then changes made by players in creative mode will be reverted too
track-creative: false
```

### Need help or have a suggestion? Join my [Discord](https://discord.gg/CGgvDUz)

### Want to buy me coffee? [PayPal](https://www.paypal.com/paypalme/majekdor) (All donations of any amount are appreciated)
