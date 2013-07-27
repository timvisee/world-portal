package com.timvisee.worldportal.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.timvisee.worldportal.WorldPortal;

public class WPWorldManager {
	
	private List<WPWorld> worlds = new ArrayList<WPWorld>(); 
	
	/**
	 * Get the world container folder
	 * @return World container folder
	 */
	public File getWorldContainerFolder() {
		return Bukkit.getWorldContainer();
	}
	
	/**
	 * Returns the world folder of any world, doesn't check if the world exists or if the world name is valid
	 * @param worldName The name of the world to get the folder from
	 * @return World folder
	 */
	public File getWorldFolder(String worldName) {
		return new File(getWorldContainerFolder(), worldName);
	}
	
	/**
	 * Get the level data file of a world, doesn't check if the world exists or if the world name is valid
	 * @param worldName The name of the world to get the folder from
	 * @return Level data file
	 */
	public File getWorldLevelFile(String worldName) {
		return new File(getWorldFolder(worldName), "level.dat");
	}
	
	/**
	 * Get a list of available worlds, both loaded an non-loaded worlds
	 * @return List of available worlds
	 */
	public List<String> getAvailableWorlds() {
		// Get the world container folder
		File wc = getWorldContainerFolder();
		
		// Make sure this world container folder exists
		if(!wc.exists())
			return new ArrayList<String>();
		
		// Create a world buffer to put the available worlds in
		List<String> buff = new ArrayList<String>();
		
		// Check for each folder inside this world container if it's a world
		for(File f : wc.listFiles()) {
			// Make sure the current entry is a folder
			if(!f.isDirectory())
				continue;
			
			// Get the world name
			String worldName = f.getName();
			
			// Make sure any world exists for this world name
			if(!worldExists(worldName))
				continue;
			
			// Add the world to the buffer
			buff.add(worldName);
		}
		
		// Return the list of available worlds
		return buff;
	}
	
	/**
	 * Get a list of loaded worlds
	 * @return List of loaded worlds
	 */
	public List<World> getLoadedWorlds() {
		return Bukkit.getWorlds();
	}
	
	/**
	 * Get a list of non loaded worlds
	 * @return List of non loaded worlds
	 */
	public List<String> getNonLoadedWorlds() {
		// Get a list of available worlds
		List<String> worlds = getAvailableWorlds();
		
		// Make sure the list contains any world
		if(worlds.size() == 0)
			return new ArrayList<String>();
		
		// Create a buffer to put all the non loaded worlds in
		List<String> buff = new ArrayList<String>();
		
		// Check for each available world if it's loaded or not
		for(String w : worlds)
			if(isWorldLoaded(w))
				buff.add(w);
		
		// Return the list of non loaded worlds
		return buff;
	}
	
	/**
	 * Check whether a world exists, the world doesn't need to be loaded
	 * @param worldName The name of the world to check
	 * @return True if the world exists
	 */
	public boolean worldExists(String worldName) {
		// Make sure the name isn't empty
		if(worldName.equals(""))
			return false;
		
		// Get the level data file of the world
		File lf = getWorldLevelFile(worldName);
		
		// Check if this file exists, and return the result
		return lf.exists();
	}
	
	/**
	 * Load a world, also broadcast a message to every player that a world is being loaded
	 * @param worldName The name of the world to load
	 * @return The loaded world, or null if something went wrong
	 */
	public World loadWorld(String worldName) {
		return loadWorld(worldName, true);
	}
	
	/**
	 * Load a world
	 * @param worldName The name of the world to load
	 * @param broadcastMsg True to broadcast a message to every player that a world is being loaded
	 * @return The loaded world, or null if something went wrong
	 */
	public World loadWorld(String worldName, boolean broadcastMsg) {
		// Make sure the worldName isn't empty
		if(worldName.equals(""))
			return null;
		
		// Make sure the world exists
		if(!worldExists(worldName))
			return null;
		
		// Broadcast a message to every player
		if(broadcastMsg) {
			for(Player p : Bukkit.getOnlinePlayers())
				p.sendMessage("");
		}
		
		// TODO: Load the world
		
		return null;
	}
	
	/**
	 * Check if a world is loaded
	 * @param worldName Name of the world to check
	 * @return True if the world is loaded, false otherwise
	 */
	public boolean isWorldLoaded(String worldName) {
		// Check for each world if the name equals to the param world name
		for(World w : Bukkit.getWorlds())
			if(w.getName().equals(worldName))
				return true;
		
		// World not loaded, return false
		return false;
	}
	
	/**
	 * Get the amount of loaded worlds (world data)
	 * @return World count
	 */
	public int getWorldsCount() {
		return this.worlds.size();
	}
	
	/**
	 * Get the worlds data file
	 * @return Worlds data file
	 */
	public File getWorldsFile() {
		return new File(WorldPortal.instance.getDataFolder(), "data/worlddata.yml");
	}
	
	/**
	 * Load the worlds from the default file
	 * @return False if failed
	 */
	public boolean load() {
		return load(getWorldsFile());
	}
	
	/**
	 * Load the worlds from an external file
	 * @param f The file to load the world from
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
			WorldPortal.instance.getWPLogger().error("An error occurred while loading the worlds file!");
			
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
	 * @param c The YAML configuration to load the worlds from
	 * @return False if failed
	 */
	public boolean load(YamlConfiguration c) {
		// Make sure the configuration isn't null
		if(c == null)
			return false;
		
		// Make sure the worlds section exists
		if(!c.isConfigurationSection("worlds"))
			return false;
		
		// Select the right configuration section
		ConfigurationSection worldsSect = c.getConfigurationSection("worlddata");
		
		// Load the worlds
		return load(worldsSect);
	}
	
	/**
	 * Load the worlds from a configuration section
	 * @param c The configuration section to load the worlds from
	 * @return False if failed
	 */
	public boolean load(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// List the keys
		Set<String> keys = c.getKeys(false);
		
		// Define a worlds buffer list, to put the loaded worlds in
		List<WPWorld> buff = new ArrayList<WPWorld>();
		
		// Load each world
		for(String key : keys) {
			// Get the configuration section
			ConfigurationSection worldSect = c.getConfigurationSection(key);
			
			// Load the world
			WPWorld p = WPWorld.load(worldSect);
			
			// Make sure the world isn't null
			if(p == null) {
				WorldPortal.instance.getWPLogger().error("Failed to load world from file!");
				continue;
			}
			
			// Put the loaded world into the buffer
			buff.add(p);
		}
		
		// Replace the current worlds list with the loaded list
		this.worlds.clear();
		this.worlds.addAll(buff);
		
		// Loaded successfully, return true
		return true;
	}
	
	/**
	 * Save the current loaded list of worlds to the default worlds file
	 * @return False if failed
	 */
	public boolean save() {
		return save(getWorldsFile());
	}
	
	/**
	 * Save the current loaded list of worlds to a file
	 * @param f The file to save the worlds in
	 * @return False if failed
	 */
	public boolean save(File f) {
		// Make sure the file isn't null
		if(f == null)
			return false;
		
		// Construct a YAML configuration to store the worlds in
		YamlConfiguration c = new YamlConfiguration();
		
		// Add the worlds to he YAML configuration
		if(!save(c))
			return false;
		
		// Save the YAML configuration
		try {
			c.save(f);
			
		} catch (IOException ex) {
			// Print the stack trace
			ex.printStackTrace();
			
			// Show a status message
			WorldPortal.instance.getWPLogger().error("An error occurred while saving the worlds!");
			
			// Return false
			return false;
		}
		
		// Saved successfully, return true
		return true;
	}
	
	/**
	 * Save the list of loaded worlds in a YAML configuration
	 * @param c The YAML configuration to save the worlds in
	 * @return False if failed
	 */
	public boolean save(YamlConfiguration c) {
		// Make sure the YAML configuration isn't null
		if(c == null)
			return false;
					
		// Create a configuration section to save the poratls in
		ConfigurationSection worldsSect = c.createSection("worlddata");
		
		// Save the worlds in the configuration section
		boolean result = save(worldsSect);
		
		// Add the current version number of World World to the YAML configuration
		c.set("version", WorldPortal.instance.getVersion());
		
		// Return the result
		return result;
	}
	
	/**
	 * Save the list of loaded worlds in a configuration section
	 * @param c The configuration section to save the worlds in
	 * @return False if failed
	 */
	public boolean save(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return false;
		
		// Define a counter
		int i = 0;
	
		// Save each world
		for(WPWorld p : this.worlds) {
			// Create a configuration section to store the current world in
			ConfigurationSection worldSect = c.createSection(String.valueOf(i));
			
			// Save the world to the section
			p.save(worldSect);
			
			// Increase the counter
			i++;
		}
		
		// Saved successfully, return true
		return true;
	}
}
