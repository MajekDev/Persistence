package dev.majek.activelobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import java.util.*;

public class SimpleCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setlobbyworld")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be in-game to use this command."); return true;
            }
            Player player = (Player) sender;
            if (!player.isOp())
                return true;
            Bukkit.getScheduler().runTaskAsynchronously(ActiveLobby.getInstance(), () -> {
                World world = player.getWorld();
                double length = world.getWorldBorder().getSize();
                int radius = (int) length / 2;
                Bukkit.getConsoleSender().sendMessage("World border size: " + length + " Radius: " + radius);
                Bukkit.getConsoleSender().sendMessage("Beginning to gather all blocks in the world...");
                long start = System.currentTimeMillis();
                List<Location> except = new ArrayList<>();
                //List<Material> doors = Utils.materialsEndingWith("_DOOR", Collections.emptyList());
                for (double x = -radius; x <= radius; x++) {
                    for (double z = -radius; z <= radius; z++) {
                        for (double y = 0; y <= 256; y++) {
                            Block block = world.getBlockAt((int) x, (int) y, (int) z);
                            Location location = new Location(world, x, y, z);
                            if (block.getType() == Material.AIR)
                                continue;
                            // Is empty is just so it runs faster
                            if (!except.isEmpty() && except.contains(location))
                                continue;
                            // IT IS A DOOR
                            if (block.getType().name().endsWith("_DOOR") && !block.getType().name().contains("LEGACY")) {
                                Door door = (Door) block.getBlockData();
                                ActiveLobby.getInstance().doorMap.put(Utils.serializeLocation(location), door.isOpen());
                                Bukkit.getConsoleSender().sendMessage(Utils.serializeLocation(location) + " | " + door.isOpen());
                                except.add(location.add(0,1,0));
                            }
                            ActiveLobby.getInstance().blockMap.put(new Location(world,x,y,z), block.getType());
                        }
                    }
                }
                Bukkit.getConsoleSender().sendMessage("Finished gathering all blocks in the world in "
                        + (System.currentTimeMillis() - start) + "ms.");
            });
            return true;
        }
        if (command.getName().equalsIgnoreCase("checkmap")) {
            Player player = (Player) sender;
            Location location = new Location(player.getWorld(), Double.parseDouble(args[0]),
                    Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            String toSend = ActiveLobby.getInstance().blockMap.get(location) == null ? "Air" :
                    ActiveLobby.getInstance().blockMap.get(location).toString();
            player.sendMessage(toSend);
            return true;
        }
        if (command.getName().equalsIgnoreCase("revert")) {

            PluginEventHandler.reset();

            /*
            Player player = (Player) sender;
            Map<String, Boolean> doorMap = ActiveLobby.getInstance().doorMap;
            for (String serializedLocation : doorMap.keySet()) {
                Location deserializedLocation = Utils.deserializeLocation(serializedLocation);
                Block block = player.getWorld().getBlockAt(deserializedLocation.getBlockX(),
                        deserializedLocation.getBlockY(), deserializedLocation.getBlockZ());
                BlockState blockState = block.getState();
                Openable openable = (Openable) blockState.getData();
                Bukkit.getConsoleSender().sendMessage("Door at " + serializedLocation + " is " + (openable.isOpen()
                        ? "open" : "closed") + " and should be " + (doorMap.get(serializedLocation) ? "open" : "closed"));
                openable.setOpen(doorMap.get(serializedLocation));
                //blockState.setData((MaterialData) openable);
                blockState.update();
            }

             */
            Bukkit.getConsoleSender().sendMessage("Finished");
            return true;
        }
        return false;
    }
}
