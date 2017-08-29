package me.timvisee.WorldPortal;

import me.timvisee.WorldPortal.WorldPortal;

import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.Location;

public class WorldPortalPlayerListener implements Listener {
	public static Logger log = Logger.getLogger("Minecraft");
	public static WorldPortal plugin;

	public WorldPortalPlayerListener(WorldPortal instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			int blockTypeId = block.getTypeId();
			
			if(blockTypeId == 63 || blockTypeId == 68 || blockTypeId == 69 || blockTypeId == 77) {
				if(plugin.isWorldPortal(player.getWorld(), block)) {
					plugin.doWorldPortal(player, block);
					event.setCancelled(true);
				}
			}
		}
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			int blockTypeId = block.getTypeId();
			
			if(plugin.createPortal.isPlayerInCreationMode(player)) {
				if(blockTypeId == 63 || blockTypeId == 68 || blockTypeId == 69 || blockTypeId == 70 || blockTypeId == 72 || blockTypeId == 77) {
					if(plugin.createPortal.getWPUsersValue(player) == 0) {
						plugin.createPortal.CreatePortalSelectNewPortal(player, block);
						event.setCancelled(true);
					}
				}
			} else if(plugin.WPRemoveUsersEnabled(player)) {
				if(blockTypeId == 63 || blockTypeId == 68 || blockTypeId == 69 || blockTypeId == 70 || blockTypeId == 72 || blockTypeId == 77) {
					plugin.removeWorldPortal(block, false);
					player.sendMessage(plugin.getMessage("worldPortalRemovedMessage", "&e[World Portal] &aWorld Portal succesfully unlinked!"));
					event.setCancelled(true);
				}
			} else {
				if(blockTypeId == 63 || blockTypeId == 68 || blockTypeId == 69 || blockTypeId == 77) {
					if(plugin.isWorldPortal(player.getWorld(), block)) {
						plugin.doWorldPortal(player, block);
						event.setCancelled(true);
					}
				}
			}
		}
		
		if(event.getAction() == Action.PHYSICAL) {
			Block block = event.getClickedBlock();
			int blockTypeId = block.getTypeId();
			
			if(blockTypeId == 70 || blockTypeId == 72) {
				if(plugin.isWorldPortal(player.getWorld(), block)) {
					plugin.doWorldPortal(player, block);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		if(plugin.createPortal.isPlayerInCreationMode(player)) {
			if(plugin.createPortal.getWPUsersValue(player) == 1) {
				plugin.createPortal.CreatePortalLinkWorld(player, message);
				event.setCancelled(true);
				
			} else if(plugin.createPortal.getWPUsersValue(player) == 2) {
				if(message.equalsIgnoreCase("normal") || message.equalsIgnoreCase("nether") || message.equalsIgnoreCase("end")) {
					plugin.createPortal.CreatePortalSelectEnvironment(player, message);
					event.setCancelled(true);
				} else {
					player.sendMessage(ChatColor.DARK_RED + message);
					String[] defaultMessages = {"&e[World Portal] &4Invalid environment!",
							"&e[World Portal] Chose from &fnormal&e, &fnether&e or &fend"};
					plugin.sendMessageList(player, "selectedInvalidEvironment", Arrays.asList(defaultMessages));
					event.setCancelled(true);
				}
				
			} else if(plugin.createPortal.getWPUsersValue(player) == 3) {
				if(message.equalsIgnoreCase("spawn")) {
					plugin.createPortal.CreatePortalSelectSpawnPoint(player, "spawn");
					event.setCancelled(true);
				} else if(message.equalsIgnoreCase("here")) {
					String playerLoc = "";
					playerLoc = String.valueOf(player.getLocation().getBlockX()) + " " + String.valueOf(player.getLocation().getBlockY()) + " " + String.valueOf(player.getLocation().getBlockZ());
					plugin.createPortal.CreatePortalSelectSpawnPoint(player, playerLoc);
					event.setCancelled(true);
				} else {
					boolean error = false;
					String[] values = message.split(" ");
					if(values.length == 2) {
						if(plugin.stringIsInt(values[0].toString()) && plugin.stringIsInt(values[1].toString())) {
							plugin.createPortal.CreatePortalSelectSpawnPoint(player, message);
						} else {
							error = true;
						}
					} else if(values.length == 3) {
						if(plugin.stringIsInt(values[0].toString()) && plugin.stringIsInt(values[1].toString()) && plugin.stringIsInt(values[2].toString())) {
							plugin.createPortal.CreatePortalSelectSpawnPoint(player, message);
						} else {
							error = true;
						}
					} else {
						error = true;
					}
					
					if(error) {
						player.sendMessage(ChatColor.DARK_RED + message);
						String[] defaultMessages = {"&e[World Portal] &4Invalid spawn point!", "&e[World Portal] Choose from &fspawn&e, &f<X> <Z>&e or &f<X> <Y> <Z>"};
						plugin.sendMessageList(player, "selectedInvalidSpawnpoint", Arrays.asList(defaultMessages));
					}
					
					event.setCancelled(true);
				}
				
			} else if(plugin.createPortal.getWPUsersValue(player) == 4) {
				if(plugin.stringIsInt(message)) {
					int lookingDirection = Integer.parseInt(message);
					plugin.createPortal.CreatePortalSelectLookingDirection(player, lookingDirection);
					event.setCancelled(true);
				} else {
					player.sendMessage(ChatColor.DARK_RED + message);
					String[] defaultMessages = {"&e[World Portal] &4Invalid degrees!", "&e[World Portal] Choose from 0 to 360"};
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
		Player player = event.getPlayer();
		Location location = event.getTo();
		
		//plugin.addLastTeleportPlayerLocation(player, location);
	}
}
