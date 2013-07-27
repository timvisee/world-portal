package com.timvisee.worldportal.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.world.WPWorldDataManager;

public class WPWorldListener implements Listener {

	/**
	 * Called when a world is being loaded
	 * @param e WorldLoadEvent instance
	 */
	@EventHandler
	public void onWorldLoad(WorldLoadEvent e) {
		World w = e.getWorld();
		
		// Register the world in the world managers
		WorldPortal.instance.getWorldManager().registerWorld(w);
		
		// Get the world data manager instance
		WPWorldDataManager wdm = WorldPortal.instance.getWorldDataManager();
		
		// Check if any world data exists for the loaded world
		if(wdm.exists(w)) {
			// Load the world data for the current world
			wdm.load(w);
			
		} else {
			// TODO: Create the world data!
		}
	}
	
	/**
	 * Called when a world is being unloaded
	 * @param event WorldUnloadEvent instance
	 */
	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event) {
		World w = event.getWorld();
		
		// Unregister the world in the world managers
		WorldPortal.instance.getWorldManager().unregisterWorld(w);
		
		// Unload any world data for the unloaded world
		WorldPortal.instance.getWorldDataManager().unload(w, true);
	}
}
