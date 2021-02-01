package dev.majek.activelobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":"
                + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }

    public static Location deserializeLocation(String serialized) {
        String[] parts = serialized.split(":");
        return parts.length == 6 ? new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]))
                : new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

    /**
     * Collect a group of Materials that have similar endings ignoring certain Materials
     *
     * @param with   end of Material names that should be collected.
     * @param except a List of Materials that should be ignored when collecting similar Materials
     * @return a List of Materials which all end with the value passed in minus exception Materials.
     */
    public static List<Material> materialsEndingWith(String with, List<Material> except) {
        return Arrays.stream(Material.values()).filter(material -> !material.name().contains("LEGACY") &&
                material.name().endsWith(with) && !except.contains(material)).collect(Collectors.toList());
    }
}
