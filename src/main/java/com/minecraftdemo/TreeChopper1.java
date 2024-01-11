package com.minecraftdemo;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class TreeChopper1 extends BukkitRunnable {
    private final Location treeLocation;
    private final Inventory chestInventory;
    private final int choppingSpeed;

    private final AutoTreeChopper plugin;

    public TreeChopper1(Location treeLocation, Inventory chestInventory, int choppingSpeed, AutoTreeChopper plugin) {
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
            this.cancel();
        }
    }

    private int getChopRange(Material bitMaterial) {
        if (bitMaterial == Material.DIAMOND_BLOCK) {
            return 5; // Fastest speed, wider range
        } else if (bitMaterial == Material.EMERALD_BLOCK) {
            return 3; // Moderate speed, bonus feature (e.g., particle effects)
        } else if (bitMaterial == Material.GOLD_BLOCK) {
            return 2; // Slowest speed, higher yield or special effect
        }
        return 0; // Invalid bit block, no chopping
    }


 /*   private boolean isTreeBlock(Block block) {
        Material type = block.getType();
        Material[] logMaterials = { Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, *//* Add more log materials *//* };
        Material[] leafMaterials = { Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.BIRCH_LEAVES, *//* Add more leaf materials *//* };

        // Check if the block is a log or a leaf
        for (Material logMaterial : logMaterials) {
            if (type == logMaterial) {
                return true; // Block is a log
            }
        }

        for (Material leafMaterial : leafMaterials) {
            if (type == leafMaterial) {
                return true; // Block is a leaf
            }
        }

        return false; // Not a tree block
    }*/

    /**
     * Chops down nearby trees recursively.
     *
     * @param range     The chopping range.
     * @param logBlock  The starting log block.
     */
    /*private void chopTree1(int range, Block logBlock) {
        plugin.getLogger().info("chopTree initiated: " + range);

        Material bitBlockMaterial = getBitBlockMaterial(logBlock.getLocation());
        int choppingSpeed = getChoppingSpeed(bitBlockMaterial);
        plugin.getLogger().info("choppingSpeed: " + choppingSpeed);

        recursiveTreeChop(logBlock, choppingSpeed);
    }*/



    /**
     * Gets the material of the bit block beneath the chest.
     *
     * @param location The location of the chest block.
     * @return The material of the bit block.
     */
    private Material getBitBlockMaterial(Location location) {
        Block bitBlock = location.getWorld().getBlockAt(location.add(0, -1, 0));
        return bitBlock.getType();
    }

    /**
     * Recursively chops down a tree starting from the specified block.
     *
     * @param currentBlock The current block to check.
     * @param choppingSpeed The chopping speed.
     */
    /*private void recursiveTreeChop(Block currentBlock, int choppingSpeed) {
        //if (currentBlock.getType() == Material.OAK_LOG) {
        Bukkit.getLogger().info("chop tree recursiveTreeChop called ");
        currentBlock.breakNaturally();
        spawnParticleEffect(currentBlock.getLocation(), Particle.VILLAGER_HAPPY, 10);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            recursiveTreeChop(currentBlock.getRelative(1, 0, 0), choppingSpeed);
            recursiveTreeChop(currentBlock.getRelative(-1, 0, 0), choppingSpeed);
            recursiveTreeChop(currentBlock.getRelative(0, 1, 0), choppingSpeed);
            recursiveTreeChop(currentBlock.getRelative(0, -1, 0), choppingSpeed);
        }, choppingSpeed);
        //}
    }*/
    private void recursiveTreeChop(Block currentBlock, int choppingSpeed, Inventory chestInventory) {
        if (currentBlock.getType() == Material.CHEST) {
            Chest chest = (Chest) currentBlock.getState();

            // Check if the chest is full
            if (chestInventory.firstEmpty() == -1) {
                Bukkit.getLogger().info("Chest is full. Stopping tree chopping.");
                return; // Stop further tree chopping
            }
        }

        Bukkit.getLogger().info("chop tree recursiveTreeChop called ");
        currentBlock.breakNaturally();
        spawnParticleEffect(currentBlock.getLocation(), Particle.VILLAGER_HAPPY, 10);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            recursiveTreeChop(currentBlock.getRelative(1, 0, 0), choppingSpeed, chestInventory);
            recursiveTreeChop(currentBlock.getRelative(-1, 0, 0), choppingSpeed, chestInventory);
            recursiveTreeChop(currentBlock.getRelative(0, 1, 0), choppingSpeed, chestInventory);
            recursiveTreeChop(currentBlock.getRelative(0, -1, 0), choppingSpeed, chestInventory);
        }, choppingSpeed);
    }


    /**
     * Spawns particle effects at the specified location.
     *
     * @param location The location at which to spawn particle effects.
     * @param particle The type of particle effect to spawn.
     * @param count    The number of particles to spawn.
     */
    private void spawnParticleEffect(Location location, Particle particle, int count) {
        location.getWorld().spawnParticle(particle, location, count);
    }

    /**
     * Gets the chopping speed based on the bit block material.
     *
     * @param bitBlockMaterial The material of the bit block.
     * @return The chopping speed.
     */
    private int getChoppingSpeed(Material bitBlockMaterial) {
        plugin.getLogger().info("getChoppingSpeed bitBlockMaterial: " + bitBlockMaterial);
        switch (bitBlockMaterial) {
            case DIAMOND_BLOCK:
                return 1; // Fast chopping speed
            case EMERALD_BLOCK:
                spawnParticleEffect(Bukkit.getWorlds().get(0).getSpawnLocation(), Particle.HEART, 10);
                return 2; // Moderate speed with a bonus feature
            case GOLD_BLOCK:
                spawnParticleEffect(Bukkit.getWorlds().get(0).getSpawnLocation(), Particle.BLOCK_CRACK, 10);
                return 3; // Slower speed with a bonus feature
            default:
                return 1; // Default to fast chopping speed
        }
    }

    //added new from tree finder class
    private static boolean isTreeBlock(Block block) {
        // Simple check for a log block - you might need to expand this to handle different tree types
        return block.getType() == Material.OAK_LOG || block.getType() == Material.SPRUCE_LOG ||
                block.getType() == Material.BIRCH_LOG || block.getType() == Material.JUNGLE_LOG ||
                block.getType() == Material.ACACIA_LOG || block.getType() == Material.DARK_OAK_LOG;
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


                    //block.breakNaturally();


                    //Bukkit.getLogger().info("chop tree block type " + block.getType());
                    if (isTreeBlock(block)) {
                        Bukkit.getLogger().info("chop tree is tree block " + block.getType());
                        // Chop the block and store wood in the chest with a delay
                        /*new BukkitRunnable() {
                            @Override
                            public void run() {
                                chestInventory.addItem(new ItemStack(block.getType()));
                                Bukkit.getLogger().info("chopping chestInventory " + chestInventory.getSize());
                                block.setType(Material.AIR);
                                block.breakNaturally();
                            }
                        }.runTaskLater(plugin, delay);
                        delay += 20 * choppingSpeed; // Increment delay for the next block
                        Bukkit.getLogger().info("chopping chestInventory " + chestInventory.getSize());
                        Bukkit.getLogger().info("chopping delay 2 " + delay);*/

                        recursiveTreeChop(block, choppingSpeed, chestInventory);

                        //chopTree1(getChopRange(block.getType()), block);

                    }
                }
            }
        }
    }


}
