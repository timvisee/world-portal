package com.timvisee.worldportal.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WPWorldUtils {

	/**
	 * Get the world container folder
	 * @return World container folder
	 */
	public static File getWorldContainerFolder() {
		return Bukkit.getWorldContainer();
	}
	
	/**
	 * Returns the world folder of any world, doesn't check if the world exists or if the world name is valid
	 * @param worldName The name of the world to get the folder from
	 * @return World folder
	 */
	public static File getWorldFolder(String worldName) {
		return new File(getWorldContainerFolder(), worldName);
	}
	
	/**
	 * Get the level data file of a world, doesn't check if the world exists or if the world name is valid
	 * @param worldName The name of the world to get the folder from
	 * @return Level data file
	 */
	public static File getWorldLevelFile(String worldName) {
		return new File(getWorldFolder(worldName), "level.dat");
	}
	
	/**
	 * Get a list of available worlds, both loaded an non-loaded worlds
	 * @return List of available worlds
	 */
	public static List<String> getAvailableWorlds() {
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
	public static List<World> getLoadedWorlds() {
		return Bukkit.getWorlds();
	}
	
	/**
	 * Get a list of non loaded worlds
	 * @return List of non loaded worlds
	 */
	public static List<String> getNonLoadedWorlds() {
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
	public static boolean worldExists(String worldName) {
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
	public static World loadWorld(String worldName) {
		return loadWorld(worldName, true);
	}
	
	/**
	 * Load a world
	 * @param worldName The name of the world to load
	 * @param broadcastMsg True to broadcast a message to every player that a world is being loaded
	 * @return The loaded world, or null if something went wrong
	 */
	public static World loadWorld(String worldName, boolean broadcastMsg) {
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
		// TODO: Show some status while loading the world, in the console and to users...
		
		return null;
	}
	
	/**
	 * Check if a world is loaded
	 * @param worldName Name of the world to check
	 * @return True if the world is loaded, false otherwise
	 */
	public static boolean isWorldLoaded(String worldName) {
		// Check for each world if the name equals to the param world name
		for(World w : Bukkit.getWorlds())
			if(w.getName().equals(worldName))
				return true;
		
		// World not loaded, return false
		return false;
	}
	
	/**
	 * Check whether a world name is valid or not
	 * @param worldName The world name to check
	 * @return True if the world name is valid
	 */
	public static boolean isValidWorldName(String worldName) {
		// Make sure the worldName isn't an empty string
		if(worldName.equals(""))
			return false;
		
		// Make sure the world name isn't longer than 256 characters
		if(worldName.length() > 256)
			return false;
		
		// The world name seems to be valid, return true
		return true;
	}
}
