package com.timvisee.worldportal.util;

public class FlagUtils {
	
	/**
	 * Check whether a flag is set
	 * @param args The list of flags
	 * @param flag The flag to check for
	 * @return True if this flag exists
	 */
	public boolean isFlagSet(String args[], String flag) {
		for(int i = 0; i < args.length; i++)
			if(args[i].equalsIgnoreCase("-" + flag) || args[i].equalsIgnoreCase("/" + flag))
				return true;
		return false;
	}
	
	/**
	 * Get the argument of a flag
	 * @param args The list of flags
	 * @param flag The flag to get the argument for
	 * @param def Default argument
	 * @return The flag argument, or the default argument if the argument couldn't be retrieved
	 */
	public String getFlagArgument(String args[], String flag, String def) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("-" + flag) || args[i].equalsIgnoreCase("/" + flag)) {
				if(i+1 < args.length)
					return args[i+1].toString();
				else
					return def;
			}
		}
		return def;
	}
	
	/**
	 * Get the argument of a flag
	 * @param args The list of flags
	 * @param flag The flag to get the argument for
	 * @return The flag argument, or an empty string if the argument couldn't be retrieved
	 */
	public String getFlagArgument(String args[], String flag) {
		return getFlagArgument(args, flag, "");
	}
}
