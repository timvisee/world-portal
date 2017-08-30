package com.timvisee.worldportal.command.executable;

import com.timvisee.worldportal.PermissionsManager;
import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.CommandParts;
import com.timvisee.worldportal.command.ExecutableCommand;
import com.timvisee.worldportal.util.Profiler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadPermissionsCommand extends ExecutableCommand {

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
        // Profile the permissions reload process
        Profiler p = new Profiler(true);

        // Show a status message
        sender.sendMessage(ChatColor.YELLOW + "Reloading permissions...");
        WorldPortal.instance.getLogger().info("Reloading permissions...");

        // Get the permissions manager and make sure it's valid
        PermissionsManager permissionsManager = WorldPortal.instance.getPermissionsManager();
        if(permissionsManager == null) {
            WorldPortal.instance.getLogger().info("Failed to access the permissions manager after " + p.getTimeFormatted() + "!");
            sender.sendMessage(ChatColor.DARK_RED + "Failed to access the permissions manager after " + p.getTimeFormatted() + "!");
            return true;
        }

        // Reload the permissions service, show an error on failure
        if(!permissionsManager.reload()) {
            WorldPortal.instance.getLogger().info("Failed to reload permissions after " + p.getTimeFormatted() + "!");
            sender.sendMessage(ChatColor.DARK_RED + "Failed to reload permissions after " + p.getTimeFormatted() + "!");
            return true;
        }

        // Show a success message
        WorldPortal.instance.getLogger().info("Permissions reloaded successfully, took " + p.getTimeFormatted() + "!");
        sender.sendMessage(ChatColor.GREEN + "Permissions reloaded successfully, took " + p.getTimeFormatted() + "!");

        // Get and show the permissions system being used
        String permissionsSystem = ChatColor.RED + "None";
        if(permissionsManager.isHooked())
            permissionsSystem = ChatColor.GOLD + permissionsManager.getUsedPermissionsSystemType().getName();
        WorldPortal.instance.getLogger().info("Used permissions system: " + permissionsSystem);
        sender.sendMessage(ChatColor.GREEN + "Used permissions system: " + permissionsSystem);
        return true;
    }
}
