package com.timvisee.worldportal.point;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.timvisee.worldportal.WorldPortal;

public class WPPointsManager {
	
	private List<WPPoint> points = new ArrayList<WPPoint>();
	
	/**
	 * Constructor
	 */
	public WPPointsManager() { }
	
	/**
	 * Get a list of loaded points
	 * @return List of loaded points
	 */
	public List<WPPoint> getPoints() {
		return this.points;
	}
	
	/**
	 * Get the amount of loaded points
	 * @return Loaded points
	 */
	public int getPointsCount() {
		return this.points.size();
	}
	
	/**
	 * Clear the list of loaded points, none of the points will be saved
	 * @return Amount of cleared points
	 */
	public int clear() {
		// Get the amount of loaded points
		int count = this.points.size();
		
		// Clear the list of points
		this.points.clear();
		
		// Return the amount of cleared points
		return count;
	}
	
	/**
	 * Get the points data file
	 * @return Points data file
	 */
	public File getPointsFile() {
		return new File(WorldPortal.instance.getDataFolder(), "data/points.yml");
	}
	
	/**
	 * Load the points from the default file
	 * @return False if failed
	 */
	public boolean load() {
		return load(getPointsFile());
	}
	
	/**
	 * Load the points from an external file
	 * @param f The file to load the point from
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
			WorldPortal.instance.getWPLogger().error("An error occurred while loading the points file!");
			
			// Return false
			return false;
		}
		
		// Make sure the configuration file isn't null
		if(c == null)
			return false;
		
		// Load the points
		return load(c);
	}
	
	/**
	 * Load the poratls from a YAML configuration
	 * @param c The YAML configuration to load the points from
	 * @return False if failed
	 */
	public boolean load(YamlConfiguration c) {
		// Make sure the configuration isn't null
		if(c == null)
			return false;
		
		// Make sure the points section exists
		if(!c.isConfigurationSection("points"))
			return false;
		
		// Select the right configuration section
		ConfigurationSection pointsSect = c.getConfigurationSection("points");
		
		// Load the points
		return load(pointsSect);
	}
	
	/**
	 * Load the points from a configuration section
	 * @param c The configuration section to load the points from
	 * @return False if failed
	 */
	public boolean load(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// List the keys
		Set<String> keys = c.getKeys(false);
		
		// Define a points buffer list, to put the loaded points in
		List<WPPoint> buff = new ArrayList<WPPoint>();
		
		// Load each point
		for(String key : keys) {
			// Get the configuration section
			ConfigurationSection pointSect = c.getConfigurationSection(key);
			
			// Load the point
			WPPoint p = WPPoint.load(pointSect);
			
			// Make sure the point isn't null
			if(p == null) {
				WorldPortal.instance.getWPLogger().error("Failed to load point from file!");
				continue;
			}
			
			// Put the loaded point into the buffer
			buff.add(p);
		}
		
		// Replace the current points list with the loaded list
		this.points.clear();
		this.points.addAll(buff);
		
		// Loaded successfully, return true
		return true;
	}
	
	/**
	 * Save the current loaded list of points to the default points file
	 * @return False if failed
	 */
	public boolean save() {
		return save(getPointsFile());
	}
	
	/**
	 * Save the current loaded list of points to a file
	 * @param f The file to save the points in
	 * @return False if failed
	 */
	public boolean save(File f) {
		// Make sure the file isn't null
		if(f == null)
			return false;
		
		// Construct a YAML configuration to store the points in
		YamlConfiguration c = new YamlConfiguration();
		
		// Add the points to he YAML configuration
		if(!save(c))
			return false;
		
		// Save the YAML configuration
		try {
			c.save(f);
			
		} catch (IOException ex) {
			// Print the stack trace
			ex.printStackTrace();
			
			// Show a status message
			WorldPortal.instance.getWPLogger().error("An error occurred while saving the points!");
			
			// Return false
			return false;
		}
		
		// Saved successfully, return true
		return true;
	}
	
	/**
	 * Save the list of loaded points in a YAML configuration
	 * @param c The YAML configuration to save the points in
	 * @return False if failed
	 */
	public boolean save(YamlConfiguration c) {
		// Make sure the YAML configuration isn't null
		if(c == null)
			return false;
					
		// Create a configuration section to save the poratls in
		ConfigurationSection pointsSect = c.createSection("points");
		
		// Save the points in the configuration section
		boolean result = save(pointsSect);
		
		// Add the current version number of World Point to the YAML configuration
		c.set("version", WorldPortal.instance.getVersion());
		
		// Return the result
		return result;
	}
	
	/**
	 * Save the list of loaded points in a configuration section
	 * @param c The configuration section to save the points in
	 * @return False if failed
	 */
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Define a counter
		int i = 0;
	
		// Save each point
		for(WPPoint p : this.points) {
			// Create a configuration section to store the current point in
			ConfigurationSection pointSect = c.createSection(String.valueOf(i));
			
			// Save the point to the section
			p.save(pointSect);
			
			// Increase the counter
			i++;
		}
		
		// Saved successfully, return true
		return true;
	}
}
