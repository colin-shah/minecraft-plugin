package com.minecraftdemo;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.World;


public class TreeFinder {

    private static final int SEARCH_RADIUS = 30;

    public static Location findNearestTree(Location machineLocation) {
        World world = machineLocation.getWorld();
        Location nearestTree = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    Block block = world.getBlockAt(machineLocation.clone().add(x, y, z));
                    if (isTreeBlock(block)) {
                        double distance = block.getLocation().distanceSquared(machineLocation);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestTree = block.getLocation();
                        }
                    }
                }
            }
        }

        return nearestTree;
    }


    private static boolean isWithinBoundary(Location location, Location minBoundary, Location maxBoundary) {
        // Check if the location is within the defined boundaries
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minBoundary.getX() && x <= maxBoundary.getX() &&
                y >= minBoundary.getY() && y <= maxBoundary.getY() &&
                z >= minBoundary.getZ() && z <= maxBoundary.getZ();
    }

    private static boolean isTreeBlock(Block block) {
        // Simple check for a log block - you might need to expand this to handle different tree types
        return block.getType() == Material.OAK_LOG || block.getType() == Material.SPRUCE_LOG ||
                block.getType() == Material.BIRCH_LOG || block.getType() == Material.JUNGLE_LOG ||
                block.getType() == Material.ACACIA_LOG || block.getType() == Material.DARK_OAK_LOG;
    }
}
