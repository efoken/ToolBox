package com.bloodchild.bukkit;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ToolPlayerListener extends PlayerListener {

	public static ToolboxPlugin plugin;

	public ToolPlayerListener(ToolboxPlugin instance) {
		plugin = instance;
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		ToolHandler.removePlayerBlock(event.getPlayer());
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.duplicatorTool) {
				// this functionality has been moved to the data tool
			} else if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.paintbrushTool) {
				// paintbrush tool
				if (ToolPermissions.canUsePaintbrushTool(event.getPlayer())) {
					event.setCancelled(true);
					ToolHandler.copyBlock(event.getPlayer(), event.getClickedBlock());
				}
			} else if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.scrollerTool) {
				// data tool
				if (ToolPermissions.canUseScrollerTool(event.getPlayer())) {
					event.setCancelled(true);
					ToolHandler.handleScrollTool(event.getPlayer(), event.getClickedBlock());
				}
			}
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.duplicatorTool) {
				// duplicator tool
				if (ToolPermissions.canUseDuplicatorTool(event.getPlayer())) {
					event.setCancelled(true);
					ToolHandler.handleDuplicatorTool(event.getPlayer(), event.getClickedBlock());
				}
			} else if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.paintbrushTool) {
				if (ToolPermissions.canUsePaintbrushTool(event.getPlayer())) {
					event.setCancelled(true);
					ToolHandler.paintBlock(event.getPlayer(), event.getClickedBlock());
				}
			} else if (event.getPlayer().getItemInHand().getTypeId() == ToolHandler.scrollerTool) {
				if (ToolPermissions.canUseScrollerTool(event.getPlayer())) {
					event.setCancelled(true);
					ToolHandler.handleScrollTool(event.getPlayer(), event.getClickedBlock(), true);
				}
			}
		}
	}

}
