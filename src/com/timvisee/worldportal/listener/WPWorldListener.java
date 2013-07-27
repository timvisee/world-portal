package com.timvisee.worldportal.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WPWorldListener implements Listener {

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) { }
	
	@EventHandler
	public void onWorldSave(WorldSaveEvent event) { }
	
	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event) { }
}
