package com.bloodchild.bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ToolboxPlugin extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");

	private Configuration config;

	public static Properties configSettings = new Properties();

	public String name;

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "] " + pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is disabled!");
	}

	/**
	 * Called on plug-in enable.
	 */
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		name = pdfFile.getName();

		log.info("[" + name + "] " + name + " version " + pdfFile.getVersion() + " is enabled!");

		// load the configuration
		config = getConfiguration();
		loadConfig();

		// load permissions
		ToolPermissions.init(getServer(), name, log);
		RegionHandler.init(getServer(), name, log);

		// register events
		(new ToolPlayerListener(this)).registerEvents();
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if ((commandLabel.equalsIgnoreCase("clear"))) {
				if (ToolPermissions.canUseClearCommand(player)) {
					CommandHandler.handleClearCommand(player);
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}
			} else if ((commandLabel.equalsIgnoreCase("compact"))) {
				if (ToolPermissions.canUseCompactCommand(player)) {
					CommandHandler.handleCompactComment(player);
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}
			} else if ((commandLabel.equalsIgnoreCase("near"))) {
				/*if (DupePermissions.canUseNearCommand(player)) {
					CommandHandler.handleNearCommand(player, args);
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}*/
			} else if (commandLabel.equalsIgnoreCase("more") || commandLabel.equalsIgnoreCase("m")) {
				if (ToolPermissions.canUseMoreCommand(player)) {
					CommandHandler.handleMoreCommand(player, args);
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}
			} else if (commandLabel.equalsIgnoreCase("pick") || commandLabel.equalsIgnoreCase("p")) {
				if (ToolPermissions.canUsePickCommand(player)) {
					CommandHandler.handlePickCommand(player, args);
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}
			} else if (commandLabel.equalsIgnoreCase("duplicator") || commandLabel.equalsIgnoreCase("duper")) {
				if (ToolPermissions.canUseDuplicatorTool(player)) {
					CommandHandler.giveDuplicatorTool(player);
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}
			} else if (commandLabel.equalsIgnoreCase("datawrench") || commandLabel.equalsIgnoreCase("scroller")) {
				if (ToolPermissions.canUseScrollerTool(player)) {
					CommandHandler.giveScrollerTool(player);
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}
			} else if (commandLabel.equalsIgnoreCase("paintbrush") || commandLabel.equalsIgnoreCase("painter")) {
				if (ToolPermissions.canUsePaintbrushTool(player)) {
					CommandHandler.givePaintbrushTool(player);
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}
			} else if ((commandLabel.equalsIgnoreCase("tools"))) {
				if (ToolPermissions.canUsePaintbrushTool(player)) {
					CommandHandler.givePaintbrushTool(player);
				}
				if (ToolPermissions.canUseScrollerTool(player)) {
					CommandHandler.giveScrollerTool(player);
				}
				if (ToolPermissions.canUseDuplicatorTool(player)) {
					CommandHandler.giveDuplicatorTool(player);
				}
			} else if ((commandLabel.equalsIgnoreCase("boxreload"))) {
				if (ToolPermissions.canReload(player)) {
					loadConfig();
					player.sendMessage(this.getDescription().getFullName() + " reloaded!");
					log.info("[" + name + "] Configuration file reloaded by "
							+ player.getDisplayName());
				} else {
					player.sendMessage(ChatColor.RED + "No permission");
				}
			}
		} else {
			if ((commandLabel.equalsIgnoreCase("boxreload"))) {
				loadConfig();
				log.info("[" + name + "] Configuration file reloaded by console");
			}
		}

		return true;
	}

	/**
	 * Loads the configuration file data into appropriate places.
	 */
	public void loadConfig() {
		ToolboxUtils.duplicatableBlocks.clear();
		config.load();

		int duplicatorToolId = ToolHandler.duplicatorTool;
		int paintbrushToolId = ToolHandler.paintbrushTool;
		int scrollerToolId = ToolHandler.scrollerTool;

		// default duplicator tool is 275
		duplicatorToolId = config.getInt("duplicatorTool", 275);

		// default paintbrush tool is 341
		paintbrushToolId = config.getInt("paintbrushTool", 341);

		// default scroller tool is 352
		scrollerToolId = config.getInt("scrollerTool", 352);

		if (paintbrushToolId == duplicatorToolId) {
			paintbrushToolId = ToolHandler.paintbrushTool;
			duplicatorToolId = ToolHandler.duplicatorTool;

			log.warning("Paintbrush tool and duplicator tool cannot be set to the same item, using defaults instead.");
		}
		if (paintbrushToolId == scrollerToolId) {
			paintbrushToolId = ToolHandler.paintbrushTool;
			scrollerToolId = ToolHandler.scrollerTool;

			log.warning("Paintbrush tool and scroller tool cannot be set to the same item, using defaults instead.");
		}
		if (duplicatorToolId == scrollerToolId) {
			duplicatorToolId = ToolHandler.duplicatorTool;
			scrollerToolId = ToolHandler.scrollerTool;

			log.warning("Duplicator tool and scroller tool cannot be set to the same item, using defaults instead.");
		}

		ToolHandler.duplicatorTool = duplicatorToolId;
		ToolHandler.paintbrushTool = paintbrushToolId;
		ToolHandler.scrollerTool = scrollerToolId;

		// 7 = bedrock, 8 & 9 = water, 10 & 11 = lava, 51 = fire, 79 = ice
		List<Integer> unduplicatable = config.getIntList("unduplicatable",
				Arrays.asList(new Integer[] { 7, 8, 9, 10, 11, 51, 52, 79 }));

		for (Integer item : unduplicatable) {
			if (item > 0) { // make sure that a valid number is given
				ToolboxUtils.duplicatableBlocks.add(item.intValue());
			}
		}

		// 17 = log, etc.
		List<Integer> scrollable = config.getIntList(
				"scrollable",
				Arrays.asList(new Integer[] { 17, 18, 23, 25, 26, 35, 43, 44, 53, 61, 63, 65, 66,
						67, 68, 69, 77, 86, 81, 91 }));

		for (Integer item : scrollable) {
			if (item > 0) { // make sure that a valid number is given
				ToolboxUtils.scrollableBlocks.add(item.intValue());
			}
		}

		saveConfig();
		log.info("[" + name + "] Config file loaded!");
	}

	/**
	 * Saves the configuration to file.
	 */
	public void saveConfig() {
		config.setProperty("duplicatorTool", ToolHandler.duplicatorTool);
		config.setProperty("paintbrushTool", ToolHandler.paintbrushTool);
		config.setProperty("scrollerTool", ToolHandler.scrollerTool);
		config.setProperty("unduplicatable", ToolboxUtils.duplicatableBlocks.toArray());
		config.setProperty("scrollable", ToolboxUtils.scrollableBlocks.toArray());
		config.save();

		log.info("[" + name + "] Successfully created new config file");
	}

}
