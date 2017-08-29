package com.timvisee.worldportal.point;

import org.bukkit.configuration.ConfigurationSection;

public class WPPoint {
	
	String name;
	String dispName;
	
	/**
	 * Constructor
	 * @param name Point name
	 * @param dispName Point display name
	 */
	public WPPoint(String name, String dispName) {
		this.name = name;
		this.dispName = dispName;
	}
	
	/**
	 * Get the name of the point
	 * @return Point name
	 */
	public String getName() {
		return this.name; 
	}
	
	/**
	 * Set the name of the point
	 * @param name Point name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the display name of the point
	 * @return Point display name
	 */
	public String getDisplayName() {
		return this.dispName;
	}
	
	/**
	 * Set the display name of the point
	 * @param dispName Point display name
	 */
	public void setDisplayName(String dispName) {
		this.dispName = dispName;
	}
	
	/**
	 * Load a point from a configuration section
	 * @param c The configuration section to load the point from
	 * @return The loaded point, null if failed
	 */
	public static WPPoint load(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return null;
		
		// TODO: Make sure the configuration section contains the required nodes
		
		// Make sure the required fields are available
		if(!c.isString("name"))
			return null;
		
		// Load the fields from the configuration section
		String name = c.getString("name");
		String dispName = c.getString("dispName", name);
		
		// Construct and return the point
		return new WPPoint(name, dispName);
	}
	
	/**
	 * Save the point to a configuration section
	 * @param c Configuration Section to save the point in
	 * @return False if failed
	 */
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Store the point in the configuration section
		c.set("name", this.name);
		c.set("dispName", this.dispName);
		
		// Point saved, return true
		return true;
	}
}
