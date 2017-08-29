package me.timvisee.WorldPortal;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class WorldPortalBlockListener implements Listener {
	public static WorldPortal plugin;

	public WorldPortalBlockListener(WorldPortal instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		World world = event.getBlock().getWorld();
		Block block = event.getBlock();
		Player player = event.getPlayer();
	
		if(plugin.getConfig().getBoolean("WorldPortalProtection", true)) {
			int id = block.getTypeId();
			if(id == 63 || id == 68 || id == 69 || id == 70 || id == 72 || id == 77) {
				if(plugin.isWorldPortal(world, block)) {
					event.setCancelled(true);
					player.sendMessage(plugin.getMessage("noDestroyPermissionMessage", "&e[World Portal] &4Don't destroy a World Portal!"));
					event.setCancelled(true);
				}
			}
		}
	}
}
