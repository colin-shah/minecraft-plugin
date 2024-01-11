package com.minecraftdemo;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MachineMover extends BukkitRunnable {

    private AutoTreeChopper plugin;
    private final Location currentLocation;
    private final Location treeLocation;
    private final Material baseMaterial;
    private final Inventory chestInventory;

    private final UUID machineId;

    public MachineMover(AutoTreeChopper plugin, Location currentLocation, Location treeLocation, Material baseMaterial, Inventory chestInventory, UUID machineId) {
        this.plugin = plugin;
        this.currentLocation = currentLocation;
        this.treeLocation = treeLocation;
        this.baseMaterial = baseMaterial;
        this.chestInventory = chestInventory;
        this.machineId = machineId;
    }

    @Override
    public void run() {
        TreeChopperListener listener = new TreeChopperListener(plugin);
        plugin.getLogger().info("target tree Location "+ treeLocation);

        if (currentLocation.getBlock().getLocation().equals(treeLocation.getBlock().getLocation())) {
            this.cancel(); // Stop the movement

            // Determine chopping speed
            int choppingSpeed = listener.getChoppingSpeed(currentLocation.getBlock().getRelative(BlockFace.DOWN));
            plugin.getLogger().info("When current location equals target tree location");

            // Start chopping tree with the determined speed
            new TreeChopper1(treeLocation, chestInventory, choppingSpeed, plugin).runTaskTimer(plugin, 0L, 20L * choppingSpeed);
            return;
        }

        // Check for special blocks in the path
        Block nextBlock = currentLocation.getWorld().getBlockAt(getNextStep(currentLocation, treeLocation));
        plugin.getLogger().info("next step Block "+nextBlock.toString());
        listener.handleSpecialBlockEncounter(nextBlock, machineId);
        // Calculate the next step towards the tree
        Location nextStep =  nextBlock.getLocation();//getNextStep(currentLocation, treeLocation);
        if (nextStep != null) {
            // Move the machine
            plugin.getLogger().info(" # of players from current Location "+currentLocation.getWorld().getPlayers());
            Player player = currentLocation.getWorld().getPlayers().get(0);
            plugin.getLogger().info("current Location "+currentLocation);
            plugin.getLogger().info(" target location "+treeLocation);
            if (player != null) {
                plugin.getLogger().info(" player is  "+ player);
                player.teleport(treeLocation); // Teleport the player to the next machine location
            }


            moveMachineTo(treeLocation, listener);
            // If there's an associated player, move the player too
            /*plugin.getLogger().info(" player about start moving ");

            int choppingSpeed = listener.getChoppingSpeed(currentLocation.getBlock().getRelative(BlockFace.DOWN));
            plugin.getLogger().info(" start chopping after reaching the tree with speed of "+choppingSpeed);
            // Start chopping tree with the determined speed
            new TreeChopper(treeLocation, chestInventory, choppingSpeed, plugin).runTaskTimer(plugin, 0L, 20L * choppingSpeed);

            plugin.getLogger().info("player Health " + player.getHealth());*/
        }
    }



    private Location getNextStep(Location from, Location to) {
        // Logic to determine the next step towards the target location
        // This is a simple straight-line movement. You might want to implement more complex pathfinding
        int dx = Integer.compare(to.getBlockX(), from.getBlockX());
        int dy = Integer.compare(to.getBlockY(), from.getBlockY());
        int dz = Integer.compare(to.getBlockZ(), from.getBlockZ());

        return from.add(dx, dy, dz);
    }

    private void moveMachineTo(Location newLocation, TreeChopperListener listener) {
        // Clear current machine location
        plugin.getLogger().info("move Machine to "+newLocation);
        currentLocation.getBlock().setType(Material.AIR);
        currentLocation.clone().add(0, 1, 0).getBlock().setType(Material.AIR);

        // Set new machine location
        Block newBaseBlock = newLocation.getBlock();
        newBaseBlock.setType(baseMaterial);
        Block newChestBlock = newLocation.clone().add(0, 1, 0).getBlock();
        newChestBlock.setType(Material.CHEST);

        // Transfer inventory to the new chest (if needed)
        Chest newChest = (Chest) newChestBlock.getState();
        newChest.getInventory().setContents(chestInventory.getContents());
        plugin.getLogger().info("moveMachine newChest "+ newChest);

       /* int choppingSpeed = listener.getChoppingSpeed(currentLocation.getBlock().getRelative(BlockFace.DOWN));
        plugin.getLogger().info("======= start chopping after reaching the tree with speed of "+choppingSpeed);
        // Start chopping tree with the determined speed
        new TreeChopper(treeLocation, chestInventory, choppingSpeed, plugin).runTaskTimer(plugin, 0L, 20L * choppingSpeed);
        plugin.getLogger().info("======= 9");*/
        return;
    }
}