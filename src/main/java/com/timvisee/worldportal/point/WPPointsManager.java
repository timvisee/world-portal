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
	 * @param showStatus True to show loading statuses in the console
	 * @return False if failed
	 */
	public boolean load(boolean showStatus) {
		return load(getPointsFile(), showStatus);
	}
	
	/**
	 * Load the points from an external file
	 * @param f The file to load the point from
	 * @param showStatus True to show loading statuses in the console
	 * @return False if failed
	 */
	public boolean load(File f, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Loading points...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the file isn't null
		if(f == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed to load points, invalid file!");
			
			return false;
		}
		
		// Make sure the file exists
		if(!f.exists()) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed to load points, file doesn't exist!");
			
			return false;
		}
		
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
		if(c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed to load points, unable to read file!");
			
			return false;
		}
		
		// Load the points
		boolean result = load(c, false);
		
		// Calculate the loading duration
		long duration = System.currentTimeMillis() - t;

		// Show status if required
		if(showStatus) {
			if(result)
				WorldPortal.instance.getWPLogger().info("Loaded " + String.valueOf(getPointsCount()) + " points, took " + String.valueOf(duration) + " ms!");
			else
				WorldPortal.instance.getWPLogger().error("Failed to load points!");
		}
		
		// Return the result
		return result;
	}
	
	/**
	 * Load the points from a YAML configuration
	 * @param c The YAML configuration to load the points from
	 * @param showStatus True to show loading statuses in the console
	 * @return False if failed
	 */
	public boolean load(YamlConfiguration c, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Loading points...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the configuration isn't null
		if(c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed to load points, invalid configuration!");
			
			return false;
		}
		
		// Make sure the points section exists
		if(!c.isConfigurationSection("points"))
			return false;
		
		// Select the right configuration section
		ConfigurationSection pointsSect = c.getConfigurationSection("points");
		
		// Load the points
		boolean result = load(pointsSect, false);
		
		// Calculate the loading duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus) {
			if(result)
				WorldPortal.instance.getWPLogger().info("Loaded " + String.valueOf(getPointsCount()) + " points, took " + String.valueOf(duration) + " ms!");
			else
				WorldPortal.instance.getWPLogger().error("Failed to load points!");
		}
		
		// Return the result
		return result;
	}
	
	/**
	 * Load the points from a configuration section
	 * @param c The configuration section to load the points from
	 * @param showStatus True to show loading statuses in the console
	 * @return False if failed
	 */
	public boolean load(ConfigurationSection c, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Loading points...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the configuration section isn't null
		if(c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed to load points, invalid configuration section!");
			
			return false;
		}
		
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
		
		// Calculate the loading duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Loaded " + String.valueOf(buff.size()) + " points, took " + String.valueOf(duration) + " ms!");
		
		// Loaded successfully, return true
		return true;
	}
	
	/**
	 * Save the current loaded list of points to the default points file
	 * @param showStatus True to show saving statuses in the console
	 * @return False if failed
	 */
	public boolean save(boolean showStatus) {
		return save(getPointsFile(), showStatus);
	}
	
	/**
	 * Save the current loaded list of points to a file
	 * @param f The file to save the points in
	 * @param showStatus True to show saving statuses in the console
	 * @return False if failed
	 */
	public boolean save(File f, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saving all loaded points...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the file isn't null
		if(f == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Saving failed, invalid file!");
			
			return false;
		}
		
		// Construct a YAML configuration to store the points in
		YamlConfiguration c = new YamlConfiguration();
		
		// Add the points to he YAML configuration
		if(!save(c, false)) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed saving points, an error occurred while writing file!");
			
			return false;
		}
		
		// Save the YAML configuration
		try {
			c.save(f);
			
		} catch (IOException ex) {
			// Print the stack trace
			ex.printStackTrace();
			
			// Show a status message
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed saving points!");
			
			// Return false
			return false;
		}
		
		// Calculate the save duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saved " + String.valueOf(getPointsCount()) + " points, took " + String.valueOf(duration) + " ms!");
		
		// Saved successfully, return true
		return true;
	}
	
	/**
	 * Save the list of loaded points in a YAML configuration
	 * @param c The YAML configuration to save the points in
	 * @param showStatus True to show saving statuses in the console
	 * @return False if failed
	 */
	public boolean save(YamlConfiguration c, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saving all loaded points...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the YAML configuration isn't null
		if(c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Saving failed, invalid configuration!");
			
			return false;
		}
					
		// Create a configuration section to save the poratls in
		ConfigurationSection pointsSect = c.createSection("points");
		
		// Save the points in the configuration section
		boolean result = save(pointsSect, false);
		
		// Add the current version number of World Point to the YAML configuration
		c.set("version", WorldPortal.instance.getVersion());
		
		// Calculate the saving duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus) {
			if(result)
				WorldPortal.instance.getWPLogger().info("Saved " + String.valueOf(getPointsCount()) + " poinst, took " + String.valueOf(duration) + " ms!");
			else
				WorldPortal.instance.getWPLogger().error("Saving failed!");
		}
		
		// Return the result
		return result;
	}
	
	/**
	 * Save the list of loaded points in a configuration section
	 * @param c The configuration section to save the points in
	 * @param showStatus True to show saving statuses in the console
	 * @return False if failed
	 */
	public boolean save(ConfigurationSection c, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saving all loaded points...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the configuration section isn't null
		if(c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed saving, invalid configuration section!");
			
			return false;
		}
		
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
		
		// Calculate the saving duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saved " + String.valueOf(getPointsCount()) + " points, took " + String.valueOf(duration) + " ms!");
		
		// Saved successfully, return true
		return true;
	}
}
