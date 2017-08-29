package com.timvisee.worldportal.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.util.WPWorldUtils;

public class WPWorldManager {
	
	private List<WPWorld> worlds = new ArrayList<WPWorld>(); 
	
	/**
	 * Get the amount of loaded worlds (world data)
	 * @return World count
	 */
	public int getWorldsCount() {
		return this.worlds.size();
	}
	
	/**
	 * Register a new world
	 * @param w The world to register
	 * @return The registered world, or null if the world wasn't valid
	 */
	public WPWorld registerWorld(World w) {
		// Make sure the world isn't null
		if(w == null)
			return null;
		
		// Register the world and return the result
		return registerWorld(w.getName());
	}
	
	/**
	 * Register a new world
	 * @param worldName The name of the world to register
	 * @return The registered world, or null if the world name wasn't valid
	 */
	public WPWorld registerWorld(String worldName) {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return null;
		
		// Make sure this world isn't registered already
		if(isWorldRegistered(worldName))
			return getWorld(worldName);
		
		// Construct the WPWorld instance
		WPWorld w = new WPWorld(worldName);
		
		// Register the world
		this.worlds.add(w);
		
		// Show a status message
		WorldPortal.instance.getWPLogger().info("Registered the world '" + worldName + "'");
		
		// Return the world instance
		return w;
	}
	
	/**
	 * Get a world
	 * @param w The world to retrieve
	 * @return The world, or null if the world isn't loaded
	 */
	public WPWorld getWorld(World w) {
		// Make sure the world isn't null
		if(w == null)
			return null;
		
		// Get the world and return the result
		return getWorld(w.getName());
	}
	
	/**
	 * Get a world
	 * @param worldName The name of the world to retrieve
	 * @return The world, or null if this world isn't loaded
	 */
	public WPWorld getWorld(String worldName) {
		// Make sure the world name is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return null;
		
		// Get the world from the list
		for(WPWorld w : this.worlds)
			if(w.getName().equals(worldName))
				return w;
		
		// The world couldn't be retrieved, return null
		return null;
	}
	
	/**
	 * Check whether a world is registered
	 * @param w The world to check for
	 * @return True if this world is registered
	 */
	public boolean isWorldRegistered(World w) {
		// Make sure the world isn't null
		if(w == null)
			return false;
		
		// Check whether the world is registered and return the result
		return isWorldRegistered(w.getName());
	}
	
	/**
	 * Check whether a world is registered
	 * @param worldName The name of the world to check for
	 * @return True if this world is registered
	 */
	public boolean isWorldRegistered(String worldName) {
		// Make sure the name of the world is valid
		if(!WPWorldUtils.isValidWorldName(worldName))
			return false;
		
		// Check for each registered world if it equals to the param world name
		for(WPWorld w : this.worlds)
			if(w.getName().equals(worldName))
				return true;
		
		// World not registered, return false
		return false;
	}
	
	/**
	 * Unregister a world
	 * @param w The world to unregister
	 * @return False if this world wasn't registered, or if the world is invalid
	 */
	public boolean unregisterWorld(World w) {
		// Make sure the world name isn't null
		if(w == null)
			return false;
		
		// Unregister the world and return the result
		return unregisterWorld(w.getName());
	}
	
	/**
	 * Unregister a world
	 * @param w The name of the world to unregister
	 * @return False if this world wasn't registered
	 */
	public boolean unregisterWorld(String worldName) {
		// Define a variable to store if any world was unregistered
		boolean anyUnregistered = false;
		
		// Try to unregister the world
		for(int i = 0; i < this.worlds.size(); i++) {
			// Get the world
			WPWorld w = this.worlds.get(i);
			
			// Check if the world equals to the world name
			if(w.getName().equals(worldName)) {
				this.worlds.remove(i);
				i--;
				anyUnregistered = true;
			}
		}
		
		// Return true if any world was unregistered
		return anyUnregistered;
	}
}
