package com.timvisee.worldportal.world;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.util.Profiler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedDeclaration")
public class WorldManager {

    /**
     * Minecraft world name validation Regex.
     */
    private static final String MINECRAFT_WORLD_NAME_REGEX = "^[[\\p{Alnum}]_-]+";

    /**
     * Defines whether the world manager is initialized or not.
     */
    private boolean init = false;

    /**
     * Defines all the worlds in the server.
     */
    private List<String> worlds = new ArrayList<>();

    /**
     * Constructor. This won't initialize the manager immediately.
     */
    public WorldManager() {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param init True to initialize the world manager immediately.
     */
    public WorldManager(boolean init) {
        if(init)
            init();
    }

    /**
     * Initialize the world manager.
     *
     * @return True on success, false on failure.
     */
    public boolean init() {
        return init(true, false);
    }

    /**
     * Initialize the world manager.
     *
     * @param refresh True to refresh the worlds, false otherwise.
     * @param preload True to preload the worlds, false otherwise.
     *
     * @return True on success, false on failure. True will also be returned if the world manager was already
     * initialized.
     */
    public boolean init(boolean refresh, boolean preload) {
        // Make sure the world manager isn't initialized already
        if(isInit())
            return true;

        // Refresh the worlds
        if(refresh)
            if(!refresh())
                return false;

        // Set whether the world manager is initialized, return the result
        this.init = true;
        return true;
    }

    /**
     * Check whether the world manager is initialized.
     *
     * @return True if the world manager is initialized, false otherwise.
     */
    public boolean isInit() {
        return this.init;
    }

    /**
     * Destroy the world manager.
     *
     * @param force True for force destroy the world manager.
     *
     * @return True if the world manager was successfully destroyed. True will also be returned if the world manager
     * wasn't initialized.
     */
    public boolean destroy(boolean force) {
        // Make sure the world manager is initialized or the destruction must be forced
        if(!isInit() && !force)
            return true;

        // TODO: Properly unload the manager and all worlds!?
        this.init = false;
        return true;
    }

    /**
     * Refresh the list with worlds.
     *
     * @return True on success, false on failure.
     */
    public boolean refresh() {
        // Get all the filesystem objects in the worlds directory of the server
        File[] files = Bukkit.getWorldContainer().listFiles();

        // List all the worlds
        if(files != null) {
            // Reset the worlds list
            this.worlds.clear();

            // Loop through all the filesystem objects in the worlds directory
            for(File worldDirectory : files) {
                // Make sure the file is a directory
                if(!worldDirectory.isDirectory())
                    continue;

                // Get the name of the world
                String worldName = worldDirectory.getName();

                // Make sure this world is valid
                if(!isWorld(worldName))
                    continue;

                // Add the world to the list
                this.worlds.add(worldName);
            }
        }

        // Return the result
        return true;
    }

    /**
     * Get all the worlds in the server. The worlds don't have to be loaded.
     *
     * @return All worlds.
     */
    public List<String> getWorlds() {
        return this.worlds;
    }

    /**
     * Check whether a world exists. The world doesn't need to be loaded.
     *
     * @param worldName The name of the world to check for.
     *
     * @return True if any world with this name exists, false otherwise.
     */
    public boolean isWorld(String worldName) {
        // Check whether the world exists by it's level data, return the result
        File worldLevelFile = new File(Bukkit.getWorldContainer(), worldName + "/level.dat");
        return worldLevelFile.exists();
    }

    /**
     * Check whether a world is loaded.
     *
     * @param worldName The name of the world to check for.
     *
     * @return True if the world is loaded, false otherwise.
     */
    public boolean isWorldLoaded(String worldName) {
        // Loop trough each loaded world to check if it's loaded
        for(World entry : Bukkit.getWorlds())
            if(entry.getName().equals(worldName))
                return true;

        // Try to get the world from the Bukkit server instance
        if(Bukkit.getWorld(worldName) != null)
            return true;

        // The world doesn't seem to be loaded, return false
        return false;
    }

    /**
     * Get the main world.
     *
     * @return The main world.
     */
    public World getMainWorld() {
        // TODO: Fix this method!
        return Bukkit.getWorlds().get(0);
    }

    /**
     * Check whether the world is the main world.
     *
     * @param world The world to check.
     *
     * @return True if the world is the main world.
     */
    public boolean isMainWorld(World world) {
        return getMainWorld().equals(world);
    }

    /**
     * Check whether the world is the main world.
     *
     * @param worldName The name of the world to check.
     *
     * @return True if the world is the main world.
     */
    public boolean isMainWorld(String worldName) {
        return getMainWorld().getName().equals(worldName);
    }

    /**
     * Load a world if it isn't loaded yet.
     *
     * @param worldName The name of the world to load.
     *
     * @return The world instance if the world is loaded, null otherwise.
     * The world instance will also be returned if the world was already loaded.
     */
    public World loadWorld(String worldName) {
        // Make sure the world exists
        if(!isWorld(worldName))
            return null;

        // Make sure the world isn't loaded yet
        if(isWorldLoaded(worldName))
            return Bukkit.getServer().getWorld(worldName);

        // Profile the world loading
        Profiler p = new Profiler(true);

        // Show a status message
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Loading world, expecting lag for a while...");

        // Store the loaded world
        World world;

        // Load the world
        try {
            // Set up the world creator to load the world
            WorldCreator newWorld = new WorldCreator(worldName);

            // Load the world
            world = newWorld.createWorld();

        } catch(Exception ex) {
            WorldPortal.instance.getLogger().info("Failed to load the world '" + worldName + ", after " + p.getTimeFormatted() + "'!");
            return null;
        }

        // Make sure the world instance is valid
        if(world == null) {
            WorldPortal.instance.getLogger().info("Failed to load the world '" + worldName + ", after " + p.getTimeFormatted() + "'!");
            return null;
        }

        // Show a status message, return the result
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "World loaded successfully, took " + p.getTimeFormatted() + "!");
        return world;
    }

    /**
     * Unload a world if it's loaded.
     *
     * @param worldName The name of the world to unload.
     *
     * @return True if any world was unloaded, false otherwise. True will also be returned if the world was not loaded.
     */
    public boolean unloadWorld(String worldName) {
        // Make sure the world exists
        if(!isWorld(worldName))
            return false;

        // Make sure the world isn't loaded yet
        if(!isWorldLoaded(worldName))
            return true;

        // Show a status message
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Unloading world, expecting lag for a while...");

        // Profile the world loading
        Profiler p = new Profiler(true);

        // Automatically save the world before unloading
        // TODO: Replace this with a this.getWorld() method!
        World world = Bukkit.getWorld(worldName);
        if(world != null) {
            try {
                WorldPortal.instance.getLogger().info("Auto saving world before unloading...");
                world.save();
                WorldPortal.instance.getLogger().info("World successfully saved, took " + p.getTimeFormatted() + "!");
            } catch(Exception ex) {
                WorldPortal.instance.getLogger().warning("Failed to save the world! (Error: " + ex.getMessage() + ")");
            }
        } else
            WorldPortal.instance.getLogger().info("Unable to auto save the world before unloading!");

        // Unload and save the world
        boolean unloaded = Bukkit.unloadWorld(worldName, true);

        // Show a status message, return the result
        if(unloaded)
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "World unloaded successfully, took " + p.getTimeFormatted() + "!");
        else
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Failed to unload the world!");
        return unloaded;
    }

    /**
     * Check whether a Minecraft world name is valid.
     * <p/>
     * param worldName The world name to validate.
     *
     * @return True if the world name is valid, false otherwise.
     */
    public static boolean isValidWorldName(String worldName) {
        // Do a regex check
        return Pattern.compile(MINECRAFT_WORLD_NAME_REGEX).matcher(worldName).matches();
    }
}
