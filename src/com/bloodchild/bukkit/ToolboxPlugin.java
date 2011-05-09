package com.bloodchild.bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

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

	/**
	 * Called on plug-in disable.
	 */
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

	/**
	 * 
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if ((commandLabel.equalsIgnoreCase("clear"))) {
				if (ToolPermissions.canUseClearCommand(player)) {
					CommandHandler.handleClearCommand(player, args);
				}
			} else if ((commandLabel.equalsIgnoreCase("compact"))) {
				if (ToolPermissions.canUseCompactCommand(player)) {
					CommandHandler.handleCompactComment(player);
				}
			} else if ((commandLabel.equalsIgnoreCase("mimic"))) {
				if (ToolPermissions.canUseMimicCommand(player)) {
					CommandHandler.handleMimicCommand(player, args);
				}
			} else if (commandLabel.equalsIgnoreCase("more") || commandLabel.equalsIgnoreCase("m")) {
				if (ToolPermissions.canUseMoreCommand(player)) {
					CommandHandler.handleMoreCommand(player, args);
				}
			} else if (commandLabel.equalsIgnoreCase("pick") || commandLabel.equalsIgnoreCase("p")) {
				if (ToolPermissions.canUsePickCommand(player)) {
					CommandHandler.handlePickCommand(player, args);
				}
			} else if (commandLabel.equalsIgnoreCase("duplicator")
					|| commandLabel.equalsIgnoreCase("duper")) {
				if (ToolPermissions.canUseDuplicatorTool(player)) {
					CommandHandler.giveDuplicatorTool(player);
				}
			} else if (commandLabel.equalsIgnoreCase("scroller")) {
				if (ToolPermissions.canUseScrollerTool(player)) {
					CommandHandler.giveScrollerTool(player);
				}
			} else if (commandLabel.equalsIgnoreCase("paintbrush")
					|| commandLabel.equalsIgnoreCase("painter")) {
				if (ToolPermissions.canUsePaintbrushTool(player)) {
					CommandHandler.givePaintbrushTool(player);
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
		ToolboxUtils.unduplicatableBlocks.clear();
		ToolboxUtils.unmimicableBlocks.clear();
		ToolboxUtils.scrollableBlocks.clear();
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
				ToolboxUtils.unduplicatableBlocks.add(item.intValue());
			}
		}

		// 7 = bedrock, 8 & 9 = water, 10 & 11 = lava, 51 = fire, 79 = ice
		List<Integer> unmimicable = config.getIntList("unmimicable", Arrays
				.asList(new Integer[] { 0, 1, 2, 3, 66, 7, 8, 9, 10, 79, 11, 12, 46, 13, 51 }));

		for (Integer item : unmimicable) {
			if (item > 0) { // make sure that a valid number is given
				ToolboxUtils.unmimicableBlocks.add(item.intValue());
			}
		}

		// max radius for /mimic command
		ToolboxUtils.mimicRadius = config.getInt("mimicRadius", 40);

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
		config.setProperty("unduplicatable", ToolboxUtils.unduplicatableBlocks.toArray());
		config.setProperty("unmimicable", ToolboxUtils.unduplicatableBlocks.toArray());
		config.setProperty("mimicRadius", Integer.valueOf(ToolboxUtils.mimicRadius));
		config.setProperty("scrollable", ToolboxUtils.scrollableBlocks.toArray());
		config.save();

		log.info("[" + name + "] Successfully created new config file");
	}

}
