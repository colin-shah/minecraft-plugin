package com.minecraftdemo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class TreeChopper extends BukkitRunnable {
    private final Location treeLocation;
    private final Inventory chestInventory;
    private final int choppingSpeed;

    private final AutoTreeChopper plugin;

    public TreeChopper(Location treeLocation, Inventory chestInventory, int choppingSpeed, AutoTreeChopper plugin) {
        this.treeLocation = treeLocation;
        this.chestInventory = chestInventory;
        this.choppingSpeed = choppingSpeed;

        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Logic to chop the tree block by block
        // Use 'choppingSpeed' to control the speed of chopping
        if(!treeLocation.getWorld().getPlayers().isEmpty() && !treeLocation.getWorld().getPlayers().get(0).isDead()){
            chopTree(treeLocation, chestInventory, choppingSpeed);
        }else {
            Bukkit.getLogger().info("chop tree runner is dead");
        }
    }

    public void chopTree(Location treeLocation, Inventory chestInventory, int choppingSpeed) {

        Bukkit.getLogger().info("chop tree " + treeLocation + " " + choppingSpeed);
        World world = treeLocation.getWorld();
        int range = 5;
        //int delay = 20 * choppingSpeed; // Convert chopping speed to ticks (assuming 20 ticks per second)
        int delay = 0; // Initialize delay to start immediately

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    Block block = world.getBlockAt(treeLocation.clone().add(dx, dy, dz));


                    block.breakNaturally();


                    if (isTreeBlock(block)) {
                        // Chop the block and store wood in the chest with a delay
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                chestInventory.addItem(new ItemStack(block.getType()));
                                Bukkit.getLogger().info("chopping chestInventory " + chestInventory.getSize());
                                block.setType(Material.AIR);
                            }
                        }.runTaskLater(plugin, delay);
                        delay += 20 * choppingSpeed; // Increment delay for the next block
                        Bukkit.getLogger().info("chopping chestInventory " + chestInventory.getSize());
                        Bukkit.getLogger().info("chopping delay 2 " + delay);
                    }
                }
            }
        }
    }

    private boolean isTreeBlock(Block block) {
        // Simple check for tree components (logs and leaves)
        Material type = block.getType();
        return type.toString().endsWith("_LOG") || type.toString().endsWith("_LEAVES");
    }
}
