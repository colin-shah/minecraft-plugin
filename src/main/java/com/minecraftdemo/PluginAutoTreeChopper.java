package com.minecraftdemo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PluginAutoTreeChopper extends JavaPlugin implements Listener {

    private final Map<Location, Block> activeMachines = new HashMap<>();

    /**
     * Enables the plugin and registers event listeners.
     */
    @Override
    public void onEnable() {
        getLogger().info("PluginAutoTreeChopper is enabled.");
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * Handles the BlockPlaceEvent for placing chests and machines.
     *
     * @param event The BlockPlaceEvent instance.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        getLogger().info("BlockPlaceEvent initiated");
        Block blockPlaced = event.getBlockPlaced();
        getLogger().info("blockPlaced: " + blockPlaced);

        if (blockPlaced.getType() == Material.CHEST) {
            Block belowChest = blockPlaced.getRelative(BlockFace.DOWN);
            getLogger().info("belowChest: " + belowChest);

            if (isBitBlock(belowChest.getType())) {
                activeMachines.put(blockPlaced.getLocation(), belowChest);
                startMachine(blockPlaced, belowChest);
            } else {
                checkSpecialBlockInteraction(blockPlaced);
            }

            event.setCancelled(true);
        }
    }

    /**
     * Handles the onCommand event for the "createmachine" command.
     *
     * @param sender The command sender.
     * @param cmd    The executed command.
     * @param label  The command label.
     * @param args   The command arguments.
     * @return Always returns false.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("createmachine")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Block targetBlock = player.getTargetBlock(null, 5);
                if (targetBlock != null) {
                    Location blockLocation = targetBlock.getLocation();
                    Block bitBlock = player.getWorld().getBlockAt(blockLocation);
                    bitBlock.setType(getRandomBitBlockMaterial(), true);
                    placeChestOnTop(bitBlock);
                    player.sendMessage("You placed a diamond block!");
                } else {
                    player.sendMessage("Invalid target location!");
                }
            } else {
                sender.sendMessage("Only players can use this command!");
            }
        }
        return false;
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


    /**
     * Places a chest on top of the specified block.
     *
     * @param baseBlock The block on which the chest should be placed.
     */
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

        if (blockPlaced.getType() == Material.CHEST) {
            Block belowChest = blockPlaced.getRelative(BlockFace.DOWN);
            getLogger().info("belowChest: " + belowChest);

            if (isBitBlock(belowChest.getType())) {
                activeMachines.put(blockPlaced.getLocation(), belowChest);
                startMachine(blockPlaced, belowChest);
            } else {
                checkSpecialBlockInteraction(blockPlaced);
            }
        }
    }

    /**
     * Starts the automated chopping machine.
     *
     * @param chestBlock The chest block associated with the machine.
     * @param bitBlock   The bit block beneath the chest.
     */
    private void startMachine(Block chestBlock, Block bitBlock) {
        getLogger().info("startMachine initiated");

        if (chestBlock.getState() instanceof Chest) {
            Chest chest = (Chest) chestBlock.getState();
            Inventory chestInventory = chest.getInventory();
            Material bitMaterial = bitBlock.getType();
            getLogger().info("bitMaterial: " + bitMaterial);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!(chest).isPlaced() || !activeMachines.containsKey(chest.getLocation())) {
                        return;
                    }

                    getLogger().info("chopTree is here");

                    chopTree(getChopRange(bitMaterial), bitBlock);

                    if (chestInventory.getSize() == chestInventory.getSize()) {
                        deactivateMachine(chestBlock);
                        sendFullChestMessage(bitBlock.getLocation());
                        cancel();
                    }
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("PluginAutoTreeChopper"), 0, 1);
        } else {
            getLogger().warning("The block is not a chest.");
        }
    }

    /**
     * Handles the BlockBreakEvent for breaking blocks.
     *
     * @param event The BlockBreakEvent instance.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        getLogger().info("onBlockBreak initiated");

        Block brokenBlock = event.getBlock();
        getLogger().info("brokenBlock initiated: " + brokenBlock);

        if (isBitBlock(brokenBlock.getType())) {
            getLogger().info("deactivateMachine initiated: " + brokenBlock.getType());
            deactivateMachine(brokenBlock);
        }

        getLogger().info("brokenBlock type: " + brokenBlock.getType());

        Material bitMaterial = brokenBlock.getType();
        getLogger().info("bitMaterial: " + bitMaterial);
        chopTree(getChopRange(bitMaterial), brokenBlock);
    }

    /**
     * Deactivates the chopping machine associated with the chest block.
     *
     * @param chestBlock The chest block of the machine to deactivate.
     */
    private void deactivateMachine(Block chestBlock) {
        activeMachines.remove(chestBlock.getLocation());
    }

    /**
     * Gets the chopping speed based on the bit block material.
     *
     * @param bitBlockMaterial The material of the bit block.
     * @return The chopping speed.
     */
    private int getChoppingSpeed(Material bitBlockMaterial) {
        getLogger().info("getChoppingSpeed bitBlockMaterial: " + bitBlockMaterial);
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
     * Chops down nearby trees recursively.
     *
     * @param range     The chopping range.
     * @param logBlock  The starting log block.
     */
    private void chopTree(int range, Block logBlock) {
        getLogger().info("chopTree initiated: " + range);

        Material bitBlockMaterial = getBitBlockMaterial(logBlock.getLocation());
        int choppingSpeed = getChoppingSpeed(bitBlockMaterial);
        getLogger().info("choppingSpeed: " + choppingSpeed);

        recursiveTreeChop(logBlock, choppingSpeed);
    }

    /**
     * Recursively chops down a tree starting from the specified block.
     *
     * @param currentBlock The current block to check.
     * @param choppingSpeed The chopping speed.
     */
    private void recursiveTreeChop(Block currentBlock, int choppingSpeed) {
        if (currentBlock.getType() == Material.OAK_LOG) {
            currentBlock.breakNaturally();
            spawnParticleEffect(currentBlock.getLocation(), Particle.VILLAGER_HAPPY, 10);

            Bukkit.getScheduler().runTaskLater(this, () -> {
                recursiveTreeChop(currentBlock.getRelative(1, 0, 0), choppingSpeed);
                recursiveTreeChop(currentBlock.getRelative(-1, 0, 0), choppingSpeed);
                recursiveTreeChop(currentBlock.getRelative(0, 1, 0), choppingSpeed);
                recursiveTreeChop(currentBlock.getRelative(0, -1, 0), choppingSpeed);
            }, choppingSpeed);
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

    private boolean isBitBlock(Material material) {
        return material == Material.DIAMOND_BLOCK ||
                material == Material.EMERALD_BLOCK ||
                material == Material.GOLD_BLOCK;
    }

    private void sendFullChestMessage(Location location) {
        for (Player player : location.getWorld().getPlayers()) {
            if (location.distance(player.getLocation()) <= 10) {
                player.sendMessage("Auto Tree Chopper chest is full!");
            }
        }
    }

    private void checkSpecialBlockInteraction(Block block) {
        switch (block.getType()) {
            case FERN:
            case LARGE_FERN:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case SUNFLOWER:
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
                // Machine breaks apart
                block.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, block.getLocation(), 10);
                deactivateMachine(block);
                break;
            case WATER:
                floatOnWater(block);
                break;
            case SPONGE:
                triggerSpongeBehavior(block);
                break;
            case ICE:
                triggerIceBehavior(block);
                break;
            default:
                break;
        }
    }

    private void floatOnWater(Block waterBlock) {
        // Implement logic for floating on water (e.g., move the machine upward)
        waterBlock.getLocation().add(0, 1, 0).getBlock().setType(Material.AIR);
    }

    private void triggerSpongeBehavior(Block spongeBlock) {
        // Implement specific behaviors or effects when encountering a sponge
        // For example, you can remove nearby water blocks
        spongeBlock.getWorld().getNearbyEntities(spongeBlock.getLocation(), 5, 5, 5)
                .stream()
                .filter(entity -> entity instanceof Player)
                .forEach(entity -> entity.sendMessage("Sponge encountered! Water removed."));
    }

    private void triggerIceBehavior(Block iceBlock) {
        // Implement specific behaviors or effects when encountering ice
        // For example, you can apply a freezing effect to nearby entities
        iceBlock.getWorld().getNearbyEntities(iceBlock.getLocation(), 5, 5, 5)
                .stream()
                .filter(entity -> entity instanceof Player)
                .forEach(entity -> entity.sendMessage("Ice encountered! Freezing effect applied."));
    }
}