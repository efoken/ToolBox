package com.bloodchild.bukkit;

import java.util.HashMap;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToolHandler {

	public static int duplicatorTool = 275;
	public static int scrollerTool = 352;
	public static int paintbrushTool = 341;
	public static int superPickaxe = 274;

	public static HashMap<Integer, Boolean> invincibleTools = new HashMap<Integer, Boolean>();

	private static int lastEmpty = -1;

	/**
	 * Storage for player block data.
	 */
	private static Hashtable<Player, Block> playerBlock = new Hashtable<Player, Block>();

	/**
	 * 
	 * @param player
	 */
	public final static void removePlayerBlock(Player player) {
		if (playerBlock.containsKey(player)) {
			playerBlock.remove(player);
		}
	}

	/**
	 * 
	 * @param itemId
	 * @return
	 */
	public static boolean isTool(int itemId) {
		return itemId == duplicatorTool || itemId == scrollerTool || itemId == paintbrushTool;
	}

	/**
	 * 
	 * @param player
	 * @param block
	 */
	public final static void copyBlock(Player player, Block block) {
		if (BlockHandler.isPaintableBlock(block)) {
			removePlayerBlock(player);
			playerBlock.put(player, block);
			player.sendMessage(ChatColor.BLUE + "Ink acquired! (" + block.getType() + ")");
		} else {
			player.sendMessage(ChatColor.RED + "Copying that block is not allowed");
		}
	}

	/**
	 * 
	 * @param player
	 * @param block
	 */
	public final static void paintBlock(Player player, Block block) {
		if (playerBlock.containsKey(player)) {
			/*
			 * If the block could not be copied, chances are things will end
			 * badly if it is possible to paste over it, so only allow pasting
			 * over block types that are copyable.
			 */
			if (BlockHandler.isPaintableBlock(block)) {
				Block copiedBlock = playerBlock.get(player);

				if (BlockHandler.isPaintableBlock(copiedBlock)) {
					block.setType(copiedBlock.getType());
					block.setData(copiedBlock.getData());
				}
			} else {
				player.sendMessage(ChatColor.RED + "Painting that block is not allowed");
			}
		}
	}

	/**
	 * 
	 * @param player
	 * @param block
	 */
	public final static void handleScrollTool(Player player, Block block) {
		handleScrollTool(player, block, false);
	}

	/**
	 * 
	 * @param player
	 * @param block
	 * @param reverse
	 */
	public final static void handleScrollTool(Player player, Block block, boolean reverse) {
		int maxData = ToolboxUtils.getMaxDataValue(block.getTypeId());

		// if the blockID was on the list, go ahead and cycle it's data
		if (maxData >= 0) {
			int currentData = block.getData();

			if (reverse) {
				currentData--;
			} else {
				currentData++;
			}

			if (currentData > maxData) {
				currentData = 0;
			}
			if (currentData < 0) {
				currentData = maxData;
			}

			block.setData((byte) (currentData & 0xFF));
		}
	}

	/**
	 * 
	 * @param player
	 * @param block
	 */
	public final static void handleDuplicatorTool(Player player, Block block) {
		handleDuplicatorTool(player, block, 64);
	}

	/**
	 * 
	 * @param player
	 * @param block
	 * @param amount
	 */
	@SuppressWarnings("deprecation")
	public final static void handleDuplicatorTool(Player player, Block block, int amount) {
		int itemId = ToolboxUtils.translateBlockToItemID(block.getTypeId());
		boolean given = false;

		if (amount < 64) {
			ItemStack lastItem;
			if (lastEmpty > 0 && (lastItem = player.getInventory().getItem(lastEmpty)) != null) {
				if (lastItem.getTypeId() == itemId && (!ToolboxUtils.isItemWithDataValue(itemId) || lastItem.getData().getData() == block.getData())) {
					if (lastItem.getAmount() < 64) {
						int newAmount = Math.min(lastItem.getAmount() + amount, 64);
						ItemStack stack = new ItemStack(lastItem.getType(), newAmount, lastItem.getDurability());
						player.getInventory().setItem(lastEmpty, stack);
						given = true;
					}
				}
			}
		}

		if (!given) {
			if (player.getInventory().firstEmpty() < 0) {
				player.sendMessage(ChatColor.RED + "Your inventory is full!");
			} else {
				if (BlockHandler.isDuplicatableBlock(block.getTypeId())) {
					ItemStack stack = new ItemStack(itemId, amount);
					if (ToolboxUtils.isItemWithDataValue(itemId)) {
						stack.setDurability(block.getData());
					}
	
					lastEmpty = player.getInventory().firstEmpty();
					player.getInventory().setItem(lastEmpty, stack);
					player.sendMessage("Enjoy your " + stack.getType());
				} else {
					player.sendMessage(ChatColor.RED + "Duplicating that block is not allowed");
				}
			}
		}
		player.updateInventory(); // FIXME Use of depreciated method
	}

}
