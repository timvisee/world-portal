package com.timvisee.worldportal.command.executable;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.CommandParts;
import com.timvisee.worldportal.command.ExecutableCommand;
import com.timvisee.worldportal.world.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListWorldCommand extends ExecutableCommand {

    /**
     * Execute the command.
     *
     * @param sender           The command sender.
     * @param commandReference The command reference.
     * @param commandArguments The command arguments.
     *
     * @return True if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean executeCommand(CommandSender sender, CommandParts commandReference, CommandParts commandArguments) {
        // Get the world manager and make sure it's valid
        WorldManager worldManager = WorldPortal.instance.getWorldManager();
        if(worldManager == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Error, failed to list the worlds!");
            return true;
        }

        // Get the world list
        List<String> worlds = worldManager.getWorlds();

        // Show the list of Dungeon Maze worlds
        sender.sendMessage(ChatColor.GOLD + "==========[ " + WorldPortal.getPluginName().toUpperCase() + " WORLDS ]==========");
        sender.sendMessage(ChatColor.GOLD + "Worlds:");
        if(worlds.size() > 0) {
            for(String worldName : worlds) {
                if(worldManager.isWorldLoaded(worldName))
                    sender.sendMessage(ChatColor.WHITE + " " + worldName + ChatColor.GREEN + ChatColor.ITALIC + " (Loaded)");
                else
                    sender.sendMessage(ChatColor.WHITE + " " + worldName + ChatColor.GRAY + ChatColor.ITALIC + " (Not Loaded)");
            }
        } else
            // No other world available, show a message
            sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + " No worlds available!");

        // Return the result
        return true;
    }
}
