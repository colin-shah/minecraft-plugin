package com.minecraftdemo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class AutoTreeChopper extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(new TreeChopperListener(this), this);
        getLogger().info("AutoTreeChopper enabled");

        World world = Bukkit.getWorlds().get(0);
        Location blockLocation = world.getSpawnLocation();
        Block bitBlock = world.getBlockAt(blockLocation);
        bitBlock.setType(getRandomBitBlockMaterial(), true);
        placeChestOnTop(bitBlock);
    }

    private Material getRandomBitBlockMaterial() {
        Random random = new Random();
        int randomNumber = random.nextInt(3);  // 0, 1, or 2

        switch (randomNumber) {
            case 0:
                return Material.DIAMOND_BLOCK;
            case 1:
                return Material.EMERALD_BLOCK;
            case 2:
                return Material.GOLD_BLOCK;
            default:
                return Material.DIAMOND_BLOCK; // Default to diamond block
        }
    }

    private void placeChestOnTop(Block baseBlock) {
        getLogger().info("placeChest initiated");

        if (!baseBlock.getType().isSolid()) {
            getLogger().warning("Cannot place chest on top. The block below is not solid.");
            return;
        }

        Location chestLocation = baseBlock.getLocation().add(0, 1, 0);
        getLogger().info("chestLocation found: " + chestLocation);

        Block blockPlaced = chestLocation.getBlock();
        blockPlaced.setType(Material.CHEST);

        BlockData blockData = blockPlaced.getBlockData();
        getLogger().info("blockData: " + blockData);

        if (blockData instanceof Directional) {
            getLogger().info("blockData found: " + blockData);
            Directional directional = (Directional) blockData;
            directional.setFacing(BlockFace.NORTH);
            blockPlaced.setBlockData(directional);
            getLogger().info("chestLocation updated: " + chestLocation);
        }

        Chest chest = (Chest) blockPlaced.getState();
        getLogger().info("final chest: " + chest);
    }

    @Override
    public void onDisable() {
        getLogger().info("AutoTreeChopper disabled.");
    }
}
