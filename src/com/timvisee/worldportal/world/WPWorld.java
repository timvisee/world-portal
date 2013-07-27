package com.timvisee.worldportal.world;

import org.bukkit.configuration.ConfigurationSection;

import com.timvisee.worldportal.WorldPortal;

public class WPWorld {
	
	private String worldName;
	private String dispName;
	
	/**
	 * Constructor
	 * @param worldName World name
	 * @param dispName World display name
	 */
	public WPWorld(String worldName, String dispName) {
		this.worldName = worldName;
		this.dispName = dispName;
	}
	
	/**
	 * Get the name of the world
	 * @return World name
	 */
	public String getName() {
		return this.worldName;
	}
	
	/**
	 * Get the display name of the world
	 * @return World display name
	 */
	public String getDisplayName() {
		return this.dispName;
	}
	
	/**
	 * Set the display name of the world
	 * @param dispName World display name
	 */
	public void setDisplayName(String dispName) {
		this.dispName = dispName;
	}
	
	/**
	 * Check whether this world is loaded
	 * @return True if this world is loaded
	 */
	public boolean isLoaded() {
		return WorldPortal.instance.getWorldManager().isWorldLoaded(this.worldName);
	}
	
	/**
	 * Load a world from a configuration section
	 * @param c The configuration section to load the portal from
	 * @return The loaded world, null if failed
	 */
	public static WPWorld load(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return null;
		
		// TODO: Make sure the configuration section contains the required nodes
		
		// Make sure the required fields are available
		if(!c.isString("worldName"))
			return null;
		
		// Load the fields from the configuration section
		String worldName = c.getString("worldName");
		String dispName = c.getString("dispName", worldName);
		
		// Construct and return the world
		return new WPWorld(worldName, dispName);
	}

	/**
	 * Save the world to a configuration section
	 * @param c Configuration Section to save the world in
	 * @return False if failed
	 */
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Store the world in the configuration section
		c.set("worldName", this.worldName);
		c.set("dispName", this.dispName);
		
		// World saved, return true
		return true;
	}
}
