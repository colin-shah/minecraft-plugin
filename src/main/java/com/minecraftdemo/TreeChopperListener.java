package com.minecraftdemo;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;

import javax.sql.rowset.spi.SyncFactoryException;
import java.util.HashMap;
import java.util.UUID;

public class TreeChopperListener implements Listener {
    private static final int SEARCH_RADIUS = 30;
    private HashMap<UUID, MachineState> machines;
    private AutoTreeChopper plugin;


    public TreeChopperListener(AutoTreeChopper plugin) {
        this.plugin = plugin;
        machines = new HashMap<>();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws SyncFactoryException {
        Block placedBlock = event.getBlockPlaced();

        //placedBlock.getLocation().getWorld().generateTree(placedBlock.getLocation(), TreeType.JUNGLE);

        //if (placedBlock.getType() == Material.CHEST) {
            Block belowBlock = placedBlock.getLocation().subtract(0, 1, 0).getBlock();
            //if (isMachineBase(belowBlock)) {
                // Activate machine
                MachineState machineState = new MachineState(placedBlock.getLocation(), belowBlock.getType());
                UUID machineId = UUID.randomUUID(); // Unique identifier for each machine
                machines.put(machineId, machineState);

                // Find nearest tree
                Location treeLocation = TreeFinder.findNearestTree(placedBlock.getLocation());
                plugin.getLogger().info("========== nearest tree  " + treeLocation);

                if (treeLocation != null && placedBlock.getWorld().getWorldBorder().isInside(treeLocation)) {
                    plugin.getLogger().info("========== nearest tree inside the world");
                    // Move machine towards the tree
                    BlockState blockState = placedBlock.getState();
                    if(blockState instanceof Chest){
                        /* Location nextStep = getNextStep(event.getPlayer().getLocation(), treeLocation);

                       Player player = event.getPlayer();

                        if (player != null) {
                            plugin.getLogger().info("======= player is  "+ player);
                            player.teleport(nextStep); // Teleport the player to the next machine location
                        }*/

                        Inventory chestInventory = ((Chest) blockState).getInventory();
                        new MachineMover(plugin, placedBlock.getLocation(), treeLocation, belowBlock.getType(), chestInventory, machineId)
                                .runTaskTimer(plugin, 20L, 20L); // Run every second
                    }
                }
            //}
        //}
    }




    public int getChoppingSpeed(Block baseBlock) {
        switch (baseBlock.getType()) {
            case DIAMOND_BLOCK:
                return 1; // Fastest
            case EMERALD_BLOCK:
                return 2; // Moderate
            case GOLD_BLOCK:
                return 3; // Slowest
            default:
                return 2;
        }
    }

    public void handleSpecialBlockEncounter(Block block, UUID machineId) {
        switch (block.getType()) {
            case WATER:
            case ICE:
                // Define behavior when encountering water or ice
                break;
            case SPONGE:
                // Define behavior when encountering a sponge
                break;
            case FERN:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case DANDELION:
            case POPPY:
                deactivateMachine(machineId);
                break;
            default:
                // Default behavior for other blocks
                break;
        }
    }

    private void deactivateMachine(UUID machineId) {
        if (machines.containsKey(machineId)) {
            MachineState machineState = machines.get(machineId);
            machineState.setActive(false);
        }
    }


    private boolean isFull(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }



    private void checkChestFull(Inventory chestInventory) {
        if (isFull(chestInventory)) {
            // Notify player
            // Stop machine operation
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

    private boolean isMachineBase(Block block) throws SyncFactoryException {
        plugin.getLogger().info("===========================isMachineBase block type " + block.getType());
        return block.getType() == Material.DIAMOND_BLOCK || block.getType() == Material.EMERALD_BLOCK || block.getType() == Material.GOLD_BLOCK;
    }
}
