package com.timvisee.worldportal.world;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.util.WPWorldUtils;

public class WPWorld {
	
	private String worldName;
	
	/**
	 * Constructor
	 * @param worldName World name
	 */
	public WPWorld(String worldName) {
		this.worldName = worldName;
	}
	
	/**
	 * Get the name of the world
	 * @return World name
	 */
	public String getName() {
		return this.worldName;
	}
	
	/**
	 * Try to get the bukkit world instance for this world
	 * @return Bukkit world instances, or null if this world isn't loaded
	 */
	public World getBukkitWorld() {
		// Check each loaded bukkit world and check if the name of the world equals to this name
		for(World w : Bukkit.getWorlds())
			if(w.getName().equals(this.worldName))
				return w;
		
		// Failed retrieving bukkit world, return null
		return null;
	}
	
	/**
	 * Check whether this world is loaded or not
	 * @return True if this world is loaded
	 */
	public boolean isLoaded() {
		return WPWorldUtils.isWorldLoaded(this.worldName);
	}
	
	/**
	 * Get the world data for this world
	 * @return World data for this world, null if no world data is loaded for this world
	 */
	public WPWorldData getWorldData() {
		return WorldPortal.instance.getWorldDataManager().getWorldData(this.worldName);
	}
}
