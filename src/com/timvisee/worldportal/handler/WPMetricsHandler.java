package com.timvisee.worldportal.handler;

import java.io.IOException;

import com.timvisee.worldportal.Metrics;
import com.timvisee.worldportal.Metrics.Graph;
import com.timvisee.worldportal.WPLogger;
import com.timvisee.worldportal.WorldPortal;

public class WPMetricsHandler {
	
	private WPLogger log;
	
	/**
	 * Constructor
	 * @param log SCLogger
	 */
	public WPMetricsHandler(WPLogger log) {
		this.log = log;
	}
	
	/**
	 * Set up Metrics
	 */
	public void setUp() {
		if(!WorldPortal.instance.getConfig().getBoolean("statistics.enabled", true)) {
			this.log.info("MCStats.org Statistics disabled in config file!");
			return;
		}
		
		// Metrics / MCStats.org
		try {
		    Metrics metrics = new Metrics(WorldPortal.instance);
		    
		    // Add graph for nerfed creepers
		    // Construct a graph, which can be immediately used and considered as valid
		    /*Graph graph = metrics.createGraph("Activities Nerfed by Safe Creeper");
		    // Creeper explosions Nerfed
		    graph.addPlotter(new Metrics.Plotter("Creeper Explosions") {
	            @Override
	            public int getValue() {
	            	int i = SafeCreeper.instance.getStaticsManager().getCreeperExplosionsNerfed();
	            	SafeCreeper.instance.getStaticsManager().setCreeperExplosionNerved(0);
	            	return i;
	            }
		    });*/
		    // Used permissions systems
		    Graph permsGraph = metrics.createGraph("Permisison Plugin Usage");
		    permsGraph.addPlotter(new Metrics.Plotter(WorldPortal.instance.getPermissionsManager().getUsedPermissionsSystemType().getName()) {
	            @Override
	            public int getValue() {
	            	return 1;
	            }
		    });
		    // Used economy systems
		    Graph econGraph = metrics.createGraph("Economy Plugin Usage");
		    econGraph.addPlotter(new Metrics.Plotter(WorldPortal.instance.getEconomyManager().getUsedEconomySystemType().getName()) {
	            @Override
	            public int getValue() {
	            	return 1;
	            }
		    });
		    
		    // Start metrics
		    metrics.start();
		    
		    // Show a status message
		    this.log.info("MCStats.org Statistics enabled.");
		} catch (IOException e) {
		    // Failed to submit the stats :-(
			e.printStackTrace();
		}
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
}
