package dev.majek.activelobby;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
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
        if (!player.getWorld().getName().equals(ActiveLobby.getInstance()
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
                System.out.println("1 " + savedStates.get(location).getState());
            } else {
                savedStates.put(location, new SavedState(door.isOpen(), System.currentTimeMillis(), Interactable.DOOR));
                System.out.println("2 " + door.isOpen());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (savedStates.containsKey(event.getBlock().getLocation()))
            System.out.println("Removed location " + event.getBlock().getLocation().toString());
        savedStates.remove(event.getBlock().getLocation());
    }

    public static void reset() {
        for (Location location : savedStates.keySet()) {
            SavedState savedState = savedStates.get(location);
            Block block = location.getWorld().getBlockAt(location);
            if ((System.currentTimeMillis() - savedState.getLastInteract()) > (savedState.getType().getCooldown() * 1000L)){
                switch (savedState.getType()) {
                    case DOOR:
                        BlockData data = block.getBlockData();
                        BlockState blockState = block.getState();
                        ((Door) data).setOpen(savedState.getState());
                        blockState.setBlockData(data);
                        blockState.update(true);
                        /*
                        Openable openable = (Openable) blockState.getData();
                        openable.setOpen(savedState.getState());
                        blockState.setData((MaterialData) openable);
                        blockState.update();
                         */
                        Bukkit.getConsoleSender().sendMessage("Reset door to " + savedState.getState());
                        savedStates.remove(location);
                }
            }
        }
    }
}
