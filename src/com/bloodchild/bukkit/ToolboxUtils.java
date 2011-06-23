package com.bloodchild.bukkit;

import org.bukkit.inventory.ItemStack;

public class ToolboxUtils {

	/**
	 * Translates an items ID into it's name.
	 */
	public static String itemIdToName(int itemId) {
		return (new ItemStack(itemId)).getType().toString();
	}

	/**
	 * Checks to see if this item is one that has a special data value, to
	 * determine if we should be copying its data field.
	 */
	public static boolean isItemWithDataValue(int itemId) {
		boolean value = false;

		switch (itemId) {
		case 44: // single step
			value = true;
			break;
		case 43: // double step
			value = true;
			break;
		case 17: // logs
			value = true;
			break;
		case 35: // wool
			value = true;
			break;
		case 53: // wood step
			value = true;
			break;
		case 67: // stone step
			value = true;
			break;
		case 18: // FIXME Leaves don't work so well
			value = true;
			break;
		}
		return value;
	}

	/**
	 * Translates a blockID into an itemID for certain block types.
	 * 
	 * For example, a wooden door block's ID is 64, but we want to give the
	 * player the item 324 instead.
	 */
	public static int translateBlockToItemID(int blockId) {
		int itemId = blockId;

		switch (blockId) {
		case 64: // wooden door
			itemId = 324;
			break;
		case 71: // iron door
			itemId = 330;
			break;
		case 26: // bed
			itemId = 355;
			break;
		case 68: // sign
			itemId = 323;
			break;
		case 93: // redstone repeater (off)
			itemId = 356;
			break;
		case 94: // redstone repeater (on)
			itemId = 356;
			break;
		case 75: // redstone torch (off)
			itemId = 76;
			break;
		case 92: // cake
			itemId = 354;
			break;
		case 59: // seeds
			itemId = 295;
			break;
		case 55: // redstone
			itemId = 331;
			break;
		case 43: // convert double steps to single steps
			itemId = 44;
			break;
		}
		return itemId;
	}

}
