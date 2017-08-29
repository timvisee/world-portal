package com.timvisee.worldportal;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

public class CreatePortal {
	public static Logger log = Logger.getLogger("Minecraft");
	public static WorldPortal plugin;
	public static PlayerListener playerListener;

	public final HashMap<Player, Integer> wpCreateUsers = new HashMap<Player, Integer>();
	
	public final HashMap<Player, World> wpCreatingWorldPortalWorld = new HashMap<Player, World>(); 
	public final HashMap<Player, Location> wpCreatingWorldPortalLocation = new HashMap<Player, Location>(); 
	public final HashMap<Player, String> wpCreatingWorldPortalLinkedWorld = new HashMap<Player, String>(); 
	public final HashMap<Player, Environment> wpCreatingWorldPortalWorldEnvironment = new HashMap<Player, Environment>(); 
	public final HashMap<Player, String> wpCreatingWorldPortalWorldSpawnLocation = new HashMap<Player, String>(); 
	public final HashMap<Player, Integer> wpCreatingWorldPortalLookingDirection = new HashMap<Player, Integer>(); 

	public CreatePortal(WorldPortal instance) {
		plugin = instance;
	}
	public CreatePortal(PlayerListener instance) {
		playerListener = instance;
	}
	
	public void toggleWPUsers(Player player) {
		if (wpCreateUsers.containsKey(player)) {
			// Disable creation mode
			wpCreateUsers.remove(player);
			player.sendMessage(plugin.getMessage("createModeDisabled", "&e[WorldPortal] Creation-mode &4disabled"));
		} else {
			// Enable creation mode
			if(plugin.WPRemoveUsersEnabled(player)) {
				plugin.toggleWPRemoveUsers(player);
			}
			wpCreateUsers.put(player, 0);
			String[] defaultMessages = {"&e[WorldPortal] Creation-mode &aenabled", "&e[WorldPortal] Right-click on a object to create a WorldPortal",
					"&e[WorldPortal] Select a &fSign&e, &fLever&e, &fPressureplate&e or a &fbutton", "&e[WorldPortal] Use &f/wp createstop&e to disable the creationmode"};
			plugin.sendMessageList(player, "createModeEnabled", Arrays.asList(defaultMessages));
		}
	}
	
	public boolean isPlayerInCreationMode(Player player) {
		return wpCreateUsers.containsKey(player);
	}
	
	public void setWPUsersValue(Player player, Integer value) {
		if (wpCreateUsers.containsKey(player)) {
			// Change value
			wpCreateUsers.remove(player);
			wpCreateUsers.put(player, value);
		}
	}
	
	public int getWPUsersValue(Player player) {
		if (wpCreateUsers.containsKey(player)) {
			// Change value
			return wpCreateUsers.get(player);
		}
		return 0;
	}
	
	public void CreatePortalSelectNewPortal(Player player, Block newPortalBlock) {
		if(!plugin.isWorldPortal(newPortalBlock.getWorld(), newPortalBlock)) {
			// Create a new world portal
			if(wpCreatingWorldPortalWorld.containsKey(player)) {
				wpCreatingWorldPortalWorld.remove(player);
			}
			wpCreatingWorldPortalWorld.put(player, newPortalBlock.getWorld());
	
			if(wpCreatingWorldPortalLocation.containsKey(player)) {
				wpCreatingWorldPortalLocation.remove(player);
			}
			wpCreatingWorldPortalLocation.put(player, newPortalBlock.getLocation());
	
			// Set user creation step
			setWPUsersValue(player, 1);
			// Send message to the player who creates the portal
			player.sendMessage(plugin.getMessage("newWorldPortalSelectedMessage", "&e[WorldPortal] &aNew world portal selected"));
			player.sendMessage(plugin.getMessage("selectNewWorldMessage", "&e[WorldPortal] Which world should be linked? Enter the world name into the chat. It can be a new world"));
		} else {
			player.sendMessage(plugin.getMessage("worldPortalAlreadyLinkedMessage", "&e[WorldPortal] &4This already is a world portal!"));
		}
	}
	
	public void CreatePortalLinkWorld(Player player, String worldName) {
		if(wpCreatingWorldPortalLinkedWorld.containsKey(player)) {
			wpCreatingWorldPortalLinkedWorld.remove(player);
		}
		wpCreatingWorldPortalLinkedWorld.put(player, worldName);
		
		// Send the player a succesfully linked message
		player.sendMessage("");
		String message = plugin.getMessage("worldPortalWorldLinkedMessage", "e[WorldPortal] World '&f%worldname%&e' linked");
		player.sendMessage(message.replaceAll("%worldname%", worldName));
		
		// Check if the user entered a existing world,
		if(plugin.worldExists(worldName)) {
			// Skip selecting new environment, because you use an existing world :)
			if(wpCreatingWorldPortalWorldEnvironment.containsKey(player)) {
				wpCreatingWorldPortalWorldEnvironment.remove(player);
			}
			
			setWPUsersValue(player, 3);
			String[] defaultMessages = {"&e[WorldPortal] Select the spawn point in the linked world. Enter the value in the chat.", "&e[WorldPortal] Chose from &fspawn&e, &fhere&e, &f<X> <Z>&e or &f<X> <Y> <Z>"};
			plugin.sendMessageList(player, "selectNewSpawnMessage", Arrays.asList(defaultMessages));
		} else {
			// Select new world environment
			setWPUsersValue(player, 2);
			String[] defaultMessages = {"&e[WorldPortal] Select the environment for the new world. Enter the environment type in the chat.", "&e[WorldPortal] Chose from &fnormal&e, &fnether&e or &fend"};
			plugin.sendMessageList(player, "selectNewEnvironmentMessage", Arrays.asList(defaultMessages));
		}
	}
	
	public void CreatePortalSelectEnvironment(Player player, String environment) {
		if(environment.equalsIgnoreCase("normal") || environment.equalsIgnoreCase("nether") || environment.equalsIgnoreCase("end")) {
			if(wpCreatingWorldPortalWorldEnvironment.containsKey(player)) {
				wpCreatingWorldPortalWorldEnvironment.remove(player);
			}
			if(environment.equalsIgnoreCase("normal")) { wpCreatingWorldPortalWorldEnvironment.put(player, Environment.NORMAL); }
			if(environment.equalsIgnoreCase("nether")) { wpCreatingWorldPortalWorldEnvironment.put(player, Environment.NETHER); }
			if(environment.equalsIgnoreCase("end")) { wpCreatingWorldPortalWorldEnvironment.put(player, Environment.THE_END); }
			
			// Environment set
			player.sendMessage(plugin.getMessage("worldPortalEnvironmentSelected", "&e[WorldPortal] &aEnvironment succesfully setS"));
			player.sendMessage(plugin.getMessage("worldPortalCreateGenerateWorld", "&e[WorldPortal] Generating world, this may take some time. Please wait for a completion message."));
			String newWorldName = wpCreatingWorldPortalLinkedWorld.get(player);
			Environment newWorldEnvironment = wpCreatingWorldPortalWorldEnvironment.get(player);
			if(plugin.worldExists(newWorldName) == false) {
				// No world with this name, create one
				plugin.createWorld(newWorldName, newWorldEnvironment, true);
			}
			player.sendMessage(plugin.getMessage("worldPortalCreateGenerateWorldDone", "&e[WorldPortal] &aWorld succesfully created"));
			setWPUsersValue(player, 3);
			String[] defaultMessages = {"&e[WorldPortal] Select the spawn point in the linked world. Enter the value in the chat.", "&e[WorldPortal] Chose from &fspawn&e, &fhere&e, &f<X> <Z>&e or &f<X> <Y> <Z>"};
			plugin.sendMessageList(player, "selectNewSpawnMessage", Arrays.asList(defaultMessages));
		} else {
			// Wrong environment, show message
			player.sendMessage(ChatColor.DARK_RED + environment);
			String[] defaultMessages = {"&e[WorldPortal] &4Invalid environment!", "&e[WorldPortal] Chose from &fnormal&e, &fnether&e or &fend"};
			plugin.sendMessageList(player, "selectedInvalidEvironment", Arrays.asList(defaultMessages));
		}
	}
	
	public void CreatePortalSelectSpawnPoint(Player player, String spawnValues) {
		// Spawn set to linked world spawn
		if(wpCreatingWorldPortalWorldSpawnLocation.containsKey(player)) {
			wpCreatingWorldPortalWorldSpawnLocation.remove(player);
		}
		
		wpCreatingWorldPortalWorldSpawnLocation.put(player, spawnValues);
		
		setWPUsersValue(player, 4);
		
		player.sendMessage(plugin.getMessage("selectedWorldPortalSpawn", "&e[WorldPortal] &aThe spawnpoint has been set"));
		player.sendMessage("");
		player.sendMessage(plugin.getMessage("####################", "&e[WorldPortal] &eSet the looking direction from 0 to 360 degrees"));
		player.sendMessage(plugin.getMessage("####################", "&e[WorldPortal] &eof the player"));
		player.sendMessage(plugin.getMessage("####################", "&e[WorldPortal] &eEnter the degrees into the chat"));
	}
	
	public void CreatePortalSelectLookingDirection(Player player, int lookingDirection) {
		if(lookingDirection < 0) {
			// Wrong looking direction, show message
			player.sendMessage(ChatColor.DARK_RED + String.valueOf(lookingDirection));
			String[] defaultMessages = {"&e[WorldPortal] &4Invalid looking direction!", "&e[WorldPortal] Choose from &f0&e to &f360"};
			plugin.sendMessageList(player, "####################", Arrays.asList(defaultMessages));
		} else if(lookingDirection > 360) {
			// Wrong looking direction, show message
			player.sendMessage(ChatColor.DARK_RED + String.valueOf(lookingDirection));
			String[] defaultMessages = {"&e[WorldPortal] &4Invalid looking direction!", "&e[WorldPortal] Choose from &f0&e to &f360"};
			plugin.sendMessageList(player, "####################", Arrays.asList(defaultMessages));
		} else {
			@SuppressWarnings("unused")
			World linkedWorld = plugin.getServer().getWorld(wpCreatingWorldPortalLinkedWorld.get(player));
			
			// Spawn set to linked world spawn
			if(wpCreatingWorldPortalLookingDirection.containsKey(player)) {
				wpCreatingWorldPortalLookingDirection.remove(player);
			}
			
			wpCreatingWorldPortalLookingDirection.put(player, lookingDirection);
			
			AddCreatedWorldPortal(wpCreatingWorldPortalWorld.get(player),wpCreatingWorldPortalLocation.get(player),wpCreatingWorldPortalLinkedWorld.get(player),wpCreatingWorldPortalWorldSpawnLocation.get(player), lookingDirection);
			
			player.sendMessage(plugin.getMessage("####################", "&e[WorldPortal] &aThe looking-direction has been set"));
			player.sendMessage(plugin.getMessage("####################", "&e[WorldPortal] &aWorld portal succesfully created!"));
			// Disable creation mode
			toggleWPUsers(player);
		}
	}
	
	public void AddCreatedWorldPortal(World world, Location worldPortalLocation, String linkedWorld, String linkedWorldSpawnLocation, int worldPortalLookingDirection) {
		String worldString = world.getName();
		String worldPortalLocationString = Integer.toString(worldPortalLocation.getBlockX())+" "+Integer.toString(worldPortalLocation.getBlockY())+" "+Integer.toString(worldPortalLocation.getBlockZ());
		String linkedWorldString = linkedWorld;
		String linkedWorldSpawnLocationString = linkedWorldSpawnLocation;
		String linkedWorldLookingDirection = String.valueOf(worldPortalLookingDirection);
		
		String newWorldPortalString = worldString + "|" + worldPortalLocationString + "|" + linkedWorldString + "|" + linkedWorldSpawnLocationString + "|" + linkedWorldLookingDirection;
		plugin.worldPortals.add(newWorldPortalString);
		
		plugin.saveWorldPortals();
	}
}
