package com.bloodchild.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

/**
 * Handles all events thrown in a relation to a player.
 * 
 * @author sturmkeyser
 */
public class ToolPlayerListener extends PlayerListener {

	/**
	 * Contains the plug-in instance.
	 */
	private ToolboxPlugin plugin;

	/**
	 * Constructs the object.
	 * 
	 * @param plugin
	 */
	public ToolPlayerListener(ToolboxPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Registers the required events.
	 */
	public void registerEvents() {
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, this, Priority.Normal, plugin);
	}

	/**
	 * Called when a player interacts with an item.
	 * 
	 * @param event Relevant event details
	 */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.duplicatorTool) {
				/*
				 * Duplication tool
				 */
				if (ToolPermissions.canUseDuplicatorTool(event.getPlayer())) {
					event.setCancelled(true);
					ToolHandler.handleDuplicatorTool(event.getPlayer(), event.getClickedBlock(), 1);
				}
			} else if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.paintbrushTool) {
				/*
				 * Paintbrush tool
				 */
				if (ToolPermissions.canUsePaintbrushTool(event.getPlayer())) {
					event.setCancelled(true);
					ToolHandler.copyBlock(event.getPlayer(), event.getClickedBlock());
				}
			} else if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.scrollerTool) {
				/*
				 * Data tool
				 */
				if (ToolPermissions.canUseScrollerTool(event.getPlayer())) {
					if (RegionHandler.canBuildHere(event.getPlayer(), event.getClickedBlock())) {
						event.setCancelled(true);
						ToolHandler.handleScrollTool(event.getPlayer(), event.getClickedBlock());
					} else {
						event.getPlayer().sendMessage(ChatColor.DARK_RED + "Using that tools is not allowed in this area.");
					}
				}
			}
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.duplicatorTool) {
				/*
				 * Duplication tool
				 */
				if (ToolPermissions.canUseDuplicatorTool(event.getPlayer())) {
					event.setCancelled(true);
					ToolHandler.handleDuplicatorTool(event.getPlayer(), event.getClickedBlock());
				}
			} else if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.paintbrushTool) {
				/*
				 * Paintbrush tool
				 */
				if (ToolPermissions.canUsePaintbrushTool(event.getPlayer())) {
					if (RegionHandler.canBuildHere(event.getPlayer(), event.getClickedBlock())) {
						event.setCancelled(true);
						ToolHandler.paintBlock(event.getPlayer(), event.getClickedBlock());
					} else {
						event.getPlayer().sendMessage(ChatColor.DARK_RED + "Using that tools is not allowed in this area.");
					}
				}
			} else if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.scrollerTool) {
				/*
				 * Data cycler tool
				 */
				if (ToolPermissions.canUseScrollerTool(event.getPlayer())) {
					if (RegionHandler.canBuildHere(event.getPlayer(), event.getClickedBlock())) {
						event.setCancelled(true);
						ToolHandler.handleScrollTool(event.getPlayer(), event.getClickedBlock(), true);
					} else {
						event.getPlayer().sendMessage(ChatColor.DARK_RED + "Using that tools is not allowed in this area.");
					}
				}
			}
		}
	}

	/**
	 * Called when a player leaves a server.
	 * 
	 * @param event Relevant event details
	 */
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		ToolHandler.removePlayerBlock(event.getPlayer());
	}

	@Override
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
		event.getItemDrop().remove();
	}

}
