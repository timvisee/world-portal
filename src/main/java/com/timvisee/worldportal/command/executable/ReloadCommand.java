package com.timvisee.worldportal.command.executable;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.CommandParts;
import com.timvisee.worldportal.command.ExecutableCommand;
import com.timvisee.worldportal.util.Profiler;
import com.timvisee.worldportal.world.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends ExecutableCommand {

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
        // Profile the reload process
        Profiler p = new Profiler(true);

        // Show a status message
        sender.sendMessage(ChatColor.YELLOW + "Reloading " + WorldPortal.getPluginName() + "...");

        // Reload the configuration
        WorldPortal.instance.reloadConfig();
        sender.sendMessage(ChatColor.YELLOW + "Reloaded the configuration!");

        // Get the world manager to reload the world list, and make sure it's valid
        WorldManager worldManager = WorldPortal.instance.getWorldManager();
        if(worldManager != null) {
            worldManager.refresh();
            sender.sendMessage(ChatColor.YELLOW + "Reloaded the worlds!");
        } else
            sender.sendMessage(ChatColor.DARK_RED + "Failed to reload the worlds!");

        // World Portal reloaded, show a status message
        sender.sendMessage(ChatColor.GREEN + WorldPortal.getPluginName() + " has been reloaded successfully, took " + p.getTimeFormatted() + "!");
        return true;
    }
}
