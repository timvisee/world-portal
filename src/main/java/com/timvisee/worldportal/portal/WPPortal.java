package com.timvisee.worldportal.portal;

import org.bukkit.configuration.ConfigurationSection;

public class WPPortal {
	
	private String name;
	private String dispName;
	
	/**
	 * Constructor
	 * @param name Portal name
	 * @param dispName Portal display name
	 */
	public WPPortal(String name, String dispName) {
		this.name = name;
		this.dispName = dispName;
	}
	
	/**
	 * Get the name of the portal
	 * @return Portal name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set the name of the portal
	 * @param name Portal name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the display name of the portal
	 * @return Portal display name
 	 */
	public String getDisplayName() {
		return this.dispName;
	}
	
	/**
	 * Set the display name of the portal
	 * @param dispName Portal display name
	 */
	public void setDisplayName(String dispName) {
		this.dispName = dispName;
	}
	
	/**
	 * Load a portal from a configuration section
	 * @param c The configuration section to load the portal from
	 * @return The loaded portal, null if failed
	 */
	public static WPPortal load(ConfigurationSection c) {
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
		
		// Construct and return the portal
		return new WPPortal(name, dispName);
	}

	/**
	 * Save the portal to a configuration section
	 * @param c Configuration Section to save the portal in
	 * @return False if failed
	 */
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Store the portal in the configuration section
		c.set("name", this.name);
		c.set("dispName", this.dispName);
		
		// Portal saved, return true
		return true;
	}
}
