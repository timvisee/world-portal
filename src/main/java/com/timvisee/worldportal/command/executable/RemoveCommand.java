package com.timvisee.worldportal.command.executable;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.CommandParts;
import com.timvisee.worldportal.command.ExecutableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("Duplicates")
public class RemoveCommand extends ExecutableCommand {

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
        // The command sender must be in game
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "You can only remove portals in-game.");
            return true;
        }

        // Define whether to enable
        boolean enable = true;

        // Get whether the restart should be forced from the command arguments
        if(commandArguments.getCount() >= 1) {
            String arg = commandArguments.get(0);

            // Check whether the argument equals 'enable'
            if(arg.equalsIgnoreCase("enable") || arg.equalsIgnoreCase("enabled") || arg.equalsIgnoreCase("start"))
                enable = true;
            else if(arg.equalsIgnoreCase("disable") || arg.equalsIgnoreCase("disabled") || arg.equalsIgnoreCase("stop"))
                enable = false;

                // TODO: Put this in some sort of utility class, because it's used multiple times!
            else if(arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("t") || arg.equalsIgnoreCase("yes") || arg.equalsIgnoreCase("y"))
                enable = true;

            else if(arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("f") || arg.equalsIgnoreCase("no") || arg.equalsIgnoreCase("n"))
                enable = false;

            else {
                sender.sendMessage(ChatColor.DARK_RED + arg);
                sender.sendMessage(ChatColor.DARK_RED + "Invalid argument!");
                return true;
            }
        }

        // Enable creation mode
        if(enable)
            WorldPortal.instance.toggleWPRemoveUsers((Player) sender);
        else if(WorldPortal.instance.WPRemoveUsersEnabled((Player) sender))
            WorldPortal.instance.toggleWPRemoveUsers((Player) sender);
        return true;
    }
}
