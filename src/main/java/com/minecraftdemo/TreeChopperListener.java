package com.minecraftdemo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import javax.sql.rowset.spi.SyncFactoryException;
import java.util.HashMap;
import java.util.UUID;

import static javax.sql.rowset.spi.SyncFactory.getLogger;

public class TreeChopperListener implements Listener {
    private static final int SEARCH_RADIUS = 30;
    private HashMap<UUID, MachineState> machines;
    private AutoTreeChopper plugin;


    public TreeChopperListener(AutoTreeChopper plugin) {
        this.plugin = plugin;
        machines = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        generateTree(player.getLocation());
    }

    private void generateTree(Location location) {
        // You can adjust the location, tree type, and other parameters as needed
        World world = location.getWorld();
        Location spawnLocation = world.getSpawnLocation();
        Bukkit.getLogger().info("generate tree: " + spawnLocation);
        location.getWorld().generateTree(spawnLocation, TreeType.TREE);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Check if the damage is caused by a zombie
        if (event.getDamager().getType() == EntityType.ZOMBIE) {
            // Cancel the damage event
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onZombieTarget(EntityTargetLivingEntityEvent event) {
        // Check if the entity is a zombie
        if (event.getEntity() instanceof Zombie) {
            // Cancel the target event to prevent zombie attacks
            event.setCancelled(true);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("gentree") && sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            World world = player.getWorld();

            // Adjust the location if needed
            location.setY(world.getHighestBlockYAt(location));

            // Generate a tree
            boolean treeGenerated = world.generateTree(location, TreeType.TREE);
            if (treeGenerated) {
                player.sendMessage("Tree generated!");
            } else {
                player.sendMessage("Failed to generate tree.");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws SyncFactoryException {
        Block placedBlock = event.getBlockPlaced();
        generateTree(placedBlock.getLocation());

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
            // Move machine towards the tree
            BlockState blockState = placedBlock.getState();
            if (blockState instanceof Chest) {
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
