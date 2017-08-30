package com.timvisee.worldportal.command.executable;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.CommandParts;
import com.timvisee.worldportal.command.ExecutableCommand;
import com.timvisee.worldportal.world.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends ExecutableCommand {

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
        // Make sure the command is executed by an in-game player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "You need to be in-game to use this command!");
            return true;
        }

        // Get the player and the world name to teleport to
        Player player = (Player) sender;
        String worldName = commandArguments.get(0);

        // Get the world manager, and make sure it's valid
        WorldManager worldManager = WorldPortal.instance.getWorldManager();
        boolean showWorldManagerError = false;
        if(worldManager == null)
            showWorldManagerError = true;
        else if(!worldManager.isInit())
            showWorldManagerError = true;
        if(showWorldManagerError) {
            sender.sendMessage(ChatColor.DARK_RED + "Failed to teleport, world manager not available!");
            return true;
        }

        // Make sure the world exists
        if(!worldManager.isWorld(worldName)) {
            sender.sendMessage(ChatColor.DARK_RED + worldName);
            sender.sendMessage(ChatColor.DARK_RED + "This world doesn't exists!");
            return true;
        }

        // Try to load the world
        World world = worldManager.loadWorld(worldName);

        // Make sure the world was loaded successfully
        if(world == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Failed to teleport, unable to load the world!");
            return true;
        }

        // Determine the location to teleport to
        Location target = null;

        // Parse the spawn parameter
        if(commandArguments.getCount() == 1 || commandArguments.get(1).equalsIgnoreCase("spawn"))
            target = WorldPortal.instance.getFixedSpawnLocation(world.getSpawnLocation());

        // Show a warning of not enough parameters are given
        if(commandArguments.getCount() > 4 || (target == null && commandArguments.getCount() == 2)) {
            sender.sendMessage(ChatColor.DARK_RED + "Specify the X, Y and Z or the X and Z coordinates.");
            return true;
        }

        // Parse the XYZ
        if(commandArguments.getCount() == 4) {
            target = world.getSpawnLocation();
            target.setX(Double.parseDouble(commandArguments.get(1)));
            target.setY(Double.parseDouble(commandArguments.get(2)));
            target.setZ(Double.parseDouble(commandArguments.get(3)));

        } else if(commandArguments.getCount() == 3) {
            target = world.getSpawnLocation();
            target.setX(Double.parseDouble(commandArguments.get(1)));
            target.setZ(Double.parseDouble(commandArguments.get(2)));
            target.setY(world.getHighestBlockYAt(target));
            target = WorldPortal.instance.getFixedSpawnLocation(target);
        }

        // Teleport the player
        if(target != null)
            WorldPortal.instance.teleportPlayer((Player) sender, target, false);

        // Show a status message and return true
        player.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.GOLD + worldName + ChatColor.GREEN + "!");
        return true;
    }
}
