package com.timvisee.worldportal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.timvisee.worldportal.handler.WPCommandHandler;
import com.timvisee.worldportal.handler.WPMetricsHandler;
import com.timvisee.worldportal.manager.WPEconomyManager;
import com.timvisee.worldportal.manager.WPPermissionsManager;

public class WorldPortal extends JavaPlugin {
	
	// World Portal static instance
	public static WorldPortal instance;
	
	// Logger
	private WPLogger log;
	
	// Listeners
	// private final SCBlockListener blockListener = new SCBlockListener();
	
	// Managers
	private WPPermissionsManager pm;
	private WPEconomyManager em;
	
	// Handlers
	private WPMetricsHandler mh;
	
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
		
		// Get Bukkit's plugin manager
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
		
		// Handlers
		setUpMetricsHandler();
		
		// Register event listeners
		//pm.registerEvents(this.blockListener, this);
		
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
		// TODO: Save unsaved data
		
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
		this.pm = new WPPermissionsManager(this.getServer(), getWPLogger());
		this.pm.setUp();
	}
	
	/**
	 * Get the permissions manager instance
	 * @return permissions manager instance
	 */
	public WPPermissionsManager getPermissionsManager() {
		return this.pm;
	}
	
	/**
	 * Set up the economy manager
	 */
	public void setUpEconomyManager() {
		this.em = new WPEconomyManager(this.getServer(), getWPLogger());
		this.em.setUp();
	}
	
	/**
	 * Get the economy manager instance
	 * @return Economy manager instance
	 */
	public WPEconomyManager getEconomyManager() {
		return this.em;
	}
	
	/**
	 * Set up the metrics handler
	 */
	public void setUpMetricsHandler() {
		this.mh = new WPMetricsHandler(getWPLogger());
		this.mh.setUp();
	}
	
	/**
	 * Get the metrics handler instance
	 * @return Metrics handler instance
	 */
	public WPMetricsHandler getMetricsHandler() {
		return this.mh;
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
