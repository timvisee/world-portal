package com.timvisee.worldportal.command.executable;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.CommandParts;
import com.timvisee.worldportal.command.ExecutableCommand;
import com.timvisee.worldportal.util.Profiler;
import com.timvisee.worldportal.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateWorldCommand extends ExecutableCommand {

    /**
     * Execute the command.
     *
     * @param sender The command sender.
     * @param commandReference The command reference.
     * @param commandArguments The command arguments.
     *
     * @return True if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean executeCommand(CommandSender sender, CommandParts commandReference, CommandParts commandArguments) {
        // Get and trim the preferred world name
        String worldName = commandArguments.get(0).trim();

        // Validate the world name
        if(!WorldManager.isValidWorldName(worldName)) {
            sender.sendMessage(ChatColor.DARK_RED + worldName);
            sender.sendMessage(ChatColor.DARK_RED + "The world name contains invalid characters!");
            return true;
        }

        // Get the world manager, and make sure it's valid
        WorldManager worldManager = WorldPortal.instance.getWorldManager();
        boolean showWorldManagerError = false;
        if(worldManager == null)
            showWorldManagerError = true;
        else if(!worldManager.isInit())
            showWorldManagerError = true;
        if(showWorldManagerError) {
            sender.sendMessage(ChatColor.DARK_RED + "Failed to create the world, world manager not available!");
            return true;
        }

        // Make sure the world doesn't exist
        if(worldManager.isWorld(worldName)) {
            sender.sendMessage(ChatColor.DARK_RED + "The world " + ChatColor.GOLD + worldName + ChatColor.DARK_RED + " already exists!");
            sender.sendMessage(ChatColor.YELLOW + "Use the command " + ChatColor.GOLD + "/" + commandReference.get(0) + " listworlds" + ChatColor.YELLOW + " to list all worlds.");
            sender.sendMessage(ChatColor.YELLOW + "Use the command " + ChatColor.GOLD + "/" + commandReference.get(0) + " loadworld " + worldName + ChatColor.YELLOW + " to load the world.");
            return true;
        }

        // Set the environment
        World.Environment environment = World.Environment.NORMAL;

        // Get the environment
        if(commandArguments.getCount() >= 2) {
            // Get the argument value
            final String value = commandArguments.get(1).trim().replaceAll(" ", "").replaceAll("_", "");

            // Make sure it's valid
            if (value.equalsIgnoreCase("normal"))
                environment = World.Environment.NORMAL;

            else if (value.equalsIgnoreCase("nether") || value.equalsIgnoreCase("thenether"))
                environment = World.Environment.NETHER;

            else if (value.equalsIgnoreCase("end") || value.equalsIgnoreCase("ender") || value.equalsIgnoreCase("theend"))
                environment = World.Environment.THE_END;

            else {
                sender.sendMessage(ChatColor.DARK_RED + commandArguments.get(1));
                sender.sendMessage(ChatColor.DARK_RED + "Invalid world environment given!");
                return true;
            }
        }

        // Show a status message
        sender.sendMessage(ChatColor.YELLOW + "Generating the world " + ChatColor.GOLD + worldName + ChatColor.YELLOW + "...");
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Generating a new world, expecting lag for a while...");

        // Profile the world generation
        Profiler p = new Profiler(true);

        // Create the world
        // TODO: Put this in a separate function!
        WorldCreator newWorld = new WorldCreator(worldName).environment(environment);
        World world = newWorld.createWorld();

        // Force-save the level.dat file for the world, profile the process
        Profiler pWorldSave = new Profiler(true);
        try {
            // Force-save the world, and show some status messages
            WorldPortal.instance.getLogger().info("Force saving the level.dat file for '" + world.getName() + "'...");
            world.save();
            WorldPortal.instance.getLogger().info("World saved successfully, took " + pWorldSave.getTimeFormatted() + "!");

        } catch(Exception ex) {
            WorldPortal.instance.getLogger().warning("Failed to save the world after " + pWorldSave.getTimeFormatted() + "!");
        }

        // Make sure the world instance is valid
        if(world == null) {
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "World generation failed after " + p.getTimeFormatted() + "!");
            sender.sendMessage(ChatColor.DARK_RED + "The world " + ChatColor.GOLD + worldName + ChatColor.GREEN +
                    " failed to generate after " + p.getTimeFormatted() + "!");
            return true;
        }

        // Show a status message
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "World generation finished, took " + p.getTimeFormatted() + "!");
        sender.sendMessage(ChatColor.GREEN + "The world " + ChatColor.GOLD + worldName + ChatColor.GREEN +
                " has successfully been generated, took " + p.getTimeFormatted() + "!");

        // If the command was executed by a player, teleport the player
        if(sender instanceof Player) {
            // Teleport the player
            ((Player) sender).teleport(world.getSpawnLocation());
            sender.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.GOLD + worldName + ChatColor.GREEN + "!");
        }

        // Return the result
        return true;
    }
}
