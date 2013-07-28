package com.timvisee.worldportal;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.timvisee.worldportal.command.WPCommandHandler;
import com.timvisee.worldportal.handler.WPMetricsHandler;
import com.timvisee.worldportal.listener.WPBlockListener;
import com.timvisee.worldportal.listener.WPPlayerListener;
import com.timvisee.worldportal.listener.WPWorldListener;
import com.timvisee.worldportal.manager.WPEconomyManager;
import com.timvisee.worldportal.manager.WPPermissionsManager;
import com.timvisee.worldportal.point.WPPointsManager;
import com.timvisee.worldportal.portal.WPPortalsManager;
import com.timvisee.worldportal.world.WPWorldDataManager;
import com.timvisee.worldportal.world.WPWorldManager;

public class WorldPortal extends JavaPlugin {
	
	// World Portal static instance
	public static WorldPortal instance;
	
	// Logger
	private WPLogger log;
	
	// Listeners
	private final WPBlockListener blockListener = new WPBlockListener();
	private final WPPlayerListener playerListener = new WPPlayerListener();
	private final WPWorldListener worldListener = new WPWorldListener();
	
	// Managers
	private WPPermissionsManager permsMan;
	private WPEconomyManager econMan;
	private WPWorldManager worldMan;
	private WPWorldDataManager worldDataMan;
	private WPPortalsManager portalMan;
	private WPPointsManager pointMan;
	
	// Handlers
	private WPMetricsHandler metricsHand;
	
	/**
	 * Constructor
	 */
	public WorldPortal() {
		// Define the World Portal static instance variable
		instance = this;
	}
	
	/**
	 * On enable method, called when plugin is being enabled
	 */
	public void onEnable() {
		// Store the time Safe Creeper is starting on so the starting duration can be calculated
		long t = System.currentTimeMillis();

		// Set up the World Portal logger
		setUpWPLogger();
		
		// Get Bukkit's plugin manager instance
		PluginManager pm = getServer().getPluginManager();
		
		// TODO: Set up the API manager
		
		// TODO: Check if config files exist
		
		// TODO: Set up config manager
		
		// TODO: Initialize update checker
		// TODO: Remove all (old) update files
		
		// TODO: Set up all managers and handlers
		
		// Managers
		setUpPermissionsManager();
		setUpEconomyManager();
		setUpWorldManager();
		setUpWorldDataManager();
		setUpPortalsManager();
		setUpPointsManager();
		
		// Handlers
		setUpMetricsHandler();
		
		// Register event listeners
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.worldListener, this);
		
		// Plugin sucesfuly enabled, show console message
		PluginDescriptionFile pdfFile = getDescription();
		
		// Calculate the load duration
		long duration = System.currentTimeMillis() - t;
		
		// Show a status message
		getWPLogger().info("World Portal v" + pdfFile.getVersion() + " enabled, took " + String.valueOf(duration) + " ms!");
	}
	
	/**
	 * On disable method, called when plugin is being disabled
	 */
	public void onDisable() {
		// Save all portals
		this.portalMan.save(true);
		
		// Save all points
		this.pointMan.save();
		
		// Save all world data
		this.worldDataMan.saveAll(true);
		
		// Cancel all running Safe Creeper tasks
		stopTasks();
		
		// TODO: Unhook all API plugins
		// TODO: Install downloaded updates
		// TODO: Remove all update files
		
		// Plugin disabled, show console message
		getWPLogger().info("World Portal Disabled");
	}
	
	/**
	 * Stop all scheduled Safe Creeper tasks
	 */
	public void stopTasks() {
		// Show a status message
		getWPLogger().info("Cancelling all running World Portal tasks...");
		
		// Store the time the cancelation is starting at
		long t = System.currentTimeMillis();
		
		// Count running World Portal tasks
		int tasksCount = 0;
		for(BukkitTask bt : getServer().getScheduler().getPendingTasks())
			if(bt.getOwner().equals(this))
				tasksCount++;
		
		// Stop all running World Portal tasks
		getServer().getScheduler().cancelTasks(this);
		
		// Calculate the duration of the task cancelation
		long duration = System.currentTimeMillis() - t;
		
		// Show a status message
		getWPLogger().info(String.valueOf(tasksCount) + " World Portal tasks cancelled, took " + String.valueOf(duration) + " ms!");
	}
    
	/**
	 * Fetch the World Portal version from the plugin description file
	 * @return World Portal version
	 */
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	/**
	 * Set up the World Portal logger
	 */
	public void setUpWPLogger() {
		this.log = new WPLogger(getLogger());
	}
	
	/**
	 * Get the World Portal logger instance
	 * @return World Portal logger instance
	 */
	public WPLogger getWPLogger() {
		return this.log;
	}
	
	/**
	 * Setup the permissions manager
	 */
	public void setUpPermissionsManager() {
		this.permsMan = new WPPermissionsManager(this.getServer(), getWPLogger());
		this.permsMan.setUp();
	}
	
	/**
	 * Get the permissions manager instance
	 * @return permissions manager instance
	 */
	public WPPermissionsManager getPermissionsManager() {
		return this.permsMan;
	}
	
	/**
	 * Set up the economy manager
	 */
	public void setUpEconomyManager() {
		this.econMan = new WPEconomyManager(this.getServer(), getWPLogger());
		this.econMan.setUp();
	}
	
	/**
	 * Get the economy manager instance
	 * @return Economy manager instance
	 */
	public WPEconomyManager getEconomyManager() {
		return this.econMan;
	}
	
	/**
	 * Set up the world manager
	 */
	public void setUpWorldManager() {
		// Construct and set up the world manager class
		this.worldMan = new WPWorldManager();
		
		// Register all current loaded worlds in the world manager
		for(World w : Bukkit.getWorlds())
			this.worldMan.registerWorld(w);
		
		// TODO: Create world data for these worlds if it doesn't exist
	}
	
	/**
	 * Get the world manager instance
	 * @return World manager instance
	 */
	public WPWorldManager getWorldManager() {
		return this.worldMan;
	}
	
	/**
	 * Set up the world data manager and load all world data
	 */
	public void setUpWorldDataManager() {
		// Construct the world data manager
		this.worldDataMan = new WPWorldDataManager();
		
		// Load world data for each already loaded world
		for(World w : Bukkit.getWorlds()) {
			// Check if the world data is already loaded
			if(!this.worldDataMan.isLoaded(w)) {
				// Check if the world data for this world exists
				if(this.worldDataMan.exists(w)) {
					// Load the world data
					this.worldDataMan.load(w, true);
				} else {
					// TODO: Create world data for this world!
				}
			}
		}
	}
	
	/**
	 * Get the world data manager instance
 	 * @return World data manager instance
	 */
	public WPWorldDataManager getWorldDataManager() {
		return this.worldDataMan;
	}
	
	/**
	 * Set up the portal manager and load all portals
	 */
	public void setUpPortalsManager() {
		// Construct the portal manager
		this.portalMan = new WPPortalsManager();
		
		// Load the portals
		this.portalMan.load(true);
	}
	
	/**
	 * Get the portals manager instance
	 * @return Portals manager instance
	 */
	public WPPortalsManager getPortalManager() {
		return this.portalMan;
	}
	
	/**
	 * Set up the points manager and and load all points
	 */
	public void setUpPointsManager() {
		// Show a status message
		getWPLogger().info("Loading points...");
		
		// Save the time
		long t = System.currentTimeMillis();
		
		// Construct the points manager
		this.pointMan = new WPPointsManager();
		
		// Load the points
		boolean result = this.pointMan.load();
		
		// Calculate the duration
		long duration = System.currentTimeMillis() - t;
		
		// Show a status message
		if(result)
			getWPLogger().info("Loaded " + String.valueOf(this.pointMan.getPointsCount()) + "points, took " + String.valueOf(duration) + " ms!");
		else
			getWPLogger().error("An error occured while loading the points!");
	}
	
	/**
	 * Get the points manager instance
	 * @return Points manager instance
	 */
	public WPPointsManager getPointsManager() {
		return this.pointMan;
	}
	
	/**
	 * Set up the metrics handler
	 */
	public void setUpMetricsHandler() {
		this.metricsHand = new WPMetricsHandler(getWPLogger());
		this.metricsHand.setUp();
	}
	
	/**
	 * Get the metrics handler instance
	 * @return Metrics handler instance
	 */
	public WPMetricsHandler getMetricsHandler() {
		return this.metricsHand;
	}
	
	/*
	/**
	 * Check if the config file exists
	 * @throws Exception
	 * /
	public void checkConigFilesExist() throws Exception {
		if(!getDataFolder().exists()) {
			getWPLogger().info("Creating new Safe Creeper folder");
			getDataFolder().mkdirs();
		}
		File configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			getWPLogger().info("Generating new config file");
			copyFile(getResource("res/config.yml"), configFile);
		}
		if(!globalConfigFile.exists()) {
			getWPLogger().info("Generating new global file");
			copyFile(getResource("res/global.yml"), globalConfigFile);
		}
		if(!worldConfigsFolder.exists()) {
			getWPLogger().info("Generating new 'worlds' folder");
			worldConfigsFolder.mkdirs();
			copyFile(getResource("res/worlds/world_example.yml"), new File(worldConfigsFolder, "world_example.yml"));
			copyFile(getResource("res/worlds/world_example2.yml"), new File(worldConfigsFolder, "world_example2.yml"));
		}
	}
	
	/**
	 * Copy a file
	 * @param in Input stream (file)
	 * @param file File to copy the file to
	 * /
	private void copyFile(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	*/

	/**
	 * On command method, called when a command ran on the server
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		// Run the command trough the command handler
		WPCommandHandler ch = new WPCommandHandler();
		return ch.onCommand(sender, cmd, commandLabel, args);
	}
}
