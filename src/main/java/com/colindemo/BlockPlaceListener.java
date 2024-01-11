package com.colindemo;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;
import java.util.Map;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // if block is a chest
        Bukkit.getLogger().info("on block place chest type" + event.getBlock().getType());
        if (event.getBlock().getType() != Material.CHEST) {
            return;
        }
        // get config for bits
        List<Map<?, ?>> bits = Main.getPlugin(Main.class).getConfig().getMapList("bits");
        Bukkit.getLogger().info("on block place bits " + bits);
        for (Map<?, ?> bit : bits) {
            // loop through all bits, check if material matches block below chest placed
            Bukkit.getLogger().info("on block place material type material " + Material.getMaterial((String) bit.get("material")));
            Bukkit.getLogger().info("on block place material type BlockFace.DOWN " + event.getBlock().getRelative(BlockFace.DOWN).getType());
            if (event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.getMaterial((String) bit.get("material"))) {
                // if so, create new TreeChopper
                TreeChopper treeChopper = new TreeChopper();
                treeChopper.init(event.getBlock(), bit, event.getPlayer());
                Main.treeChoppers.add(treeChopper);
                return;
            }
        }
    }
}
