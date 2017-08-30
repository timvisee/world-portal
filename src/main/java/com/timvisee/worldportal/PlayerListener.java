package com.timvisee.worldportal;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.util.Arrays;
import java.util.logging.Logger;

public class PlayerListener implements Listener {
	public static Logger log = Logger.getLogger("Minecraft");
	public static WorldPortal plugin;

	public PlayerListener(WorldPortal instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if(MaterialHelper.isTeleportable(block)) {
				if(plugin.isWorldPortal(player.getWorld(), block)) {
					plugin.doWorldPortal(player, block);
					event.setCancelled(true);
				}
			}
		}
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();

			if(plugin.createPortal.isPlayerInCreationMode(player)) {
				if(MaterialHelper.isTeleportable(block)) {
					if(plugin.createPortal.getWPUsersValue(player) == 0) {
						plugin.createPortal.createPortalSelectNewPortal(player, block);
						event.setCancelled(true);
					}
				}

			} else if(plugin.WPRemoveUsersEnabled(player)) {
				if(MaterialHelper.isTeleportable(block)) {
					if(plugin.isWorldPortal(player.getWorld(), block)) {
						plugin.removeWorldPortal(block, false);
						player.sendMessage(plugin.getMessage("worldPortalRemovedMessage", "&e[WorldPortal] &aWorld Portal succesfully unlinked!"));
						event.setCancelled(true);
					}
				}

			} else {
				if(MaterialHelper.isTeleportable(block)) {
					if(plugin.isWorldPortal(player.getWorld(), block)) {
						plugin.doWorldPortal(player, block);
						event.setCancelled(true);
					}
				}
			}
		}

		if(event.getAction() == Action.PHYSICAL) {
			Block block = event.getClickedBlock();
			if(plugin.isWorldPortal(block.getWorld(), block))
                if(MaterialHelper.isTeleportable(block))
                    plugin.doWorldPortal(player, block);
		}
	}

	@EventHandler
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBed();

		// Return early if the bed is not teleportable
		if(!MaterialHelper.isTeleportable(block))
			return;

		// Create portals
        if(plugin.createPortal.isPlayerInCreationMode(player)) {
            if(plugin.createPortal.getWPUsersValue(player) == 0) {
                plugin.createPortal.createPortalSelectNewPortal(player, block);
                event.setCancelled(true);
            }

        } else if(plugin.WPRemoveUsersEnabled(player)) {
			if(plugin.isWorldPortal(player.getWorld(), block)) {
				// Remove portals
				plugin.removeWorldPortal(block, false);
				player.sendMessage(plugin.getMessage("worldPortalRemovedMessage", "&e[WorldPortal] &aWorld Portal succesfully unlinked!"));
				event.setCancelled(true);
			}

        } else if(plugin.isWorldPortal(player.getWorld(), block)) {
            plugin.doWorldPortal(player, block);
            event.setCancelled(true);
        }
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		if(plugin.createPortal.isPlayerInCreationMode(player)) {
			if(plugin.createPortal.getWPUsersValue(player) == 1) {
				plugin.createPortal.createPortalLinkWorld(player, message);
				event.setCancelled(true);
				
			} else if(plugin.createPortal.getWPUsersValue(player) == 2) {
				if(message.equalsIgnoreCase("normal") || message.equalsIgnoreCase("nether") || message.equalsIgnoreCase("end")) {
					plugin.createPortal.createPortalSelectEnvironment(player, message);
					event.setCancelled(true);
				} else {
					player.sendMessage(ChatColor.DARK_RED + message);
					String[] defaultMessages = {"&e[WorldPortal] &4Invalid environment!",
							"&e[WorldPortal] Chose from &fnormal&e, &fnether&e or &fend"};
					plugin.sendMessageList(player, "selectedInvalidEvironment", Arrays.asList(defaultMessages));
					event.setCancelled(true);
				}
				
			} else if(plugin.createPortal.getWPUsersValue(player) == 3) {
				if(message.equalsIgnoreCase("spawn")) {
					plugin.createPortal.createPortalSelectSpawnPoint(player, "spawn");
					event.setCancelled(true);
				} else if(message.equalsIgnoreCase("here")) {
					String playerLoc;
					playerLoc = String.valueOf(player.getLocation().getBlockX()) + " " + String.valueOf(player.getLocation().getBlockY()) + " " + String.valueOf(player.getLocation().getBlockZ());
					plugin.createPortal.createPortalSelectSpawnPoint(player, playerLoc);
					event.setCancelled(true);
				} else {
					boolean error = false;
					String[] values = message.split(" ");
					if(values.length == 2) {
						if(plugin.stringIsInt(values[0]) && plugin.stringIsInt(values[1])) {
							plugin.createPortal.createPortalSelectSpawnPoint(player, message);
						} else {
							error = true;
						}
					} else if(values.length == 3) {
						if(plugin.stringIsInt(values[0]) && plugin.stringIsInt(values[1]) && plugin.stringIsInt(values[2])) {
							plugin.createPortal.createPortalSelectSpawnPoint(player, message);
						} else {
							error = true;
						}
					} else {
						error = true;
					}
					
					if(error) {
						player.sendMessage(ChatColor.DARK_RED + message);
						String[] defaultMessages = {"&e[WorldPortal] &4Invalid spawn point!", "&e[WorldPortal] Choose from &fspawn&e, &f<X> <Z>&e or &f<X> <Y> <Z>"};
						plugin.sendMessageList(player, "selectedInvalidSpawnpoint", Arrays.asList(defaultMessages));
					}
					
					event.setCancelled(true);
				}
				
			} else if(plugin.createPortal.getWPUsersValue(player) == 4) {
				if(plugin.stringIsInt(message)) {
					int lookingDirection = Integer.parseInt(message);
					plugin.createPortal.createPortalSelectLookingDirection(player, lookingDirection);
					event.setCancelled(true);
				} else {
					player.sendMessage(ChatColor.DARK_RED + message);
					String[] defaultMessages = {"&e[WorldPortal] &4Invalid degrees!", "&e[WorldPortal] Choose from 0 to 360"};
					plugin.sendMessageList(player, "selectedInvalidDegrees", Arrays.asList(defaultMessages));
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		Location location = event.getPlayer().getLocation();
		String reason = event.getReason();
		
		if(reason.equalsIgnoreCase("You moved too quickly :( (Hacking?)")) {
			if(plugin.movedTooQuicklyIgnoreList.contains(player.getName())) {
				player.teleport(plugin.getLastTeleportPlayerLocation(player.getName()));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		//plugin.addLastTeleportPlayerLocation(player, location);

		// Return early if not teleported by a portal
		if(event.getCause() != PlayerTeleportEvent.TeleportCause.END_GATEWAY &&
				event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
			return;

		// Find the portal block
		Block block = findPlayerPortalBlock(
				event.getFrom(),
				event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY ?
					Material.ENDER_PORTAL :
					Material.PORTAL
		);
		if(!MaterialHelper.isTeleportable(block))
			return;

		// Check whether the portal block is a world portal
		if(plugin.isWorldPortal(block.getWorld(), block)) {
			plugin.doWorldPortal(event.getPlayer(), block);
			event.setCancelled(true);
		}
	}

	/**
	 * Find the portal block a player used.
	 */
	public Block findPlayerPortalBlock(Location location, Material portalType) {
		// Get the base block
		final Block base = location.getBlock();
		if(base == null)
			return null;

	    // Loop through the x/z grid, in order: 0,1,-1,2,-2
		for(int xRaw = 0; xRaw < 5; xRaw++) {
			for(int zRaw = 0; zRaw < 5; zRaw++) {
                // Find the x and z coordinates to use
                final int x = (xRaw % 2 != 0) ?
						(int) Math.ceil(((double) xRaw) / 2) :
						xRaw / 2 * -1;
				final int z = (zRaw % 2 != 0) ?
						(int) Math.ceil(((double) zRaw) / 2) :
						zRaw / 2 * -1;

				// Go through the layers
				for(int y = 0; y < 3; y++) {
					// We should only search (y + 1) wide at max
					if(Math.abs(x) > y + 1 || Math.abs(z) > y + 1)
						continue;

					// Find the block
					final Block current = base.getRelative(x, y, z);

					// Test whether the block is a candidate
                    if(MaterialHelper.isTeleportable(current))
                        return current;
				}
			}
		}

		// No block was found, return
		return null;
	}
}
