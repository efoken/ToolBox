package com.bloodchild.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandHandler {

	private static final int MAX_STACKS = 36;

	//private static int[] unmimicable = { 0, 1, 2, 3, 66, 7, 8, 9, 10, 79, 11, 12, 46, 13, 51 };

	/**
	 * Clears the player's inventory.
	 * 
	 * @param player
	 * @return True on success
	 */
	public static boolean handleClearCommand(Player player) {
		player.getInventory().clear();
		player.sendMessage("Inventory cleared!");
		return true;
	}

	/**
	 * 
	 * @param player
	 * @param args
	 * @return
	 *\/
	public static boolean handleNearCommand(Player player, String[] args) {
		int radius = 10;
		HashSet<Material> give = new HashSet<Material>();

		if (args.length > 0) {
			radius = Integer.parseInt(args[0]);
		}

		Location location = player.getLocation();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					Block block = player.getWorld().getBlockAt(x, y, z);
					give.add(block.getType());
				}
			}
		}

		Iterator<Material> itr = give.iterator();
		while (itr.hasNext()) {
			Material item = itr.next();
			if (!Arrays.asList(unmimicable).contains(item.getId())) {
				player.sendMessage(String.valueOf(item.getId()));
				ItemStack stack = new ItemStack(item, 64);
				player.getInventory().addItem(stack);
			}
		}
		return true;
	}

	/**
	 * Compacts the player's inventory.
	 * 
	 * @param player
	 * @return True on success
	 */
	public static boolean handleCompactComment(Player player) {
		ItemStack[] items = player.getInventory().getContents();

		int affected = 0;

		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];

			// avoid infinite stacks and stacks with durability
			// TODO Check if item is stackable
			if (item == null || item.getAmount() <= 0) {
				continue;
			}

			if (item.getAmount() < 64) {
				int needed = 64 - item.getAmount();

				// find another stack of the same type
				for (int j = i + 1; j < items.length; j++) {
					ItemStack item2 = items[j];

					// avoid infinite stacks and stacks with durability
					// TODO Check if item is stackable
					if (item2 == null || item2.getAmount() <= 0) {
						continue;
					}

					// check if the item has the same type
					if (item.getTypeId() == item2.getTypeId()
							&& (!ToolboxUtils.isScrollableBlock(item.getTypeId()) || item
									.getDurability() == item2.getDurability())) {
						if (item2.getAmount() > needed) {
							item.setAmount(64);
							item2.setAmount(item2.getAmount() - needed);
							break;
						} else {
							items[j] = null;
							item.setAmount(item.getAmount() + item2.getAmount());
							needed = 64 - item.getAmount();
						}

						affected++;
					}
				}
			}
		}

		if (affected > 0) {
			player.getInventory().setContents(items);
		}

		player.sendMessage("Inventory compacted!");
		return true;
	}

	/**
	 * Handles the /more command Finishes the player's current stack, and gives
	 * them the specified number more stacks.
	 * 
	 * @param player
	 * @param args
	 * @return True on success
	 */
	public static boolean handleMoreCommand(Player player, String[] args) {
		int numStacks = 1;

		if (args.length > 0) {
			try {
				numStacks = Integer.parseInt(args[0].trim());
			} catch (Exception e) {
				numStacks = 1;
			}
		}

		if (player.getItemInHand().getTypeId() != 0) {
			if (player.getItemInHand().getAmount() < 64) {
				player.getItemInHand().setAmount(64);
				numStacks--;
			}
			if (numStacks > 0) {
				giveStack(player, player.getItemInHand(), numStacks);
			}

			player.sendMessage("Here you've got some more " + player.getItemInHand().getType());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Handles the /pick command - /pick <#> Attempts to set the data value of
	 * whatever the player is holding to the specified number.
	 * 
	 * @param player
	 * @param args
	 * @return True on success
	 */
	public static boolean handlePickCommand(Player player, String[] args) {
		if (player.getItemInHand().getTypeId() != 0) {
			int newDataValue = 0;

			if (args.length > 0) {
				try {
					newDataValue = Integer.parseInt(args[0].trim());
				} catch (Exception e) {
					newDataValue = 0;
				}

			}

			/*
			 * Check to see if we allow this block's data to be modified and
			 * figure out the appropriate data values for it
			 */
			int maxData = -1;
			switch (player.getItemInHand().getTypeId()) {
			case 44:
				maxData = 3;
				break;
			case 43:
				maxData = 3;
				break;
			case 17:
				maxData = 2;
				break;
			case 35:
				maxData = 15;
				break;
			case 53:
				maxData = 3;
				break;
			case 67:
				maxData = 3;
				break;
			case 18:
				maxData = 2;
				break;
			case 6:
				maxData = 2;
				break;
			}

			// if everything is alright, change the data value
			if ((newDataValue <= maxData) && (newDataValue >= 0)) {
				player.getItemInHand().setDurability((short) newDataValue);
			}
		}
		return true;
	}

	/**
	 * Gives the player one stack of the given item.
	 */
	public static boolean giveStack(Player player, ItemStack stack) {
		player.getInventory().addItem(stack);
		return true;
	}

	/**
	 * Gives the player the specified number of stacks of a given item.
	 */
	public static boolean giveStack(Player player, ItemStack stack, int numStacks) {
		if (numStacks > MAX_STACKS) {
			// number of stacks should exceed the inventory space
			numStacks = MAX_STACKS;
		}

		for (int x = 0; x < numStacks; x++) {
			ItemStack giveItem = new ItemStack(stack.getTypeId(), 64, (byte) 0);
			giveItem.setData(stack.getData());
			giveItem.setDurability(stack.getDurability());
			player.getInventory().addItem(giveItem);
		}
		return true;
	}

	/**
	 * Gives the player a duplicator tool.
	 * 
	 * @param player
	 */
	public static void giveDuplicatorTool(Player player) {
		ItemStack duplicator = new ItemStack(ToolHandler.duplicatorTool, 1);
		player.sendMessage(ChatColor.DARK_PURPLE + "Right click: Duplicate block");
		player.getInventory().addItem(duplicator);
	}

	/**
	 * Gives the player a paintbrush tool.
	 * 
	 * @param player
	 */
	public static void givePaintbrushTool(Player player) {
		ItemStack paintbrush = new ItemStack(ToolHandler.paintbrushTool, 1);
		player.sendMessage(ChatColor.DARK_PURPLE + "Left click: Set ink; Right click: Brush ink");
		player.getInventory().addItem(paintbrush);
	}

	/**
	 * Gives the player a scroller tool.
	 * 
	 * @param player
	 */
	public static void giveScrollerTool(Player player) {
		ItemStack scroller = new ItemStack(ToolHandler.scrollerTool, 1);
		player.sendMessage(ChatColor.DARK_PURPLE + "Right click: Scroll data value");
		player.getInventory().addItem(scroller);
	}

}
