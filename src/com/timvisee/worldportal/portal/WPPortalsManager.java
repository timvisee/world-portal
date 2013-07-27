package com.timvisee.worldportal.portal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.timvisee.worldportal.WorldPortal;

public class WPPortalsManager {
	
	private List<WPPortal> portals = new ArrayList<WPPortal>();
	
	/**
	 * Constructor
	 */
	public WPPortalsManager() { }
	
	/**
	 * Get a list of loaded portals
	 * @return List of loaded portals
	 */
	public List<WPPortal> getPortals() {
		return this.portals;
	}
	
	/**
	 * Get the amount of loaded portals
	 * @return Loaded portals
	 */
	public int getPortalsCount() {
		return this.portals.size();
	}
	
	/**
	 * Clear the list of loaded portals, none of the portals will be saved
	 * @return Amount of cleared portals
	 */
	public int clear() {
		// Get the amount of loaded portals
		int count = this.portals.size();
		
		// Clear the list of portals
		this.portals.clear();
		
		// Return the amount of cleared portals
		return count;
	}
	
	/**
	 * Get the portals data file
	 * @return Portals data file
	 */
	public File getPortalsFile() {
		return new File(WorldPortal.instance.getDataFolder(), "data/portals.yml");
	}
	
	/**
	 * Load the portals from the default file
	 * @return False if failed
	 */
	public boolean load() {
		return load(getPortalsFile());
	}
	
	/**
	 * Load the portals from an external file
	 * @param f The file to load the portal from
	 * @return False if failed
	 */
	public boolean load(File f) {
		// Make sure the file isn't null
		if(f == null)
			return false;
		
		// Make sure the file exists
		if(!f.exists())
			return false;
		
		// Try to load the file
		YamlConfiguration c = null;
		try {
			c = YamlConfiguration.loadConfiguration(f);
			
		} catch(Exception ex) {
			// Print the stack trace
			ex.printStackTrace();
			
			// Show an error message
			WorldPortal.instance.getWPLogger().error("An error occurred while loading the portals file!");
			
			// Return false
			return false;
		}
		
		// Make sure the configuration file isn't null
		if(c == null)
			return false;
		
		// Load the poratls
		return load(c);
	}
	
	/**
	 * Load the poratls from a YAML configuration
	 * @param c The YAML configuration to load the portals from
	 * @return False if failed
	 */
	public boolean load(YamlConfiguration c) {
		// Make sure the configuration isn't null
		if(c == null)
			return false;
		
		// Make sure the portals section exists
		if(!c.isConfigurationSection("portals"))
			return false;
		
		// Select the right configuration section
		ConfigurationSection portalsSect = c.getConfigurationSection("portals");
		
		// Load the portals
		return load(portalsSect);
	}
	
	/**
	 * Load the portals from a configuration section
	 * @param c The configuration section to load the portals from
	 * @return False if failed
	 */
	public boolean load(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// List the keys
		Set<String> keys = c.getKeys(false);
		
		// Define a portals buffer list, to put the loaded portals in
		List<WPPortal> buff = new ArrayList<WPPortal>();
		
		// Load each portal
		for(String key : keys) {
			// Get the configuration section
			ConfigurationSection portalSect = c.getConfigurationSection(key);
			
			// Load the portal
			WPPortal p = WPPortal.load(portalSect);
			
			// Make sure the portal isn't null
			if(p == null) {
				WorldPortal.instance.getWPLogger().error("Failed to load portal from file!");
				continue;
			}
			
			// Put the loaded portal into the buffer
			buff.add(p);
		}
		
		// Replace the current portals list with the loaded list
		this.portals.clear();
		this.portals.addAll(buff);
		
		// Loaded successfully, return true
		return true;
	}
	
	/**
	 * Save the current loaded list of portals to the default portals file
	 * @return False if failed
	 */
	public boolean save() {
		return save(getPortalsFile());
	}
	
	/**
	 * Save the current loaded list of portals to a file
	 * @param f The file to save the portals in
	 * @return False if failed
	 */
	public boolean save(File f) {
		// Make sure the file isn't null
		if(f == null)
			return false;
		
		// Construct a YAML configuration to store the portals in
		YamlConfiguration c = new YamlConfiguration();
		
		// Add the portals to he YAML configuration
		if(!save(c))
			return false;
		
		// Save the YAML configuration
		try {
			c.save(f);
			
		} catch (IOException ex) {
			// Print the stack trace
			ex.printStackTrace();
			
			// Show a status message
			WorldPortal.instance.getWPLogger().error("An error occurred while saving the portals!");
			
			// Return false
			return false;
		}
		
		// Saved successfully, return true
		return true;
	}
	
	/**
	 * Save the list of loaded portals in a YAML configuration
	 * @param c The YAML configuration to save the portals in
	 * @return False if failed
	 */
	public boolean save(YamlConfiguration c) {
		// Make sure the YAML configuration isn't null
		if(c == null)
			return false;
					
		// Create a configuration section to save the poratls in
		ConfigurationSection portalsSect = c.createSection("portals");
		
		// Save the portals in the configuration section
		boolean result = save(portalsSect);
		
		// Add the current version number of World Portal to the YAML configuration
		c.set("version", WorldPortal.instance.getVersion());
		
		// Return the result
		return result;
	}
	
	/**
	 * Save the list of loaded portals in a configuration section
	 * @param c The configuration section to save the portals in
	 * @return False if failed
	 */
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Define a counter
		int i = 0;
	
		// Save each portal
		for(WPPortal p : this.portals) {
			// Create a configuration section to store the current portal in
			ConfigurationSection portalSect = c.createSection(String.valueOf(i));
			
			// Save the portal to the section
			p.save(portalSect);
			
			// Increase the counter
			i++;
		}
		
		// Saved successfully, return true
		return true;
	}
}
