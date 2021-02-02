package dev.majek.activelobby;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginEventHandler implements Listener {

    public static Map<Location, SavedState> savedStates = new ConcurrentHashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) return;
        if (!player.getWorld().getName().equals(Persistence.getInstance()
                .getConfig().getString("lobby-world-name"))) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Location location = block.getLocation();

        // Door handler
        if (block.getType().name().endsWith("_DOOR") && !block.getType().name().contains("LEGACY")) {
            Door door = (Door) block.getBlockData();
            if (door.getHalf() == Bisected.Half.TOP)
                location = location.subtract(0,1,0);
            door.setHalf(Bisected.Half.BOTTOM);
            System.out.println(location.toString());
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
                //System.out.println("1 " + savedStates.get(location).getState());
            } else {
                savedStates.put(location, new SavedState(door.isOpen(), System.currentTimeMillis(), Interactable.DOOR));
                //System.out.println("2 " + door.isOpen());
            }
        }

        // Trapdoor handler
        if (block.getType().name().endsWith("TRAPDOOR") && !block.getType().name().contains("LEGACY")) {
            TrapDoor trapDoor = (TrapDoor) block.getBlockData();
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
                //System.out.println("1 " + savedStates.get(location).getState());
            } else {
                savedStates.put(location, new SavedState(trapDoor.isOpen(), System.currentTimeMillis(), Interactable.TRAPDOOR));
                //System.out.println("2 " + trapDoor.isOpen());
            }
        }

        // Gate handler
        if (block.getType().name().endsWith("GATE") && !block.getType().name().contains("LEGACY")) {
            Gate gate = (Gate) block.getBlockData();
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
                //System.out.println("1 " + savedStates.get(location).getState());
            } else {
                savedStates.put(location, new SavedState(gate.isOpen(), System.currentTimeMillis(), Interactable.FENCE_GATE));
                //System.out.println("2 " + gate.isOpen());
            }
        }

        // Lever handler
        if (block.getType() == Material.LEVER) {
            Powerable openable = (Powerable) block.getBlockData();
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
                //System.out.println("1 " + savedStates.get(location).getState());
            } else {
                savedStates.put(location, new SavedState(openable.isPowered(), System.currentTimeMillis(), Interactable.LEVER));
                //System.out.println("2 " + openable.isPowered());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        if (block.getType().name().endsWith("_DOOR") && !block.getType().name().contains("LEGACY")) {
            Door door = (Door) block.getBlockData();
            if (door.getHalf() == Bisected.Half.TOP)
                location = location.subtract(0,1,0);
            door.setHalf(Bisected.Half.BOTTOM);
        }
        //if (savedStates.containsKey(location))
            //System.out.println("Removed location " + location.toString());
        savedStates.remove(location);
    }

    public static void reset() {
        for (Location location : savedStates.keySet()) {
            SavedState savedState = savedStates.get(location);
            Block block = location.getWorld().getBlockAt(location);
            if ((System.currentTimeMillis() - savedState.getLastInteract()) > (savedState.getType().getCooldown() * 1000L)){
                BlockData data = block.getBlockData();
                BlockState blockState = block.getState();
                switch (savedState.getType()) {
                    case DOOR:
                        ((Door) data).setOpen(savedState.getState());
                        //Bukkit.getConsoleSender().sendMessage("Reset door to " + savedState.getState());
                        break;
                    case TRAPDOOR:
                        ((TrapDoor) data).setOpen(savedState.getState());
                        //Bukkit.getConsoleSender().sendMessage("Reset trapdoor to " + savedState.getState());
                        break;
                    case FENCE_GATE:
                        ((Gate) data).setOpen(savedState.getState());
                        //Bukkit.getConsoleSender().sendMessage("Reset gate to " + savedState.getState());
                        break;
                    case LEVER:
                        ((Powerable) data).setPowered(savedState.getState());
                        //Bukkit.getConsoleSender().sendMessage("Reset lever to " + savedState.getState());
                        break;
                }
                blockState.setBlockData(data);
                blockState.update(true);
                savedStates.remove(location);
            }
        }
    }
}
