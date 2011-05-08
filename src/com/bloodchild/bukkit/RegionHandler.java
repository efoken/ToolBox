package com.bloodchild.bukkit;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;


public class RegionHandler {

	public static GlobalRegionManager regions;

	public static void init(Server server, String pluginName, Logger log) {
		Plugin wg = server.getPluginManager().getPlugin("WorldGuard");

		if (wg != null) {
			regions = ((WorldGuardPlugin) wg).getGlobalRegionManager();
			log.info("[" + pluginName + "] Using WorldGuard for regions support");
		}
	}

	public static boolean canBuildHere(Player player, Block block) {
		if (regions != null) {
			return regions.canBuild(player, block);
		} else {
			return true;
		}
	}
}
