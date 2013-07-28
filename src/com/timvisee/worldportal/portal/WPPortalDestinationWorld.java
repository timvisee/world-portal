package com.timvisee.worldportal.portal;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.timvisee.worldportal.util.WPWorldUtils;

public class WPPortalDestinationWorld extends WPPortalDestination {

	private String worldName;
	
	/**
	 * Constructor
	 * @param worldName Name of the destination world
	 */
	public WPPortalDestinationWorld(String worldName) {
		this.worldName = worldName;
	}
	
	/**
	 * Get the name of the destination world
	 * @return Name of the destination world
	 */
	public String getWorldName() {
		return this.worldName;
	}
	
	/**
	 * Get the world instance
	 * @return World instance, or null if the instance couldn't be retrieved
	 */
	public World getWorld() {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(this.worldName))
			return null;
		
		// Make sure the world is loaded
		if(!WPWorldUtils.isWorldLoaded(this.worldName))
			return null;
		
		// Get and return the world instance
		return WPWorldUtils.getWorld(this.worldName);
	}
	
	/**
	 * Set the destination world
	 * @param w The destination world
	 * @return False if the world is invalid
	 */
	public boolean setWorld(World w) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Set the world and return the result
		return setWorld(w.getName());
	}
	
	/**
	 * Set the destination world
	 * @param worldName The name of the destination world
	 * @return False if the world name is invalid
	 */
	public boolean setWorld(String worldName) {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return false;
		
		// Set the world name
		this.worldName = worldName;
		
		return true;
	}
	
	/**
	 * Check whether the destination world is loaded
	 * @return True if the destination world is loaded
	 */
	public boolean isDestinationWorldLoaded() {
		return WPWorldUtils.isWorldLoaded(this.worldName);
	}
	
	@Override
	public boolean teleport(Player p, TeleportCause cause) {
		// Make sure the player isn't null
		if(p == null)
			return false;
		
		// Try to get the destination location
		Location l = getLocation();
		
		// Make sure the location isn't null
		if(l == null)
			return false;
		
		// Teleport the player to the destination location
		p.teleport(l, cause);
		
		return true;
	}

	@Override
	public Location getLocation() {
		// Try to retrieve the world
		World w = getWorld();
		
		// Make sure the world isn't null
		if(w == null)
			return null;
		
		// Return the spawn location of the world
		return w.getSpawnLocation();
	}

	@Override
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Save the data
		c.set("worldName", this.worldName);
		
		return true;
	}
}
