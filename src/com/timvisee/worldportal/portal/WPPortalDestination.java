package com.timvisee.worldportal.portal;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public abstract class WPPortalDestination {
	
	/**
	 * Teleport a player to the destination
	 * @param p The player to teleport
	 * @return False if failed
	 */
	public boolean teleport(Player p) {
		return teleport(p, TeleportCause.PLUGIN);
	}
	
	/**
	 * Teleport a player to the destination
	 * @param p The player to teleport
	 * @param cause Teleport cause
	 * @return False if failed
	 */
	public abstract boolean teleport(Player p, TeleportCause cause);
	
	/**
	 * Get the location of the destination, if more locations are available, a random one will be returned
	 * @return Location of the destination, a random destination if more destinations are availble
	 */
	public abstract Location getLocation();
	
	/**
	 * Load a destination from a configuration section
	 * @param c Configuration sectin to load the destination from
	 * @return False if failed
	 */
	public static WPPortalDestination load(ConfigurationSection c) {
		// Make sure the configuration section isn't null
		if(c == null)
			return null;
		
		// TODO: Load some data and select the right destination class to load the destination
		
		return null;
	}
	
	/**
	 * Save the destination in a configuration section
	 * @param c Configuration Section to save the destination in
	 * @return False if failed
	 */
	public abstract boolean save(ConfigurationSection c);
}
