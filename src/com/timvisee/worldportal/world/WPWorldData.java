package com.timvisee.worldportal.world;

import org.bukkit.configuration.ConfigurationSection;

import com.timvisee.worldportal.WorldPortal;

public class WPWorldData {
	
	private String worldName;
	private String dispName;
	
	/**
	 * Constructor
	 * @param worldName Name of the world
	 * @param dispName Display name of the world
	 */
	public WPWorldData(String worldName, String dispName) {
		this.worldName = worldName;
		this.dispName = dispName;
	}
	
	/**
	 * Get the name of the world
	 * @return World name
	 */
	public String getWorldName() {
		return this.worldName;
	}
	
	/**
	 * Get the world
	 * @return WPWorld instance, null if the WPWorld instance isn't loaded
	 */
	public WPWorld getWorld() {
		return WorldPortal.instance.getWorldManager().getWorld(this.worldName);
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
	 * Load world data from a configuration section
	 * @param c The configuration section to load the world data from
	 * @param worldName The name of the world
	 * @return The loaded world data, null if failed
	 */
	public static WPWorldData load(ConfigurationSection c, String worldName) {
		// Make sure the configuration section isn't null
		if(c == null)
			return null;
		
		// TODO: Make sure the configuration section contains the required nodes
		
		// TODO: Make sure the required fields are available
		
		// Load the fields from the configuration section
		String dispName = c.getString("dispName", worldName);
		
		// Construct and return the world data
		return new WPWorldData(worldName, dispName);
	}

	/**
	 * Save the world data to a configuration section
	 * @param c Configuration Section to save the world data in
	 * @return False if failed
	 */
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Store the world data in the configuration section
		c.set("dispName", this.dispName);
		
		// World saved, return true
		return true;
	}
}
