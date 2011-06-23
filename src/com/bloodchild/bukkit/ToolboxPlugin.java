package com.bloodchild.bukkit;

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
		(new ToolBlockListener(this)).registerEvents();
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
			} else if (commandLabel.equalsIgnoreCase("superpickaxe")
					|| commandLabel.equalsIgnoreCase("spa")) {
				if (ToolPermissions.canUseSuperPickaxe(player)) {
					CommandHandler.toggleSuperPickaxe(player);
				}
			} else if (commandLabel.equalsIgnoreCase("duplicator")
					|| commandLabel.equalsIgnoreCase("duper")) {
				if (ToolPermissions.canUseDuplicatorTool(player)) {
					CommandHandler.giveDuplicatorTool(player);
				}
			} else if (commandLabel.equalsIgnoreCase("datawrench")
					|| commandLabel.equalsIgnoreCase("scroller")) {
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
		config.load();

		int duplicatorTool = config.getInt("duplicatorTool", 275);
		int paintbrushTool = config.getInt("paintbrushTool", 341);
		int scrollerTool = config.getInt("scrollerTool", 352);

		if (paintbrushTool == duplicatorTool) {
			paintbrushTool = ToolHandler.paintbrushTool;
			duplicatorTool = ToolHandler.duplicatorTool;
			log.warning("Paintbrush tool and duplicator tool cannot be set to the same item, using defaults instead.");
		}
		if (paintbrushTool == scrollerTool) {
			paintbrushTool = ToolHandler.paintbrushTool;
			scrollerTool = ToolHandler.scrollerTool;
			log.warning("Paintbrush tool and scroller tool cannot be set to the same item, using defaults instead.");
		}
		if (duplicatorTool == scrollerTool) {
			duplicatorTool = ToolHandler.duplicatorTool;
			scrollerTool = ToolHandler.scrollerTool;
			log.warning("Duplicator tool and scroller tool cannot be set to the same item, using defaults instead.");
		}

		ToolHandler.duplicatorTool = duplicatorTool;
		ToolHandler.paintbrushTool = paintbrushTool;
		ToolHandler.scrollerTool = scrollerTool;

		CommandHandler.mimicRadius = config.getInt("mimicRadius", 40);

		// load blocks that are unduplicatable
		String temp[] = config.getString("unduplicatable", "7,8,9,10,11,51,52,79").split(",");
		for (String block : temp) {
			if (!block.equals("")) {
				BlockHandler.unduplicatableBlocks.put(Integer.parseInt(block), false);
			}
		}
		// load blocks that are unmimicable
		temp = config.getString("unmimicable", "0,1,2,3,7,8,9,10,11,12,13,46,51,66,79").split(",");
		for (String block : temp) {
			if (!block.equals("")) {
				BlockHandler.unmimicableBlocks.put(Integer.parseInt(block), false);
			}
		}
		// load blocks that are scrollable
		temp = config.getString("scrollable", "17,18,23,25,26,35,43,44,53,61,63,65,66,67,68,69,77,86,81,91").split(",");
		for (String block : temp) {
			if (!block.equals("")) {
				BlockHandler.scrollableBlocks.put(Integer.parseInt(block), true);
			}
		}

		// load tools that are invincible
		temp = config.getString("invincibleTools", "278,284,285,286").split(",");
		for (String block : temp) {
			if (!block.equals("")) {
				ToolHandler.invincibleTools.put(Integer.parseInt(block), true);
			}
		}

		saveConfig();
	}

	/**
	 * Saves the configuration to file.
	 */
	public void saveConfig() {
		config.setProperty("duplicatorTool", ToolHandler.duplicatorTool);
		config.setProperty("paintbrushTool", ToolHandler.paintbrushTool);
		config.setProperty("scrollerTool", ToolHandler.scrollerTool);
		config.setProperty("mimicRadius", Integer.valueOf(CommandHandler.mimicRadius));

		// save block that are scrollable
		StringBuilder sb = new StringBuilder();
		for (Integer block : BlockHandler.scrollableBlocks.keySet()) {
			sb.append(block).append(",");
		}
		config.setProperty("scrollable", sb.toString());

		// save block that are unmimicable
		sb = new StringBuilder();
		for (Integer block : BlockHandler.unmimicableBlocks.keySet()) {
			sb.append(block).append(",");
		}
		config.setProperty("unmimicable", sb.toString());

		// save block that are unduplicatable
		sb = new StringBuilder();
		for (Integer block : BlockHandler.unduplicatableBlocks.keySet()) {
			sb.append(block).append(",");
		}
		config.setProperty("unduplicatable", sb.toString());

		// save tools that are invincible
		sb = new StringBuilder();
		for (Integer block : ToolHandler.invincibleTools.keySet()) {
			sb.append(block).append(",");
		}
		config.setProperty("invincibleTools", sb.toString());

		config.save();
	}

}
