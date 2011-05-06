package com.bloodchild.bukkit;

import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToolHandler {

	public static int duplicatorTool = 275;
	public static int scrollerTool = 352;
	public static int paintbrushTool = 341;

	/**
	 * Storage for player block data.
	 */
	private static Hashtable<Player, Block> playerBlock = new Hashtable<Player, Block>();

	public final static void removePlayerBlock(Player player) {
		if (playerBlock.containsKey(player)) {
			playerBlock.remove(player);
		}
	}

	public final static void copyBlock(Player player, Block block) {
		if (ToolboxUtils.isPaintableBlock(block)) {
			removePlayerBlock(player);
			playerBlock.put(player, block);
			player.sendMessage(ChatColor.BLUE + "Ink acquired! (" + block.getType() + ")");
		} else {
			player.sendMessage(ChatColor.RED + "Copying that block is not allowed");
		}
	}

	public final static void paintBlock(Player player, Block block) {
		if (playerBlock.containsKey(player)) {
			/*
			 * If the block could not be copied, chances are things will end
			 * badly if it is possible to paste over it, so only allow pasting
			 * over block types that are copyable.
			 */
			if (ToolboxUtils.isPaintableBlock(block)) {
				Block copiedBlock = playerBlock.get(player);

				if (ToolboxUtils.isPaintableBlock(copiedBlock)) {
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
		int maxData = -1;

		// check if this is a block that we want to modify
		switch (block.getTypeId()) {
		case 6:
			maxData = 2;
			break; // saplings
		case 17:
			maxData = 2;
			break; // logs
		case 18:
			maxData = 2;
			break; // leaves
		case 35:
			maxData = 15;
			break; // wool
		case 43:
			maxData = 3;
			break; // single steps
		case 44:
			maxData = 3;
			break; // double steps
		case 53:
			maxData = 3;
			break; // wooden stairs
		case 67:
			maxData = 3;
			break; // cobblestone stairs
		case 86:
			maxData = 3;
			break; // pumpkins (changes direction)
		case 91:
			maxData = 3;
			break; // jack-o-lanterns (changes direction)
		}

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
	@SuppressWarnings("deprecation")
	public final static void handleDuplicatorTool(Player player, Block block) {
		int itemId = ToolboxUtils.translateBlockToItemID(block.getTypeId());

		if (player.getInventory().firstEmpty() < 0) {
			player.sendMessage(ChatColor.RED + "Your inventory is full!");
		} else {
			if (ToolboxUtils.isDuplicatableBlock(block.getTypeId())) {
				ItemStack stack = new ItemStack(itemId, 64);
				if (ToolboxUtils.isItemWithDataValue(itemId)) {
					stack.setDurability(block.getData());
				}

				CommandHandler.giveStack(player, stack);
				player.sendMessage("Enjoy your " + stack.getType());
				player.updateInventory(); // FIXME Use of depreciated method
			} else {
				player.sendMessage(ChatColor.RED + "Duplicating that block is not allowed");
			}
		}
	}

}
