package com.timvisee.worldportal.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.permission.Permission;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.timvisee.worldportal.WPLogger;
import com.timvisee.worldportal.WorldPortal;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

public class WPPermissionsManager {
	
	private Server s;
	private WPLogger log;
	
	// Current permissions system that is used
	private PermissionsSystemType permsType = PermissionsSystemType.NONE;
	
	// Permissions Ex
	private PermissionManager pexPerms;
	
	// Group manager essentials
	private GroupManager groupManagerPerms;

	// zPermissions
	private ZPermissionsService zPermissionsService;
	
	// Vault
	public Permission vaultPerms = null;
	
	/**
	 * Constructor
	 * @param s Server
	 * @param log World Portal logger
	 */
	public WPPermissionsManager(Server s, WPLogger log) {
		this.s = s;
		this.log = log;
	}
	
	/**
	 * Get the World Portal logger
	 * @return World Portal logger
	 */
	public WPLogger getWPLogger() {
		return this.log;
	}
	
	/**
	 * Set the World Portal logger
	 * @param log World Portal logger
	 */
	public void setWPLogger(WPLogger log) {
		this.log = log;
	}
	
	/**
	 * Return the permissions system where the permissions manager is currently hooked into
	 * @return permissions system type
	 */
	public PermissionsSystemType getUsedPermissionsSystemType() {
		return this.permsType;
	}
	
	/**
	 * Check if the permissions manager is currently hooked into any of the supported permissions systems
	 * @return false if there isn't any permissions system used
	 */
	public boolean isEnabled() {
		return !permsType.equals(PermissionsSystemType.NONE);
	}
	
	/**
	 * Setup and hook into the permissions systems
	 * @return the detected permissions system
	 */
	public PermissionsSystemType setUp() {
		// Define the plugin manager
		final PluginManager pm = this.s.getPluginManager();
		
		// Reset used permissions system type
		this.permsType = PermissionsSystemType.NONE;
		
		// Check if permissions usage is globally disabled
		if(!WorldPortal.instance.getConfig().getBoolean("permissions.usePermissions", true)) {
			this.log.info("Permissions usage disabled in config file!");
			return PermissionsSystemType.NONE;
		}
		
		// PermissionsEx
		// Check if PermissionsEx is allowed to be used
		if(isPermissionsSystemAllowed("PermissionsEx")) {
			// Check if PermissionsEx is available
			try {
				Plugin pex = pm.getPlugin("PermissionsEx");
				if(pex != null) {
					pexPerms = PermissionsEx.getPermissionManager();
					if(pexPerms != null) {
						permsType = PermissionsSystemType.PERMISSIONS_EX;
						
						this.log.info("Hooked into PermissionsEx!");
						return permsType;
					}
				}
			} catch(Exception ex) {
				// An error occured, show a warning message
				this.log.error("Error while hooking into PermissionsEx!");
			}
		} else {
			// Show a warning message
			this.log.info("Not checking for PermissionsEx, disabled in config file!");
		}
			
		// PermissionsBukkit
		// Check if PermissionsBukkit is allowed to be used
		if(isPermissionsSystemAllowed("PermissionsBukkit")) {
			// Check if PermissionsBukkit is available
			try {
				Plugin bukkitPerms = pm.getPlugin("PermissionsBukkit");
				if(bukkitPerms != null) {
					permsType = PermissionsSystemType.PERMISSIONS_BUKKIT;
					this.log.info("Hooked into PermissionsBukkit!");
					return permsType;
				}
			} catch(Exception ex) {
				// An error occured, show a warning message
				this.log.error("Error while hooking into PermissionsBukkit!");
			}
		} else {
			// Show a warning message
			this.log.info("Not checking for PermissionsBukkit, disabled in config file!");
		}
		
		// bPermissions
		// Check if bPermissions is allowed to be used
		if(isPermissionsSystemAllowed("bPermissions")) {
			// Check if bPermissions is available
			try {
				Plugin testBPermissions = pm.getPlugin("bPermissions");
				if(testBPermissions != null) {
					permsType = PermissionsSystemType.B_PERMISSIONS;
					this.log.info("Hooked into bPermissions!");
					return permsType;
				}
			} catch(Exception ex) {
				// An error occured, show a warning message
				this.log.error("Error while hooking into bPermissions!");
			}
		} else {
			// Show a warning message
			this.log.info("Not checking for bPermissions, disabled in config file!");
		}
		
		// Essentials Group Manager
		// Check if Essentials Group Manager is allowed to be used
		if(isPermissionsSystemAllowed("EssentialsGroupManager")) {
			// Check if Essentials Group Manager is available
			try {
				final Plugin GMplugin = pm.getPlugin("GroupManager");
				if (GMplugin != null && GMplugin.isEnabled()) {
					permsType = PermissionsSystemType.ESSENTIALS_GROUP_MANAGER;
					groupManagerPerms = (GroupManager)GMplugin;
					this.log.error("Hooked into Essentials Group Manager!");
		            return permsType;
				}
			} catch(Exception ex) {
				// An error occured, show a warning message
				this.log.error("Error while hooking into Essentials Group Manager!");
			}
		} else {
			// Show a warning message
			this.log.info("Not checking for Essentials Group Manager, disabled in config file!");
		}

		// zPermissions
		// Check if zPermissions is allowed to be used
		if(isPermissionsSystemAllowed("zPermissions")) {
			// Check if zPermissions is available
			try {
				Plugin testzPermissions = pm.getPlugin("zPermissions");
				if(testzPermissions != null){
					zPermissionsService = Bukkit.getServicesManager().load(ZPermissionsService.class);
					if(zPermissionsService != null){
						permsType = PermissionsSystemType.Z_PERMISSIONS;
						this.log.info("Hooked into zPermissions!");
						return permsType;
					}
				}
			} catch(Exception ex) {
				// An error occured, show a warning message
				this.log.error("Error while hooking into zPermissions!");
			}
		} else {
			// Show a warning message
			this.log.info("Not checking for zPermissions, disabled in config file!");
		}
		
		// Vault
		// Check if Vault is allowed to be used
		if(isPermissionsSystemAllowed("Vault")) {
			// Check if Vault is available
			try {
				final Plugin vaultPlugin = pm.getPlugin("Vault");
				if (vaultPlugin != null && vaultPlugin.isEnabled()) {
					RegisteredServiceProvider<Permission> permissionProvider = this.s.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			        if (permissionProvider != null) {
			            vaultPerms = permissionProvider.getProvider();
			            if(vaultPerms.isEnabled()) {
			            	permsType = PermissionsSystemType.VAULT;
			            	this.log.error("Hooked into Vault Permissions!");
			    		    return permsType;
			            } else {
			            	this.log.info("Not using Vault Permissions, Vault Permissions is disabled!");
			            }
			        }
				}
			} catch(Exception ex) {
				// An error occured, show a warning message
				this.log.error("Error while hooking into Vault Permissions!");
			}
		} else {
			// Show a warning message
			this.log.info("Not checking for Vault, disabled in config file!");
		}

	    // No recognized permissions system found
	    permsType = PermissionsSystemType.NONE;
	    
	    // Show a status message
	    this.log.info("No supported permissions system found! Permissions disabled!");
	    
	    return PermissionsSystemType.NONE;
    }
	
	/**
	 * Break the hook with the current hooked permissions system
	 */
	public void unhook() {
        // Break the WorldGuard hook
        this.permsType = PermissionsSystemType.NONE;
        
        if(!permsType.equals(PermissionsSystemType.NONE))
        	this.log.info("Unhooked from " + this.permsType.getName() + "!");
        else
        	this.log.info("Unhooked from Permissions!");
	}
	
	/**
	 * Method called when a plugin is being enabled
	 * @param e Event instance
	 */
	public void onPluginEnable(PluginEnableEvent e) {
		Plugin p = e.getPlugin();
		String pn = p.getName();
		
		// Is the WorldGuard plugin enabled
		if(pn.equals("PermissionsEx") || pn.equals("PermissionsBukkit") ||
				pn.equals("bPermissions") || pn.equals("GroupManager") ||
				pn.equals("zPermissions") || pn.equals("Vault")) {
			this.log.info(pn + " plugin enabled, updating hooks!");
			setUp();
		}
	}
	
	/**
	 * Method called when a plugin is being disabled
	 * @param e Event instance
	 */
	public void onPluginDisable(PluginDisableEvent e) {
		Plugin p = e.getPlugin();
		String pn = p.getName();
		
		// Is the WorldGuard plugin disabled
		if(pn.equals("PermissionsEx") || pn.equals("PermissionsBukkit") ||
				pn.equals("bPermissions") || pn.equals("GroupManager") ||
				pn.equals("zPermissions") || pn.equals("Vault")) {
			this.log.info(pn + " plugin disabled, updating hooks!");
			setUp();
		}
	}
	
	/**
	 * Check if the player has permission. If no permissions system is used, the player has to be OP
	 * @param p player
	 * @param permsNode permissions node
	 * @return true if the player is permitted
	 */
	public boolean hasPermission(Player p, String permsNode) {
		return hasPermission(p, permsNode, p.isOp());
	}
	
	/**
	 * Check if a player has permission
	 * @param p player
	 * @param permsNode permission node
	 * @param def default if no permissions system is used
	 * @return true if the player is permitted
	 */
	public boolean hasPermission(Player p, String permsNode, boolean def) {
		if(!isEnabled()) {
			// No permissions system is used, return default
			return def;
		}
		
		switch (this.permsType) {
		case PERMISSIONS_EX: // Permissions Ex
			PermissionUser user  = PermissionsEx.getUser(p);
			return user.has(permsNode);
			
		case PERMISSIONS_BUKKIT: // Permissions Bukkit
			return p.hasPermission(permsNode);
			
		case B_PERMISSIONS: // bPermissions
			return ApiLayer.hasPermission(p.getWorld().getName(), CalculableType.USER, p.getName(), permsNode);
			
		case ESSENTIALS_GROUP_MANAGER: // Essentials Group Manager
			final AnjoPermissionsHandler handler = groupManagerPerms.getWorldsHolder().getWorldPermissions(p);
			if (handler == null)
				return false;
			return handler.has(p, permsNode);
		case Z_PERMISSIONS: // zPermissions
			Map<String, Boolean> perms = zPermissionsService.getPlayerPermissions(p.getWorld().getName(), null, p.getName());
			return perms.getOrDefault(permsNode, def);
				
		case VAULT: // Vault
			return vaultPerms.has(p, permsNode);
			
		case NONE: // Not hooked into any permissions system, return default
			return def;
			
		default: // Something went wrong, return false to prevent problems
			return false;
		}
	}

	/**
	 * Get the primary group of a player
	 * @param p The player to get the primary group from
	 * @return
	 */
	public String getPrimaryGroup(Player p) {
		List<String> groups = getGroups(p);
		
		// Make sure the list isn't null
		if(groups == null)
			return "";
		
		// Make sure there's any item inside the groups list
		if(groups.size() == 0)
			return "";
		
		// Return the first item
		return groups.get(0);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getGroups(Player p) {
		if(!isEnabled()) {
			// No permissions system is used, return an empty list
			return new ArrayList<String>();
		}
		
		switch (this.permsType) {
		case PERMISSIONS_EX:
			// Permissions Ex
			PermissionUser user  = PermissionsEx.getUser(p);
			return Arrays.asList(user.getGroupsNames());
			
		case PERMISSIONS_BUKKIT:
			// Permissions Bukkit
			// Permissions Bukkit doesn't support group, return an empty list
			return new ArrayList<String>();
			
		case B_PERMISSIONS:
			// bPermissions
			return Arrays.asList(ApiLayer.getGroups(p.getName(), CalculableType.USER, p.getName()));
			
		case ESSENTIALS_GROUP_MANAGER:
			// Essentials Group Manager
			final AnjoPermissionsHandler handler = groupManagerPerms.getWorldsHolder().getWorldPermissions(p);
			if (handler == null)
				return new ArrayList<String>();
			return Arrays.asList(handler.getGroups(p.getName()));
			
		case Z_PERMISSIONS:
			//zPermissions
			return new ArrayList<String>(zPermissionsService.getPlayerGroups(p.getName()));
			
		case VAULT:
			// Vault
			return Arrays.asList(vaultPerms.getPlayerGroups(p));
			
		case NONE:
			// Not hooked into any permissions system, return an empty list
			return new ArrayList<String>();
			
		default:
			// Something went wrong, return an empty list to prevent problems
			return new ArrayList<String>();
		}
	}
	
	/**
	 * Check if a permissions system is allowed to be used according to the configuration file
	 * @param configNodeName The configuration node name of the permissions system
	 * @return True if this system is allowed to be used
	 */
	private boolean isPermissionsSystemAllowed(String configNodeName) {
		return WorldPortal.instance.getConfig().getBoolean("permissions.permissionsSystems." + configNodeName + ".enabled", true);
	}
	
	public enum PermissionsSystemType {
		NONE("None"),
		PERMISSIONS_EX("Permissions Ex"),
		PERMISSIONS_BUKKIT("Permissions Bukkit"),
		B_PERMISSIONS("bPermissions"),
		ESSENTIALS_GROUP_MANAGER("Essentials Group Manager"),
		Z_PERMISSIONS("zPermissions"),
		VAULT("Vault");

		public String name;
		
		PermissionsSystemType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
}