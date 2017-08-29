package com.timvisee.worldportal.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.timvisee.worldportal.WorldPortal;

public class WPCommandHandler {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("worldportal") || commandLabel.equalsIgnoreCase("wp")) {
			
			if(args.length == 0) {
				sender.sendMessage(ChatColor.DARK_RED + "Unknown command!");
				sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + ChatColor.YELLOW + "to view help");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver") || args[0].equalsIgnoreCase("v")
					 || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("a")) {
				
				// Check wrong command arguments
				if(args.length != 1) {
					sender.sendMessage(ChatColor.DARK_RED + "Invalid command arguments!");sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + ChatColor.YELLOW + "to view help");
					return true;
				}
				
				String ver = WorldPortal.instance.getVersion();
				sender.sendMessage(ChatColor.YELLOW + "This server is running World Portal v" + ver);
				sender.sendMessage(ChatColor.YELLOW + "World Portal is made by Tim Visee - timvisee.com");
				return true;
			}
			
			sender.sendMessage(ChatColor.DARK_RED + "Unknown command!");
			sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + ChatColor.YELLOW + "to view help");
			return true;
		}
		
		return false;
	}
}
