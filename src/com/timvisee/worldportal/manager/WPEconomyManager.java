package com.timvisee.worldportal.manager;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.timvisee.SimpleEconomy.SimpleEconomyHandler.SimpleEconomyHandler;
import com.timvisee.worldportal.WPLogger;
import com.timvisee.worldportal.WorldPortal;

import cosine.boseconomy.BOSEconomy;

public class WPEconomyManager {
	
	private EconomySystemType economyType = EconomySystemType.NONE;
	private Server s;
	private WPLogger log;

	// Simple Economy
	public static SimpleEconomyHandler simpleEconomyHandler;
	
	// BOSEconomy
	private BOSEconomy BOSEcon = null;
	
	// Vault
    public static Economy vaultEconomy = null;
	
	/**
	 * Constructor
	 * @param s Server
	 * @param log World Portal logger
	 */
	public WPEconomyManager(Server s, WPLogger log) {
		this.s = s;
		this.log = log;
	}
	
	/**
	 * Get the World Portal logger instance
	 * @return World Portal logger instance
	 */
	public WPLogger getWPLogger() {
		return this.log;
	}
	
	/**
	 * Set the World Portal logger instance
	 * @param log World Portal logger instance
	 */
	public void setWPLogger(WPLogger log) {
		this.log = log;
	}
	
	/**
	 * Get the used economy system where the economy manager is hooked into
	 * @return economy system
	 */
	public EconomySystemType getUsedEconomySystemType() {
		return this.economyType;
	}
	
	/**
	 * Check if the economy manager hooked into any of the supported economy systems
	 * @return false if there isn't any economy system used
	 */
	public boolean isEnabled() {
		return !economyType.equals(EconomySystemType.NONE);
	}
	
	/**
	 * Check if the current economy system support banks
	 * @return true if supported
	 */
	public boolean hasBankSupport() {
		if(!isEnabled()) {
			// Not hooked into any permissions system, return false
			return false;
		}
		
		switch (this.economyType) {
		case BOSECONOMY:
			// BOSEconomy
			// These systems will support banking
			return true;
			
		case VAULT:
			// Vault
			return vaultEconomy.hasBankSupport();
			
		default:
			// Return false as default
			return false;
		}
	}
	
	public EconomySystemType setUp() {
		// Define the plugin manager
		final PluginManager pm = this.s.getPluginManager();
		
		// Reset used economy system type
		economyType = EconomySystemType.NONE;
		
		// Check if economy usage is globally disabled
		if(!WorldPortal.instance.getConfig().getBoolean("economy.useEconomy", true)) {
			this.log.info("Economy usage disabled in config file!");
			return EconomySystemType.NONE;
		}

		// Simple Economy
		// Check if PermissionsEx is allowed to be used
		if(isEconomySystemAllowed("SimpleEconomy")) {
			// Check if Simple Economy is available
			Plugin simpleEconomy = pm.getPlugin("Simple Economy"); //TODO Rename plugin without space when updated
			if(simpleEconomy != null) {
				simpleEconomyHandler = ((com.timvisee.SimpleEconomy.SimpleEconomy) simpleEconomy).getHandler();
				economyType = EconomySystemType.SIMPLE_ECONOMY;
				this.log.info("Hooked into Simple Economy!");
			    return EconomySystemType.SIMPLE_ECONOMY;
			}
		} else {
			// Show a warning message
			this.log.info("Not checking for Simple Economy, disabled in config file!");
		}
		
		// BOSEconomy
		// Check if BOSEconomy is allowed to be used
		if(isEconomySystemAllowed("BOSEconomy")) {
			// Check if BOSEconomy is available
		    Plugin bose = pm.getPlugin("BOSEconomy");
		    if(bose != null) {
		        BOSEcon = (BOSEconomy)bose;
				economyType = EconomySystemType.BOSECONOMY;
				this.log.info("Hooked into BOSEconomy!");
			    return EconomySystemType.BOSECONOMY;
		    }
		} else {
			// Show a warning message
			this.log.info("Not checking for BOSEconomy, disabled in config file!");
		}
		
		// Vault
		// Check if Vault is allowed to be used
		if(isEconomySystemAllowed("Vault")) {
			// Check if Vault is available
		    final Plugin vaultPlugin = pm.getPlugin("Vault");
			if (vaultPlugin != null && vaultPlugin.isEnabled()) {
				RegisteredServiceProvider<Economy> economyProvider = this.s.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		        if (economyProvider != null) {
		            vaultEconomy = economyProvider.getProvider();
		            if(vaultEconomy.isEnabled()) {
		            	economyType = EconomySystemType.VAULT;
		            	this.log.info("Hooked into Vault Economy!");
		    		    return EconomySystemType.VAULT;
		            } else
		            	this.log.info("Not using Vault Economy, Vault Economy is disabled!");
		        }
			}
		} else {
			// Show a warning message
			this.log.info("Not checking for Vault, disabled in config file!");
		}
		
	    // No recognized economy system found
	    economyType = EconomySystemType.NONE;
	    this.log.info("No supported economy system found! Economy disabled!");
	    
	    return EconomySystemType.NONE;
	}
	
	/**
	 * Get the money balance of a player
	 * @param p player name
	 * @return money balance
	 */
	public double getBalance(String p) {
		return getBalance(p, 0.00);
	}
	
	/**
	 * Get the money balance of a player
	 * @param p player name
	 * @param def default balance if not hooked into any economy system
	 * @return money balance
	 */
	public double getBalance(String p, double def) {
		if(!isEnabled()) {
			// No economy system is used, return zero balance
			return 0.00;
		}
		
		switch(this.economyType) {
		case SIMPLE_ECONOMY:
			// Simple Economy
			return simpleEconomyHandler.getMoney(p);
			
		case BOSECONOMY:
			// BOSEconomy
			return BOSEcon.getPlayerMoneyDouble(p);
			
		case VAULT:
			// Vault
			return vaultEconomy.getBalance(p);
			
		case NONE:
			// Not hooked into any economy system, return default balance
			return def;
			
		default:
			// Something went wrong, return zero balance to prevent problems
			return 0.00;
		}
	}
	
	/**
	 * Check if a player has enough money balance to pay something
	 * @param p player name
	 * @param price price to pay
	 * @return true if has enough money
	 */
	public boolean hasEnoughMoney(String p, double price) {
		if(!isEnabled())
			return false;
		
		double balance = getBalance(p);
		return (balance >= price);
	}
	
	/**
	 * Deposit money to a player
	 * @param p player name
	 * @param money money amount
	 * @return false when something was wrong
	 */
	public boolean depositMoney(String p, double money) {
		if(!isEnabled()) {
			// No economy system is used, return false
			return false;
		}
		
		// Get current player balance
		//double balance = getBalance(p);
		
		// Deposit money
		switch(this.economyType) {
		case SIMPLE_ECONOMY:
			// Simple Economy
			simpleEconomyHandler.addMoney(p, money);
			break;
			
		case BOSECONOMY:
			// BOSEconomy
			BOSEcon.addPlayerMoney(p, money, false);
			break;
			
		case VAULT:
			// Vault
			vaultEconomy.depositPlayer(p, money);
			break;
			
		case NONE:
			// Not hooked into any economy system, return false
			return false;
			
		default:
			// Something went wrong, return false to prevent problems
			return false;
		}
		
		return true;
	}
	
	/**
	 * Withdraw money from a player
	 * @param p player name
	 * @param money money amount
	 * @return false when something was wrong
	 */
	public boolean withdrawMoney(String p, double money) {
		if(!isEnabled()) {
			// No economy system is used, return false
			return false;
		}
		
		// Get current player balance
		double balance = getBalance(p);
		double newBalance = balance - money;
		
		// The new Balance has to be zero or above
		if(newBalance < 0) {
			return false;
		}
		
		// Withdraw money
		switch(this.economyType) {
		case SIMPLE_ECONOMY:
			// Simple Economy
			simpleEconomyHandler.subtractMoney(p, money);
			break;
			
		case BOSECONOMY:
			// BOSEconomy
			BOSEcon.setPlayerMoney(p, newBalance, false);
			break;
			
		case VAULT:
			// Vault
			vaultEconomy.withdrawPlayer(p, money);
			break;
			
		case NONE:
			// Not hooked into any economy system, return false
			return false;
			
		default:
			// Something went wrong, return false to prevent problems
			return false;
		}
		
		return true;
	}
	
	/**
	 * Get the currency name
	 * @param money the current balance (to get the Singular/Plural thingy right)
	 * @return currency name
	 */
	public String getCurrencyName(double money) {
		return getCurrencyName(money, "Money");
	}
	
	/**
	 * Get the currency name
	 * @param money the current balance (to get the Singular/Plural thingy right)
	 * @param def the default currency name
	 * @return currency name
	 */
	public String getCurrencyName(double money, String def) {
		if(!isEnabled()) {
			// No economy system is used, return false
			return def;
		}
		
		// Get currency name
		switch(this.economyType) {
		case SIMPLE_ECONOMY:
			// Simple Economy
			//TODO Finish this function in the API of Simple Economy
			return "Silver";
			
		case BOSECONOMY:
			// BOSEconomy
			return BOSEcon.getMoneyNameProper(money);
			
		case VAULT:
			// Vault
			if(money > 1.00) {
				return vaultEconomy.currencyNamePlural();
			} else {
				return vaultEconomy.currencyNameSingular();
			}
			
		case NONE:
			// Not hooked into any economy system, return false
			return def;
			
		default:
			// Something went wrong, return false to prevent problems
			return def;
		}
	}
	
	public String toMoneyNotationProper(double money, boolean withCurrencyName) {
		if(withCurrencyName)
			return moneyToStringProper(money) + " " + getCurrencyName(money);
		return moneyToStringProper(money);
	}
	
	/**
	 * Check if a economy system is allowed to be used according to the configuration file
	 * @param configNodeName The configuration node name of the economy system
	 * @return True if this system is allowed to be used
	 */
	private boolean isEconomySystemAllowed(String configNodeName) {
		return WorldPortal.instance.getConfig().getBoolean("economy.economySystems." + configNodeName + ".enabled", true);
	}
	
	
	
	
	
	
	
	
	
	public double stringToDouble(String doubleString) {
		doubleString = doubleString.replace(",", ".");
		BigDecimal d = new BigDecimal(doubleString);
		return d.doubleValue();
	}
	/*private double roundMoney(double money) {
	    DecimalFormat twoDForm = new DecimalFormat("#0.##");
	    return stringToDouble(twoDForm.format(money));
	}*/
	private String moneyToStringProper(double money) {
		DecimalFormat twoDForm = new DecimalFormat("#0.00");
	    return twoDForm.format(money);
	}
	
	
	
	
	
	
	public enum EconomySystemType {
		NONE("None"),
		SIMPLE_ECONOMY("Simple Economy"),
		BOSECONOMY("BOSEconomy"),
		VAULT("Vault");
		
		public String name;
		
		EconomySystemType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
}
