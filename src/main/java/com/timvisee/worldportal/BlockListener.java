package com.timvisee.worldportal;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {
	public static WorldPortal plugin;

	public BlockListener(WorldPortal instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		World world = event.getBlock().getWorld();
		Block block = event.getBlock();
		Player player = event.getPlayer();
	
		if(plugin.getConfig().getBoolean("WorldPortalProtection", true)) {
			if(MaterialHelper.isTeleportable(block)) {
				if(plugin.isWorldPortal(world, block)) {
					player.sendMessage(plugin.getMessage("noDestroyPermissionMessage", "&e[WorldPortal] &4Don't destroy a WorldPortal!"));
					event.setCancelled(true);
				}
			}
		}
	}
}
