package com.timvisee.worldportal.command.executable;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.CommandParts;
import com.timvisee.worldportal.command.ExecutableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static jdk.nashorn.internal.runtime.ECMAErrors.getMessage;

public class InfoCommand extends ExecutableCommand {

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
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "You can only view your info in-game.");
            return true;
        }

        if(WorldPortal.instance.createPortal.isPlayerInCreationMode((Player) sender))
            getMessage("infoCreationModeEnabled", "&e[WorldPortal] Creation-mode &aenabled");
        else
            getMessage("infoCreationModeDisabled", "&e[WorldPortal] Creation-mode &4disabled");
        if(WorldPortal.instance.WPRemoveUsersEnabled((Player) sender))
            getMessage("infoRemoveModeEnabled", "&e[WorldPortal] Remove-mode &aenabled");
        else
            sender.sendMessage(getMessage("infoRemoveModeDisabled", "&e[WorldPortal] Remove-mode &4disabled"));

        return true;
    }
}
