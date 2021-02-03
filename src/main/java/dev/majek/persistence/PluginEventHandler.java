package dev.majek.persistence;

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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PluginEventHandler implements Listener {

    public static Map<Location, SavedState> savedStates = new ConcurrentHashMap<>();
    public static Map<UUID, Pair<Location, Long>> vehicleMap = new ConcurrentHashMap<>();

    @EventHandler
    public void onInteractBlock(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!Persistence.getCore().getConfig().getBoolean("track-creative")
                && player.getGameMode() == GameMode.CREATIVE) return;
        if (!player.getWorld().getName().equals(Persistence.getCore()
                .getConfig().getString("lobby-world-name"))) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Location location = block.getLocation();

        // Door handler
        if (block.getType().name().endsWith("_DOOR") && !block.getType().name().contains("LEGACY")
                && Interactable.DOOR.isEnabled()) {
            Door door = (Door) block.getBlockData();
            if (door.getHalf() == Bisected.Half.TOP)
                location = location.subtract(0,1,0);
            door.setHalf(Bisected.Half.BOTTOM);
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
            } else
                savedStates.put(location, new SavedState(door.isOpen(),
                        System.currentTimeMillis(), Interactable.DOOR));
        }

        // Trapdoor handler
        if (block.getType().name().endsWith("TRAPDOOR") && !block.getType().name().contains("LEGACY")
                && Interactable.TRAPDOOR.isEnabled()) {
            TrapDoor trapDoor = (TrapDoor) block.getBlockData();
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
            } else
                savedStates.put(location, new SavedState(trapDoor.isOpen(),
                        System.currentTimeMillis(), Interactable.TRAPDOOR));
        }

        // Gate handler
        if (block.getType().name().endsWith("GATE") && !block.getType().name().contains("LEGACY")
                && Interactable.FENCE_GATE.isEnabled()) {
            Gate gate = (Gate) block.getBlockData();
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
            } else
                savedStates.put(location, new SavedState(gate.isOpen(),
                        System.currentTimeMillis(), Interactable.FENCE_GATE));
        }

        // Lever handler
        if (block.getType() == Material.LEVER && Interactable.LEVER.isEnabled()) {
            Powerable openable = (Powerable) block.getBlockData();
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
            } else
                savedStates.put(location, new SavedState(openable.isPowered(),
                        System.currentTimeMillis(), Interactable.LEVER));
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!Persistence.getCore().getConfig().getBoolean("track-creative")
                && player.getGameMode() == GameMode.CREATIVE) return;
        if (!player.getWorld().getName().equals(Persistence.getCore()
                .getConfig().getString("lobby-world-name"))) return;

        Entity entity = event.getRightClicked();
        Location location = entity.getLocation();

        // ItemFrame handler
        if (entity instanceof ItemFrame && Interactable.ITEM_FRAME.isEnabled()) {
            ItemFrame itemFrame = (ItemFrame) entity;
            if (savedStates.containsKey(location)) {
                SavedState newState = savedStates.get(location);
                newState.setLastInteract(System.currentTimeMillis());
                savedStates.put(location, newState);
            } else
                savedStates.put(location, new SavedState(false, itemFrame.getRotation(),
                        System.currentTimeMillis(), Interactable.ITEM_FRAME, itemFrame.getUniqueId()));
        }
    }

    @EventHandler
    public void onMount(VehicleEnterEvent event) {
        if (!Interactable.VEHICLE.isEnabled()) return;
        if (!(event.getEntered() instanceof Player)) return;
        Player player = (Player) event.getEntered();
        Vehicle vehicle = event.getVehicle();

        if (!Persistence.getCore().getConfig().getBoolean("track-creative")
                && player.getGameMode() == GameMode.CREATIVE) return;
        if (!player.getWorld().getName().equals(Persistence.getCore()
                .getConfig().getString("lobby-world-name"))) return;

        if (vehicleMap.containsKey(vehicle.getUniqueId())
                && Persistence.getCore().getConfig().getBoolean("always-return-original"))
            vehicleMap.put(vehicle.getUniqueId(), new Pair<>(vehicleMap.get(vehicle.getUniqueId()).getFirst(),
                    System.currentTimeMillis()));
        else
            vehicleMap.put(vehicle.getUniqueId(), new Pair<>(vehicle.getLocation(), System.currentTimeMillis()));
    }

    @EventHandler
    public void onDismount(VehicleExitEvent event) {
        if (!Interactable.VEHICLE.isEnabled()) return;
        if (!(event.getExited() instanceof Player)) return;
        Vehicle vehicle = event.getVehicle();

        if (vehicleMap.containsKey(vehicle.getUniqueId())) {
            vehicleMap.put(vehicle.getUniqueId(), new Pair<>(vehicleMap.get(vehicle.getUniqueId()).getFirst(),
                    System.currentTimeMillis()));
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
        savedStates.remove(location);
    }

    public static void reset() {
        reset(false);
    }

    /**
     * Reset interactables cooldown is expired.
     * @param force Ignore cooldown and reset anyway.
     */
    public static void reset(boolean force) {
        for (Location location : savedStates.keySet()) {
            SavedState savedState = savedStates.get(location);
            Block block = location.getWorld().getBlockAt(location);
            Entity entity = null;
            if (savedState.getRotation() != null)
                entity = location.getWorld().getEntity(savedState.getUuid());
            if ((System.currentTimeMillis() - savedState.getLastInteract())
                    > (savedState.getType().getCooldown() * 1000L) || force) {
                BlockData data = block.getBlockData();
                BlockState blockState = block.getState();
                switch (savedState.getType()) {
                    case DOOR:
                        ((Door) data).setOpen(savedState.getState()); break;
                    case TRAPDOOR:
                        ((TrapDoor) data).setOpen(savedState.getState()); break;
                    case FENCE_GATE:
                        ((Gate) data).setOpen(savedState.getState()); break;
                    case LEVER:
                        ((Powerable) data).setPowered(savedState.getState()); break;
                    case ITEM_FRAME:
                        if (entity != null)
                            ((ItemFrame) entity).setRotation(savedState.getRotation());
                        break;
                }
                blockState.setBlockData(data);
                blockState.update(true);
                savedStates.remove(location);
            }
        }
        for (UUID uuid : vehicleMap.keySet()) {
            if ((System.currentTimeMillis() - vehicleMap.get(uuid).getSecond())
                    > (Interactable.VEHICLE.getCooldown() * 1000L) || force) {
                Location location = vehicleMap.get(uuid).getFirst();
                Vehicle vehicle = (Vehicle) location.getWorld().getEntity(uuid);
                if (vehicle == null) continue;
                if (force || vehicle.isEmpty()) {
                    vehicle.eject();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Persistence.getCore(), () -> {
                        vehicle.teleport(location);
                        vehicleMap.remove(uuid);
                    }, 5L);
                } else
                    vehicleMap.put(vehicle.getUniqueId(), new Pair<>(vehicleMap.get(vehicle.getUniqueId()).getFirst(),
                            System.currentTimeMillis()));
            }
        }
    }
}
