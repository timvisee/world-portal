package com.timvisee.worldportal.world;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.util.WPWorldUtils;

public class WPWorldDataManager {
	
	private HashMap<String, WPWorldData> worldsData = new HashMap<String, WPWorldData>(); 
	
	/**
	 * Get the world data file for a specific world
	 * @param worldName The world to get the data file for
	 * @return The data file for the world, or null if the world is invalid
	 */
	public File getWorldDataFile(World w) {
		// Make sure the world isn't null
		if(w == null)
			return null;
		
		// Get the world data file and return the result
		return getWorldDataFile(w.getName());
	}
	
	/**
	 * Get the world data file for a specific world
	 * @param worldName The name of the world to get the data file for
	 * @return The data file for the world, or null if the world name is invalid
	 */
	public File getWorldDataFile(String worldName) {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return null;
		
		// Get the world folder
		File w = WPWorldUtils.getWorldFolder(worldName);
		
		// Get and return the data file
		return new File(w, "WorldPortal/data/worlddata.yml");
	}
	
	/**
	 * Check whether any world data is loaded for a specific world,
	 * doesn't check if the world data for this world exists
	 * @param worldName The world to check for
	 * @return True if the data is loaded, false otherwise
	 */
	public boolean isLoaded(World w) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Check if the data file for this world is loaded and return the result
		return isLoaded(w.getName());
	}
	
	/**
	 * Check whether any world data is loaded for a specific world,
	 * doesn't check if the world data for this world exists
	 * @param worldName The name of the world to check for
	 * @return True if the data is loaded, false otherwise
	 */
	public boolean isLoaded(String worldName) {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return false;
		
		// Check if any data is loaded for this world and return the result
		return this.worldsData.containsKey(worldName);
	}
	
	/**
	 * Check whether the data file of a world exists
	 * @param w The world to check for
	 * @return True if the data file for this world exists, false otherwise
	 */
	public boolean exists(World w) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Check if the data file for this world exists and return the result
		return exists(w.getName());
	}
	
	/**
	 * Check whether the data file of a world exists
	 * @param w The name of the world to check for
	 * @return True if the data file for this world exists, false otherwise
	 */
	public boolean exists(String worldName) {
		// Make sure the name of the world is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return false;
		
		// Get the data file
		File wd = getWorldDataFile(worldName);
		
		// TODO: Check if the file contents are valid
		
		// Check if this file exists and return the result
		return wd.exists();
	}
	
	/**
	 * Get world data for a specific world, the world data must be loaded already
	 * @param w The world to get the world data for
	 * @return The world data, null if there's not world data loaded for this world
	 */
	public WPWorldData getWorldData(World w) {
		// Make sure the world isn't null
		if(w == null)
			return null;
		
		// Get and return the world data
		return getWorldData(w.getName());
	}
	
	/**
	 * Get world data for a specific world, the world data must be loaded already
	 * @param w The name of the world to get the world data for
	 * @return The world data, null if there's not world data loaded for this world
	 */
	public WPWorldData getWorldData(String worldName) {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return null;
		
		// Make sure the data for this world is loaded
		if(!isLoaded(worldName))
			return null;
		
		// Get and return the data
		return this.worldsData.get(worldName);
	}
	
	/**
	 * Return a hashmap of all loaded world data
	 * @return Hashmap with all loaded world data
	 */
	public HashMap<String, WPWorldData> getLoadedWorldData() {
		return this.worldsData;
	}
	
	/**
	 * Get the count of loaded world data
	 * @return Count of loaded world data
	 */
	public int getLoadedWorldDataCount() {
		return this.worldsData.size();
	}
	
	/**
	 * Unload the world data for a specific world
	 * @param w The world to unload the data for
	 * @param saveData True to save the data before unloading
	 * @param showStatus True to show saving status in the console
	 * @return False if no data was loaded for this world or if the world was wrong
	 */
	public boolean unload(World w, boolean saveData, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Unload the data for this world and return the result
		return unload(w.getName(), saveData, showStatus);
	}
	
	/**
	 * Unload the world data for a specific world
	 * @param worldName The name of the world to unload the data for
	 * @param saveData True to save the data before unloading
	 * @param showStatus True to show saving status in the console
	 * @return False if no data was loaded for this world or if the world was wrong
	 */
	public boolean unload(String worldName, boolean saveData, boolean showStatus) {
		// Check if the data should be saved first
		if(saveData)
			save(worldName, showStatus);
		
		// Unload the world
		return (this.worldsData.remove(worldName) == null);
	}
	
	/**
	 * Unload all loaded world data
	 * @param saveData True to save the data before unloading
	 * @return Count of unloaded world data
	 */
	public int unloadAll(boolean saveData, boolean showStatus) {
		// Show status if required
		if(saveData && showStatus)
			WorldPortal.instance.getWPLogger().info("Saving all loaded world data...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Get the amount of loaded world data
		int count = this.worldsData.size();
		
		// Unload each loaded world data
		for(String worldName : this.worldsData.keySet())
			unload(worldName, saveData, false);
		
		// Calculate the save duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(saveData && showStatus)
			WorldPortal.instance.getWPLogger().info("All world data saved, took " + String.valueOf(duration) + " ms!");
		
		// Return the count of unloaded world data
		return count;
	}
	
	/**
	 * Load the world data for a specific world from the default file
	 * @param w The world to load the world data for
	 * @param showStatus True to show loading status in the console
	 * @return False if failed
	 */
	public boolean load(World w, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Load the world data and return the result
		return load(w.getName(), showStatus);
	}
	
	/**
	 * Load the world data for a specific world from the default file
	 * @param worldName The name of the world to load the world data for
	 * @param showStatus True to show loading status in the console
	 * @return False if failed
	 */
	public boolean load(String worldName, boolean showStatus) {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return false;
		
		// Load the data and return the result
		return load(worldName, getWorldDataFile(worldName), showStatus);
	}
	
	/**
	 * Load the world data for a specific world from a file
	 * @param w The world to load the world data for
	 * @param f The file to load the world data from
	 * @param showStatus True to show loading status in the console
	 * @return False if failed
	 */
	public boolean load(World w, File f, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Load the world data and return the result
		return load(w.getName(), f, showStatus);
	}
	
	/**
	 * Load the world data for a specific world from a file
	 * @param worldName The name of the world to load the world data for
	 * @param f The file to load the world data from
	 * @param showStatus True to show loading status in the console
	 * @return False if failed
	 */
	public boolean load(String worldName, File f, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Loading world data for '" + worldName + "'...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the file isn't null
		if(f == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed loading world data, invalid file!");
			
			return false;
		}
		
		// Make sure the file exists
		if(!f.exists()) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().info("Failed loading world data, file doesn't exist!");
			
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
			WorldPortal.instance.getWPLogger().error("An error occurred while loading world data for the world '" + worldName + "'!");
			
			// Return false
			return false;
		}
		
		// Make sure the configuration file isn't null
		if(c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().info("Failed loading world data!");
			
			return false;
		}
		
		// Load the world data
		boolean result = load(worldName, c, false);
		
		// Calculate the load time
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus) {
			if(result)
				WorldPortal.instance.getWPLogger().info("World data successfully loaded, took " + String.valueOf(duration) + " ms!");
			else
				WorldPortal.instance.getWPLogger().info("Failed loading world data!");
		}
		
		// Return the result
		return load(worldName, c, false);
	}
	
	/**
	 * Load the world data for a specific world
	 * @param w The world to load the world data for
	 * @param c The configuration to load the world data from
	 * @param showStatus True to show loading status in the console
	 * @return False if failed
	 */
	public boolean load(World w, YamlConfiguration c, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Load the world data and return the result
		return load(w.getName(), c, showStatus);
	}
	
	/**
	 * Load the world data for a specific world
	 * @param worldName The name of the world to load the world data for
	 * @param c The configuration to load the world data from
	 * @param showStatus True to show loading status in the console
	 * @return False if failed
	 */
	public boolean load(String worldName, YamlConfiguration c, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Loading world data for '" + worldName + "'...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the configuration isn't null
		if(c == null) {// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Loading world data failed, invalid configuration!");
			
			return false;
		}
		
		// Make sure the worlds section exists
		if(!c.isConfigurationSection("worlddata")) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Loading world data failed, invalid configuration!");
			
			return false;
		}
		
		// Select the right configuration section
		ConfigurationSection worldDataSect = c.getConfigurationSection("worlddata");
		
		// Load the world data
		boolean result = load(worldName, worldDataSect, false);
		
		// Calculate the loading duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus) {
			if(result)
				WorldPortal.instance.getWPLogger().info("World data successfully loaded, took " + String.valueOf(duration) + " ms!");
			else
				WorldPortal.instance.getWPLogger().error("Failed loading world data!");
		}
		
		// Return the result
		return result;
	}
	
	/**
	 * Load the world data for a specific world
	 * @param w The world to load the world data for
	 * @param c The configuration section to load the world data from
	 * @param showStatus True to show loading status in the console
	 * @return False if failed
	 */
	public boolean load(World w, ConfigurationSection c, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Load the world data and return the result
		return load(w.getName(), c, showStatus);
	}
	
	/**
	 * Load the world data for a specific world
	 * @param worldName The name of the world to load the world data for
	 * @param c The configuration section to load the world data from
	 * @param showStatus True to show loading status in the console
	 * @return False if failed
	 */
	public boolean load(String worldName, ConfigurationSection c, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Loading world data for '" + worldName + "'...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the world name is valid and also make sure the configuration section isn't null
		if(!WPWorldUtils.isValidWorldName(worldName) || c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Error while loading world data, invalid world name or configuration section!");
			
			return false;
		}
		
		// Load the world
		WPWorldData wd = WPWorldData.load(c, worldName);
		
		// Make sure the loaded data isn't null
		if(wd == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed to load world data!");
			
			return false;
		}
		
		// Unload any world data for this world if it's already loaded
		unload(worldName, false, false);
		
		// Add the loaded data to the worldData list
		this.worldsData.put(worldName, wd);
		
		// Calculate the load duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("World data successfully loaded, took " + String.valueOf(duration) + " ms!");
		
		// Loaded successfully, return true
		return true;
	}

	/**
	 * Save the world data for a specific world into the default file
	 * @param w The world to save the data for
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean save(World w, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Save the data and return the result
		return save(w.getName(), showStatus);
	}
	
	/**
	 * Save the world data for a specific world into the default file
	 * @param worldName The name of the world to save the data for
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean save(String worldName, boolean showStatus) {
		return save(worldName, getWorldDataFile(worldName), showStatus);
	}

	/**
	 * Save the world data of a specific world to a file
	 * @param w The world to save the data for
	 * @param f The file to save the world data in
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean save(World w, File f, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Save the data and return the result
		return save(w.getName(), f, showStatus);
	}
	
	/**
	 * Save the world data of a specific world to a file
	 * @param worldName The name of the world to save the data for
	 * @param f The file to save the world data in
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean save(String worldName, File f, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saving world data for '" + worldName + "'...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the file isn't null
		if(f == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed saving world data, invalid file!");
			
			return false;
		}
		
		// Construct a YAML configuration to store the worlds in
		YamlConfiguration c = new YamlConfiguration();
		
		// Add the worlds to he YAML configuration
		if(!save(worldName, c, false)) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed saving world data!");
			
			return false;
		}
		
		// Save the YAML configuration
		try {
			c.save(f);
			
		} catch (IOException ex) {
			// Print the stack trace
			ex.printStackTrace();
			
			// Show a status message
			WorldPortal.instance.getWPLogger().error("An error occurred while saving the world data for the world '" + worldName + "'!");
			
			// Return false
			return false;
		}
		
		// Calculate the save duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("World data successfully saved, took " + String.valueOf(duration) + " ms!");
		
		// Saved successfully, return true
		return true;
	}
	
	/**
	 * Save the world data of a specific world
	 * @param w The world to save the data for
	 * @param c The configuration to save the worlds data in
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean save(World w, YamlConfiguration c, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Save the data and return the result
		return save(w.getName(), c, showStatus);
	}
	
	/**
	 * Save the world data of a specific world
	 * @param worldName The name of the world to save the data for
	 * @param c The configuration to save the worlds data in
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean save(String worldName, YamlConfiguration c, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saving world data for '" + worldName + "'...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the YAML configuration isn't null
		if(c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed saving world data, invalid configuration!");
			
			return false;
		}
					
		// Create a configuration section to save the poratls in
		ConfigurationSection worldsSect = c.createSection("worlddata");
		
		// Save the worlds data in the configuration section
		boolean result = save(worldName, worldsSect, false);
		
		// Add the current version number of World Portal to the YAML configuration
		c.set("version", WorldPortal.instance.getVersion());
		
		// Calculate the duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus) {
			if(result)
				WorldPortal.instance.getWPLogger().info("World data saved, took " + String.valueOf(duration) + " ms!");
			else
				WorldPortal.instance.getWPLogger().error("Failed saving world data!");
		}
		
		// Return the result
		return result;
	}
	
	/**
	 * Save the world data of a specific world
	 * @param w The world to save the data for
	 * @param c The configuration section to save the worlds data in
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean save(World w, ConfigurationSection c, boolean showStatus) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Save the data and return the result
		return save(w.getName(), c, showStatus);
	}
	
	/**
	 * Save the world data of a specific world
	 * @param worldName The name of the world to save the data for
	 * @param c The configuration section to save the worlds data in
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean save(String worldName, ConfigurationSection c, boolean showStatus) {
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saving world data for '" + worldName + "'...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Make sure the name of the world is valid and make sure the configuration section isn't null
		if(!WPWorldUtils.isValidWorldName(worldName) || c == null) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed saving world data, invalid world or configuration section!");
			
			return false;
		}
		
		// Make sure the data for the world is loaded
		if(!isLoaded(worldName)) {
			// Show status if required
			if(showStatus)
				WorldPortal.instance.getWPLogger().error("Failed saving world data, no data loaded for this world!");
			
			return false;
		}
		
		// Get the world data to save
		WPWorldData wd = getWorldData(worldName);
		
		// Save the data
		wd.save(c);
		
		// Calculate the save duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().error("World data successfully saved, took " + String.valueOf(duration) + " ms!");
		
		// Saved successfully, return true
		return true;
	}
	
	/**
	 * Save all the loaded world data
	 * @param showStatus True to show saving status in the console
	 * @return False if failed
	 */
	public boolean saveAll(boolean showStatus) {
		// Show a status message if required
		if(showStatus)
			WorldPortal.instance.getWPLogger().info("Saving all loaded world data...");
		
		// Store the current time
		long t = System.currentTimeMillis();
		
		// Define a result variable
		boolean failed = false;
		
		// Save each loaded world data
		for(String worldName : this.worldsData.keySet())
			if(!save(worldName, false))
				failed = true;
		
		// Calculate the duration
		long duration = System.currentTimeMillis() - t;
		
		// Show a status message if required
		if(showStatus) {
			if(!failed)
				WorldPortal.instance.getWPLogger().info("Saved all world data, took " + String.valueOf(duration) + " ms!");
			else
				WorldPortal.instance.getWPLogger().error("Failed saving all world data!");
		}
		
		// Return false if failed
		return (!failed);
	}
}
