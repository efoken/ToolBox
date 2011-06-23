package com.bloodchild.bukkit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class ToolBlockListener extends BlockListener {

	/**
	 * Contains the plug-in instance.
	 */
	private ToolboxPlugin plugin;

	/**
	 * Constructs the object.
	 * 
	 * @param plugin
	 */
	public ToolBlockListener(ToolboxPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Registers the required events.
	 */
	public void registerEvents() {
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, this, Priority.Normal, plugin);
	}

	/**
	 * Called when a block breaks.
	 * 
	 * @param event Relevant event details
	 */
	@SuppressWarnings("deprecation")
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack itemInHand = player.getItemInHand();

		if (itemInHand.getDurability() > 0) {
			if (ToolHandler.invincibleTools.containsKey(itemInHand.getTypeId())) {
				if (ToolPermissions.canUseInvincibleTools(player)) {
					itemInHand.setDurability((short) -1);
					player.updateInventory(); // TODO Use of depreciated method
				}
			}
		}
		event.setCancelled(true); // no drops
		event.getBlock().setType(Material.AIR);
	}

	/**
	 * 
	 */
	public void onBlockDamage(BlockDamageEvent event) {
		if (!event.isCancelled()
				&& ToolHandler.playersWithSuperPickaxe.contains(event.getPlayer().getName().hashCode())
				&& event.getItemInHand().getTypeId() == ToolHandler.superPickaxe) {
			event.setInstaBreak(true);
		}
	}

}
