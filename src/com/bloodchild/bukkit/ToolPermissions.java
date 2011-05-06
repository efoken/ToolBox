package com.bloodchild.bukkit;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ToolPermissions {

	public static PermissionHandler handler;

	public static void init(Server server) {
		Plugin permissions = server.getPluginManager().getPlugin("Permissions");

		if (permissions != null) {
			handler = ((Permissions) permissions).getHandler();
			// log.info("Using Permissions");
		}
	}

	/**
	 * Checks if the given player has the given permission.
	 * 
	 * @param player
	 * @param permission
	 * @param defaultPermission
	 * @return
	 */
	private static boolean hasPermission(Player player, String permission, boolean defaultPermission) {
		if (handler != null) {
			return handler.has(player, permission);
		} else {
			return defaultPermission;
		}
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canUseMoreCommand(Player player) {
		return hasPermission(player, "toolbox.commands.more", true);
	}

	/**
	 * 
	 * @param player
	 * @return
	 *\/
	public static boolean canUseNearCommand(Player player) {
		return hasPermission(player, "toolbox.commands.mimic", true);
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canUseClearCommand(Player player) {
		return hasPermission(player, "toolbox.commands.clear", true);
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canUseCompactCommand(Player player) {
		return hasPermission(player, "toolbox.commands.compact", true);
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canUsePickCommand(Player player) {
		return hasPermission(player, "toolbox.commands.pick", true);
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canUseScrollerTool(Player player) {
		return hasPermission(player, "toolbox.tools.scroller", true);
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canUseDuplicatorTool(Player player) {
		return hasPermission(player, "toolbox.tools.duplicator", true);
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canUsePaintbrushTool(Player player) {
		return hasPermission(player, "toolbox.tools.paintbrush", true);
	}

	/**
	 * Ability to reload the configuration file from the server
	 * 
	 * @param player
	 * @return
	 */
	public static boolean canReload(Player player) {
		return hasPermission(player, "toolbox.reload", player.isOp());
	}

}
