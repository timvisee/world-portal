package com.timvisee.worldportal;

import com.timvisee.worldportal.command.CommandHandler;
import com.timvisee.worldportal.world.WorldManager;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class WorldPortal extends JavaPlugin implements CommandExecutor {

	/**
	 * Defines the name of the plugin.
	 */
	private static final String PLUGIN_NAME = "World Portal";

	/**
	 * Defines the current Dungeon Maze version name.
	 */
	private static final String PLUGIN_VERSION_NAME = "0.2.9.1";

	/**
	 * Defines the current Dungeon Maze version code.
	 */
	private static final int PLUGIN_VERSION_CODE = 10;

	public static Logger log = Logger.getLogger("Minecraft");
	
	private final BlockListener blockListener = new BlockListener(this);
	private final EntityListener entityListener = new EntityListener(this);
	private final PlayerListener playerListener = new PlayerListener(this);
	public final CreatePortal createPortal = new CreatePortal(this);

	private File worldPortalsFile = new File("plugins/WorldPortal/WorldPortals.list");
	private File worldPortalConfigFile = new File("plugins/WorldPortal/config.yml");
	private File worldPortalLangFile = new File("plugins/WorldPortal/messages.yml");
	
	public final List<Player> wpRemoveUsers = new ArrayList<Player>();

	private WorldManager worldManager;

	private CommandHandler commandHandler;

	private PermissionsManager permsMan;

	List<String> worldPortals = new ArrayList<String>();
	List<String> movedTooQuicklyIgnoreList = new ArrayList<String>();
	
	public final HashMap<String, Location> lastPlayerTeleportLocation = new HashMap<String, Location>();

	public static WorldPortal instance;

	/**
	 * Defines the initialization time of the core.
	 */
	private Date initTime = new Date();

	/**
	 * List of possible block faces.
	 */
	public static final BlockFace BLOCK_FACES[] = {
			BlockFace.UP,
			BlockFace.DOWN,
			BlockFace.NORTH,
			BlockFace.EAST,
			BlockFace.SOUTH,
			BlockFace.WEST,
	};

	public WorldPortal() {
		WorldPortal.instance = this;
	}

	public void onEnable() {
		// Setup costum files and folders
		worldPortalsFile = new File(getDataFolder() + "/" + getConfig().getString("WorldPortalsListFile", "WorldPortals.list"));
		worldPortalConfigFile = new File(getDataFolder() + "/" + "config.yml");
		worldPortalLangFile = new File(getDataFolder() + "/" + "messages.yml");

		initTime = new Date();
		
		// Check if all the config file exists
		try {
			checkConigFilesExist();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.entityListener, this);
		pm.registerEvents(this.playerListener, this);

		// Set up the world manager
		setUpWorldManager();

		// Setup the command handler
        setUpCommandHandler();
		
		// Setup the permissions manager
		setUpPermissionsManager();
		
		// Load all the world portals in the local list
		loadWorldPortals();
		
		PluginDescriptionFile pdfFile = getDescription();
		log.info("[WorldPortal] WorldPortal v" + pdfFile.getVersion() + " Started");
	}

	public void onDisable() {
		// Save all the world portals to a external file (don't save it anymore this could couse an empty file bug)
		//saveWorldPortals();
		
		PluginDescriptionFile pdfFile = getDescription();
		log.info("[WorldPortal] WorldPortal v" + pdfFile.getVersion() + " Disabled");
	}

	public void setUpWorldManager() {
		// Initialize the world manager
		this.worldManager = new WorldManager(false);

		// Initialize the world manager, return the result
		this.worldManager.init();
	}

	public WorldManager getWorldManager() {
		return this.worldManager;
	}

    public void setUpCommandHandler() {
		// Initialize the command handler
		this.commandHandler = new CommandHandler(false);

		// Initialize the command handler, return the result
		this.commandHandler.init();
	}

	public CommandHandler getCommandHandler() {
		return commandHandler;
	}

	/**
	 * Handle Bukkit commands.
	 *
	 * @param sender       The command sender (Bukkit).
	 * @param cmd          The command (Bukkit).
	 * @param commandLabel The command label (Bukkit).
	 * @param args         The command arguments (Bukkit).
	 *
	 * @return True if the command was executed, false otherwise.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		// Make sure the command handler isn't null
		if(commandHandler == null)
			return false;

		// Handle the command, return the result
		return commandHandler.onCommand(sender, cmd, commandLabel, args);
	}

	/**
	 * Setup the permissions manager
	 */
	public void setUpPermissionsManager() {
	    // Determine whether to use permissions
		boolean usePerms = getConfig().getBoolean("permissions.usePermissions", true);

		// Set up and start the permissions managj
		this.permsMan = new PermissionsManager(
				Bukkit.getServer(),
				this,
				getLogger(),
				usePerms
		);

		// Set up the permissions manager
		if(usePerms)
            this.permsMan.setup();

		// Set whether to always permit OP players
		this.permsMan.setAlwaysPermitOp(getConfig().getBoolean("permissions.alwaysPermitOp", false));
	}

	/**
	 * Get the permissions manager instance
	 * @return permissions manager instance
	 */
	public PermissionsManager getPermissionsManager() {
		return this.permsMan;
	}

	public boolean canUsePortal(Player player) {
	    return getPermissionsManager().hasPermission(player, "worldportal.use", true);
	}

	public void saveWorldPortals() {
		// Save the Array(List) worldPortals in a String, line by line to a file
		log.info("[WorldPortal] Saving World Portals...");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(worldPortalsFile));
			//ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(worldPortalsFile));
			
			for(int i = 0; i < worldPortals.size(); i++) {
				if(i != 0) { out.newLine(); }
				out.write(worldPortals.get(i));
			}
			out.close();

			log.info("[WorldPortal] World Portals saved");
		} catch(IOException e) {
	      System.out.println(e);
			log.info("[WorldPortal] Error by saving World Portals");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void loadWorldPortals() {
		// Load the Array(List) worldPortals in a
	    if(worldPortalsFile.exists()) {
			log.info("[WorldPortal] Loading World Portals...");
	    	File file = worldPortalsFile;
	        FileInputStream fis = null;
	        BufferedInputStream bis = null;
	        DataInputStream dis = null;

	        try {
				fis = new FileInputStream(file);
				
				// Here BufferedInputStream is added for fast reading.
				bis = new BufferedInputStream(fis);
				dis = new DataInputStream(bis);
				
				// Make the Array(List) WorldPortals empty
				worldPortals.clear();
				
				// dis.available() returns 0 if the file does not have more lines.
				while (dis.available() != 0) {
					worldPortals.add(dis.readLine());
				}
				
				fis.close();
				bis.close();
				dis.close();
				
				log.info("[WorldPortal] World Portals loaded");
	        } catch (IOException e) {
	        	e.printStackTrace();
	    		log.info("[WorldPortal] Error by loading World Portals");
	        }
	    } else {
    		log.info("[WorldPortal] File '" + worldPortalsFile.getPath() + "' not found");
	    }
	}
	
	public void checkConigFilesExist() throws Exception {
		if(!getDataFolder().exists()) {
			log.info("[WorldPortal] Creating new WorldPortal folder");
			getDataFolder().mkdirs();
		}
		if(!worldPortalConfigFile.exists()) {
			log.info("[WorldPortal] Generating new config file");
			copy(getResource("res/WorldPortal/config.yml"), worldPortalConfigFile);
		}
		if(!worldPortalsFile.exists()) {
			log.info("[WorldPortal] Generating new portals file");
			copy(getResource("res/WorldPortal/WorldPortals.list"), worldPortalsFile);
		}
		if(!worldPortalLangFile.exists()) {
			log.info("[WorldPortal] Generating new language file");
			copy(getResource("res/WorldPortal/messages.yml"), worldPortalLangFile);
		}
	}
	
	private void copy(InputStream in, File file) {
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
	
	
	
	
	
	
	
	// Function to get a configuration file (.yml) from a file path
	public FileConfiguration getConfigurationFromPath(String filePath, boolean insideDataFolder) {
		if(insideDataFolder) {
			File file = new File(getDataFolder(), filePath);
			return getConfigFromPath(file);
		} else {
			File file = new File(filePath);
			return getConfigFromPath(file);
		}
	}
	
	// Fuctnion to get a constum configuration file
	public FileConfiguration getConfigFromPath(String filePath, boolean insideDataFolder) {
		if(insideDataFolder) {
			File file = new File(getDataFolder(), filePath);
			return getConfigFromPath(file);
		} else {
			File file = new File(filePath);
			return getConfigFromPath(file);
		}
	}
	
	// Function to get a costum configuration file
	public FileConfiguration getConfigFromPath(File file) {
		FileConfiguration c;
		
		if (file == null) {
		    return null;
		}

	    c = YamlConfiguration.loadConfiguration(file);
	    
	    return c;
	}
	
	public String convertChatColors(String input)
    {        
		// Convert the color strings in a string to colors
        return input
                .replace("&0", ChatColor.BLACK.toString())
                .replace("&1", ChatColor.DARK_BLUE.toString())
                .replace("&2", ChatColor.DARK_GREEN.toString())
                .replace("&3", ChatColor.DARK_AQUA.toString())
                .replace("&4", ChatColor.DARK_RED.toString())
                .replace("&5", ChatColor.DARK_PURPLE.toString())
                .replace("&6", ChatColor.GOLD.toString())
                .replace("&7", ChatColor.GRAY.toString())
                .replace("&8", ChatColor.DARK_GRAY.toString())
                .replace("&9", ChatColor.BLUE.toString())
                .replace("&a", ChatColor.GREEN.toString()).replace("&A", ChatColor.GREEN.toString())
                .replace("&b", ChatColor.AQUA.toString()).replace("&B", ChatColor.AQUA.toString())
                .replace("&c", ChatColor.RED.toString()).replace("&C", ChatColor.RED.toString())
                .replace("&d", ChatColor.LIGHT_PURPLE.toString()).replace("&D", ChatColor.LIGHT_PURPLE.toString())
                .replace("&e", ChatColor.YELLOW.toString()).replace("&E", ChatColor.YELLOW.toString())
                .replace("&f", ChatColor.WHITE.toString()).replace("&F", ChatColor.WHITE.toString());        
    }
	
	public String getMessage(String messageId, String defaultMessage) {
		return convertChatColors(getConfigurationFromPath("messages.yml", true).getString(messageId, defaultMessage));
	}
	
	public List<String> getMessageList(String messageId, List<String> defaultMessages) {
		List<String> messages = getConfigurationFromPath("messages.yml", true).getStringList(messageId);
		List<String> messagesOutput = new ArrayList<String>();

		for (String message : messages)
			messagesOutput.add(convertChatColors(message));

		return messagesOutput;
	}
	
	public void sendMessageList(Player player, String messageId, List<String> defaultMessages) {
		List<String> messages = getMessageList(messageId, defaultMessages);
		for(int i = 0; i < messages.size(); i++)
			player.sendMessage(convertChatColors(messages.get(i)));
	}

	public void addMovedTooQuicklyIgnoreListPlayer(Player player) {
		addMovedTooQuicklyIgnoreListPlayer(player.getName());
	}
	
	public void addMovedTooQuicklyIgnoreListPlayer(String player) {
		if(movedTooQuicklyIgnoreList.contains(player)) {
			movedTooQuicklyIgnoreList.remove(player);
		}
		
		movedTooQuicklyIgnoreList.add(player);
		
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() { public void run() {
			if(movedTooQuicklyIgnoreList.size() >= 1) {
				movedTooQuicklyIgnoreList.remove(0);
			}
		} }, 20*10);
	}
	
	public void addLastTeleportPlayerLocation(Player player, Location location) {
		addLastTeleportPlayerLocation(player.getName(), location);
	}
	
	public void addLastTeleportPlayerLocation(String player, Location location) {
		if(lastPlayerTeleportLocation.containsKey(player)) {
			lastPlayerTeleportLocation.remove(player);
		}
		
		lastPlayerTeleportLocation.put(player, location);
	}
	
	public Location getLastTeleportPlayerLocation(String player) {
		if(lastPlayerTeleportLocation.containsKey(player)) {
			return lastPlayerTeleportLocation.get(player);
		}
		return getServer().getPlayer(player).getLocation();
	}
	
	
	
	public void toggleWPRemoveUsers(Player player) {
		if (wpRemoveUsers.contains(player)) {
			// Disable creation mode
			wpRemoveUsers.remove(player);
			player.sendMessage(ChatColor.YELLOW + "[WorldPortal] Remove-mode " + ChatColor.DARK_RED + "disabled");
		} else {
			// Enable creation mode
			wpRemoveUsers.add(player);
			String[] defaultMessages = {"&e[WorldPortal] Remove-mode &aenabled", "&e[WorldPortal] Right-click on a WorldPortal to remove it",
					"&e[WorldPortal] Remove a &fSign&e, &fLever&e, &fPressureplate&e or a &fbutton&e", "&e[WorldPortal] Use &f/wp remove stop &eto disable the remove-mode"};
			sendMessageList(player, "removeModeEnabled", Arrays.asList(defaultMessages));
		}
	}
	
	public boolean WPRemoveUsersEnabled(Player player) {
		return wpRemoveUsers.contains(player);
	}
	
	public boolean isInt(String string) {
		return stringIsInt(string);
	}
	
	public boolean stringIsInt(String string) {
        try {
            @SuppressWarnings("unused")
			int i = Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
	
	public void createWorld(String worldName, Environment environment) {
		createWorld(worldName, environment, true);
	}
	public void createWorld(String worldName, Environment environment, boolean broadcastMessage) {
		if(!worldExists(worldName)) {
			if(getConfig().getBoolean("broadcastMessageOnWorldGeneration", true)) {
				//getServer().broadcastMessage(getMessage("worldGenerationBroadcastMessage", "&d[WorldPortal] Generating a new world, there's probably some lag for a little while"));
				//getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[WorldPortal] Generating a new world, there's probably some lag for a little while");
			}
			
			/*//getServer().createWorld(worldName, environment);
			
			WorldCreator newWorld = new WorldCreator(worldName);
			newWorld.environment(environment);
			newWorld.generateStructures(true);
			getServer().createWorld(newWorld);*/
			(new WorldCreator(worldName)).environment(environment).createWorld();
			
			if(getConfig().getBoolean("broadcastMessageOnWorldGeneration", true)) {
				//getServer().broadcastMessage(getMessage("worldGenerationBroadcastMessageDone", "&d[WorldPortal] World generation complete!"));
				//getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[WorldPortal] World generation complete!");
			}
		}
	}
	
	public boolean worldExists(World world) {
		return worldExists(world.getName());
	}
	
	public boolean worldExists(String worldName) {
		File worldLevelFile = new File(worldName + "/level.dat");
		return worldLevelFile.exists();
	}
	
	public boolean isWorldLoaded(World world) {
		return isWorldLoaded(world.getName());
	}
	public boolean isWorldLoaded(String worldName) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<World> worlds = new ArrayList();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> worldNames = new ArrayList();
		
		worlds.addAll(getServer().getWorlds());
		for(int i=0; i < worlds.size(); i++) {
			worldNames.add(worlds.get(i).getName());
		}
		if(worldNames.contains(worldName)) {
			return true;
		}
		// No world with this name, return false
		return false;
	}
	
	public void loadWorld(World world) {
		loadWorld(world.getName());
	}
	public void loadWorld(String worldName) {
		if(worldExists(worldName)) {
			if(getConfig().getBoolean("broadcastMessageOnWorldLoad", true)) {
				getServer().broadcastMessage(getMessage("worldLoadBroadcastMessage", "&d[WorldPortal] Loading world, there's probably some lag for a little while"));
			}
			
			WorldCreator newWorld = new WorldCreator(worldName);
			newWorld.environment(Environment.NORMAL);
			newWorld.generateStructures(true);
			getServer().createWorld(newWorld);
			
			if(getConfig().getBoolean("broadcastMessageOnWorldLoad", true)) {
				getServer().broadcastMessage(getMessage("worldLoadBroadcastMessageDone", "&d[WorldPortal] World succesfully loaded!"));
			}
		}
	}
	
	public boolean checkIfNumber(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
	
	public void removeWorldPortal(Block block, boolean removeBlock) {
		removeWorldPortal(block.getWorld(), block, removeBlock, true, true);
	}

	public void removeWorldPortal(World world, Block block, boolean removeBlock, boolean escalate, boolean save) {
		// Escalate to find connected blocks
		if(escalate) {
			// Find other bed parts, and add it
			if(block.getType() == Material.BED) {
				final Block other = WorldPortal.findOtherBedBlock(block);
				if(other != null)
					removeWorldPortal(world, other, removeBlock, false, false);
			}

			// Find adjacent portal blocks
			if(block.getType() == Material.PORTAL || block.getType() == Material.END_GATEWAY) {
				// Find connected portal blocks, and add them all
				final Set<Block> portalBlocks = WorldPortal.getConnectedBlocks(block);
				for (Block portalBlock : portalBlocks)
					removeWorldPortal(world, portalBlock, removeBlock, false, false);

				// Save and return
                if(save)
                    saveWorldPortals();
				return;
			}
		}

		// Remove the block
		for(int i = 0; i < worldPortals.size(); i++) {
			final String[] worldPortalValues = worldPortals.get(i).split("\\|");
			if(worldPortalValues[0].equals(world.getName())) {
				final String worldPortalLocationString = Integer.toString(block.getX())+" "+Integer.toString(block.getY())+" "+Integer.toString(block.getZ());
				if(worldPortalValues[1].equals(worldPortalLocationString))
					worldPortals.remove(i);
			}
		}

		// Remove the block
		if(removeBlock)
			block.setType(Material.AIR);

		// Save the portals because one was removed
		if(save)
            saveWorldPortals();
	}
	
	public boolean isWorldPortal(Block block) {
		return isWorldPortal(block.getWorld(), block);
	}
	public boolean isWorldPortal(World world, Block block) {
		for (String worldPortal : worldPortals) {
			String[] worldPortalValues = worldPortal.split("\\|");
			if (worldPortalValues[0].equals(world.getName())) {
				String worldPortalLocationString = Integer.toString(block.getX()) + " " + Integer.toString(block.getY()) + " " + Integer.toString(block.getZ());
				if (worldPortalValues[1].equals(worldPortalLocationString)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public World getWorldPortalLinkedWorld(Block block) {
		return getWorldPortalLinkedWorld(block.getWorld(), block);
	}
	public World getWorldPortalLinkedWorld(World world, Block block) {
		for (String worldPortal : worldPortals) {
			String[] worldPortalValues = worldPortal.split("\\|");
			if (worldPortalValues[0].equals(world.getName())) {
				String worldPortalLocationString = Integer.toString(block.getX()) + " " + Integer.toString(block.getY()) + " " + Integer.toString(block.getZ());
				if (worldPortalValues[1].equals(worldPortalLocationString)) {
					return getServer().getWorld(worldPortalValues[2]);
				}
			}
		}
		return world;
	}
	public String getWorldPortalLinkedWorldName(String world, Block block) {
		for (String worldPortal : worldPortals) {
			String[] worldPortalValues = worldPortal.split("\\|");
			if (worldPortalValues[0].equals(world)) {
				String worldPortalLocationString = Integer.toString(block.getX()) + " " + Integer.toString(block.getY()) + " " + Integer.toString(block.getZ());
				if (worldPortalValues[1].equals(worldPortalLocationString)) {
					return worldPortalValues[2];
				}
			}
		}
		return world;
	}
	
	public Location getWorldPortalLinkedWorldSpawnLocation(World world, Block block) {
		for (String worldPortal : worldPortals) {
			String[] worldPortalValues = worldPortal.split("\\|");
			if (worldPortalValues[0].equals(world.getName())) {
				String worldPortalLocationString = Integer.toString(block.getX()) + " " + Integer.toString(block.getY()) + " " + Integer.toString(block.getZ());
				if (worldPortalValues[1].equals(worldPortalLocationString)) {

					if (worldPortalValues[3].equalsIgnoreCase("spawn")) {
						return getFixedSpawnLocation(getWorldPortalLinkedWorld(world, block).getSpawnLocation());

					} else if (worldPortalValues[3].split(" ").length == 2) {
						String[] splittedLocation = worldPortalValues[3].split(" ");
						Location linkedWorldSpawnLocation = block.getLocation();
						linkedWorldSpawnLocation.setX(Integer.parseInt(splittedLocation[0]));
						linkedWorldSpawnLocation.setY(0);
						linkedWorldSpawnLocation.setZ(Integer.parseInt(splittedLocation[1]));
						linkedWorldSpawnLocation.setY(getWorldPortalLinkedWorld(world, block).getHighestBlockYAt(linkedWorldSpawnLocation.getBlockX(), linkedWorldSpawnLocation.getBlockZ()));
						return linkedWorldSpawnLocation;

					} else {
						String[] splittedLocation = worldPortalValues[3].split(" ");
						Location linkedWorldSpawnLocation = block.getLocation();
						linkedWorldSpawnLocation.setX(Integer.parseInt(splittedLocation[0]));
						linkedWorldSpawnLocation.setY(Integer.parseInt(splittedLocation[1]));
						linkedWorldSpawnLocation.setZ(Integer.parseInt(splittedLocation[2]));
						return linkedWorldSpawnLocation;
					}

				}
			}
		}
		return block.getLocation();
	}
	
	public float getWorldPortalLinkedWorldLookingDirection(World world, Block block) {
		for (String worldPortal : worldPortals) {
			String[] worldPortalValues = worldPortal.split("\\|");
			if (worldPortalValues[0].equals(world.getName())) {
				String worldPortalLocationString = Integer.toString(block.getX()) + " " + Integer.toString(block.getY()) + " " + Integer.toString(block.getZ());
				if (worldPortalValues[1].equals(worldPortalLocationString)) {

					if (worldPortalValues.length >= 5) {
						return Float.parseFloat(worldPortalValues[4].toString());
					}

				}
			}
		}
		return 0;
	}
	
	public void doWorldPortal(Player player, Block block) {
		doWorldPortal(player, block.getWorld(), block);
	}
	public void doWorldPortal(Player player, World world, Block block) {
		if(!canUsePortal(player)) {
			player.sendMessage(ChatColor.YELLOW + "[WorldPortal] " + ChatColor.DARK_RED + "You don't have permission to use this portal!");
			return;
		}
		
		String tpToWorldName = getWorldPortalLinkedWorldName(block.getWorld().getName(), block);
		if(!isWorldLoaded(tpToWorldName)) {
			if(worldExists(tpToWorldName)) {
				loadWorld(tpToWorldName);
			} else {
				createWorld(tpToWorldName, Environment.NORMAL, true);
			}
		}
		
		// Set some variables to teleport to
		World tpToWorld = getWorldPortalLinkedWorld(block.getWorld(), block);
		Location WorldPortalLinkedWorldSpawnLocation = getWorldPortalLinkedWorldSpawnLocation(player.getWorld(), block);
		Location tpToWorldLocation = new Location(tpToWorld, WorldPortalLinkedWorldSpawnLocation.getX(), WorldPortalLinkedWorldSpawnLocation.getY(), WorldPortalLinkedWorldSpawnLocation.getZ());
		
		// Set the looking direction of the teleportation
		tpToWorldLocation.setYaw(getWorldPortalLinkedWorldLookingDirection(player.getWorld(), block));
		
		// Moved to quickly fix!
		addMovedTooQuicklyIgnoreListPlayer(player);
		
		// Teleport the player using the following function
		teleportPlayer(player, tpToWorldLocation, true);
		
		// Force the chunk where the player was teleported to, to load.
		// Otherwise its possible that the player is floating in a black hole in the world.
		forceChunkToLoad(tpToWorldLocation);
	}
	
	public void teleportPlayer(Player player, String worldName) {
		teleportPlayer(player, worldName, true);
	}
	public void teleportPlayer(Player player, String worldName, boolean showMessage) {
		teleportPlayer(player, getServer().getWorld(worldName).getSpawnLocation(), showMessage);
	}
	public void teleportPlayer(Player player, String worldName, int x, int z) {
		teleportPlayer(player, worldName, x, z, true);
	}
	public void teleportPlayer(Player player, String worldName, int x, int z, boolean showMessage) {
		int y = getServer().getWorld(worldName).getHighestBlockYAt(x, z);
		teleportPlayer(player, worldName, x, y, z, showMessage);
	}
	public void teleportPlayer(Player player, String worldName, int x, int y, int z) {
		teleportPlayer(player, worldName, x, y, z, true);
	}
	public void teleportPlayer(Player player, String worldName, int x, int y, int z, boolean showMessage) {
		teleportPlayer(player, new Location(getServer().getWorld(worldName), x, y, z), showMessage);
	}
	public void teleportPlayer(Player player, Location location) {
		teleportPlayer(player, location, true);
	}
	public void teleportPlayer(Player player, Location location, boolean showMessage) {
		// Teleport the player
		
		// Fix the location (to the middle of the block!)
		if(((double)location.getBlockX()) >= 0) {
			location.setX(((double)location.getBlockX())+0.5);
		} else {
			location.setX(((double)location.getBlockX())+0.5);
		}
		if(((double)location.getBlockZ()) >= 0) {
			location.setZ(((double)location.getBlockZ())+0.5);
		} else {
			location.setZ(((double)location.getBlockZ())+0.5);
		}
		
		addLastTeleportPlayerLocation(player, location);
		player.setVelocity(new Vector(0, 0, 0));
		player.teleport(location);

		if(showMessage) {
			if(getConfig().getBoolean("showMessageOnTeleportation", true)) {
				String message = getMessage("tpToWorldMessage", "&e[WorldPortal] Teleported to the world '&f%worldname%&e'");
				player.sendMessage(message.replace("%worldname%", location.getWorld().getName()));
			}
		}
	}
	
	public Location getFixedSpawnLocation(Location spawnLocation) {
		for(int y = spawnLocation.getBlockY(); y < 254; y++) {
			// Get the block
			Block current = spawnLocation.getWorld().getBlockAt(
					(int) spawnLocation.getX(),
					y,
					(int) spawnLocation.getZ()
			);

			if(current.getType() == Material.AIR) {
				// Get the blocks one and two above
				Block one = current.getRelative(0, 1, 0);
				Block two = current.getRelative(0, 2, 0);

				// Continue if one isn't free
				if(one == null || one.getType() != Material.AIR)
					continue;

				if(two == null || two.getType() != Material.AIR)
                    spawnLocation.setY(y);
				else
					spawnLocation.setY(y + 1);

				return spawnLocation;
			}
		}

		return spawnLocation;
	}
	
	/**
	 * This will force a chunk to reload
	 * @param location The location of a block inside the chunk
	 * @return Returns true when the chunk was already loaded. But it is force reloaded again
	 */
	public boolean forceChunkToLoad(Location location) {
		boolean b = location.getChunk().isLoaded();
		location.getChunk().load();
		return b;
	}

	/**
	 * Find the other block that is part of the given bed.
	 *
	 * @param block The block of the bed.
	 *
	 * @return Other bed block, or null if not available.
	 */
	public static Block findOtherBedBlock(Block block) {
		// Make sure the block is a bed
		if(block.getType() != Material.BED)
			return null;

		try {
			// Get the bed block material
			final Bed bed = (Bed) block.getState().getData();

			// Get the other block
			final Block other = block.getRelative(bed.getFacing().getOppositeFace());
			if(other.getType() != Material.BED)
				return null;

			// Make sure the other part is the real other part
			final Bed otherBed = (Bed) other.getState().getData();
			return bed.isHeadOfBed() != otherBed.isHeadOfBed() ? other : null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void getConnectedBlocks(Block block, Set<Block> results, List<Block> todo) {
		//Loop through all block faces (All 6 sides around the block)
		for(BlockFace face : BLOCK_FACES) {
			Block b = block.getRelative(face);
			//Check if they're both of the same type
			if(b.getType() == block.getType()) {
				//Add the block if it wasn't added already
				if(results.add(b))
					//Add this block to the list of blocks that are yet to be done.
					todo.add(b);
			}
		}
	}

    public static Set<Block> getConnectedBlocks(Block block) {
        Set<Block> set = new HashSet<>();
        LinkedList<Block> list = new LinkedList<>();

        //Add the current block to the list of blocks that are yet to be done
        list.add(block);

        //Execute this method for each block in the 'todo' list
        while((block = list.poll()) != null)
            getConnectedBlocks(block, set, list);

        return set;
    }

	/**
	 * Get the name of the plugin.
	 *
	 * @return Plugin name.
	 */
	public static String getPluginName() {
		return PLUGIN_NAME;
	}

	/**
	 * Get the current installed Dungeon Maze version name.
	 *
	 * @return The version name of the currently installed Dungeon Maze instance.
	 */
	public static String getVersionName() {
		return PLUGIN_VERSION_NAME;
	}

	/**
	 * Get the current installed Dungeon Maze version code.
	 *
	 * @return The version code of the currently installed Dungeon Maze instance.
	 */
	public static int getVersionCode() {
		return PLUGIN_VERSION_CODE;
	}

	/**
	 * Get the complete version identifier.
	 * The includes a prefixed 'v' sign, the version name and the version code between brackets.
	 *
	 * @param name True to include the plugin name in front.
	 *
	 * @return The complete version string.
	 */
	public static String getVersionComplete(boolean name) {
		return (name ? WorldPortal.PLUGIN_NAME : "") + " v" + getVersionName() + " (" + getVersionCode() + ")";
	}

	/**
	 * Get the plugin initialization time.
	 *
	 * @return Plugin initialization time.
	 */
	public Date getInitializationTime() {
		return this.initTime;
	}
}
