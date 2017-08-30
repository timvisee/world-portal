package com.timvisee.worldportal.command.executable;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.CommandParts;
import com.timvisee.worldportal.command.ExecutableCommand;
import com.timvisee.worldportal.util.Profiler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveCommand extends ExecutableCommand {

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
        sender.sendMessage(ChatColor.YELLOW + "Saving portals...");

        // Save
        WorldPortal.instance.saveWorldPortals();

        // World Portal reloaded, show a status message
        sender.sendMessage(ChatColor.GREEN + "All portals have been saved successfully, took " + p.getTimeFormatted() + "!");
        return true;
    }
}
