package com.timvisee.worldportal.portal;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.timvisee.worldportal.util.WPWorldUtils;

public class WPPortalDestinationLocation extends WPPortalDestination {

	private String worldName;
	private double x, y, z = 0.0;
	
	/**
	 * Constructor
	 * @param worldName Name of the destination world
	 * @param x X coord
	 * @param y Y coord
	 * @param z Z coord
	 */
	public WPPortalDestinationLocation(String worldName, double x, double y, double z) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	/**
	 * Get the X coord
	 * @return X coord
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * Set the X coord
	 * @return X coord
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Get the Y coord
	 * @return Y coord
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * Set the Y coord
	 * @return Y coord
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Get the Z coord
	 * @return Z coord
	 */
	public double getZ() {
		return this.z;
	}

	/**
	 * Set the Z coord
	 * @return Z coord
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	/**
	 * Set the destination
	 * @param l The destination
	 * @return False if the destination is invalid
	 */
	public boolean setDestination(Location l) {
		// Make sure the location isn't null
		if(l == null)
			return false;
		
		// Set the destination
		this.worldName = l.getWorld().getName();
		this.x = l.getX();
		this.y = l.getY();
		this.z = l.getZ();
		
		return true;
	}
	
	/**
	 * Set the destination
	 * @param worldName The name of the destination world
	 * @param x Destination X coord
	 * @param y Destination Y coord
	 * @param z Destination Z coord
	 * @return False if the world name is invalid
	 */
	public boolean setDestination(String worldName, double x, double y, double z) {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return false;
		
		// Set the destination
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		
		return true;
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
		
		// Construct and return the location
		return new Location(w, this.x, this.y, this.z);
	}

	@Override
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Save the data
		c.set("worldName", this.worldName);
		c.set("x", this.x);
		c.set("y", this.y);
		c.set("z", this.z);
		
		return true;
	}
}
