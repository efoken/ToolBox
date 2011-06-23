package de.disposed.bukkit.toolbox;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandHandler {

	protected static int mimicRadius = 40;

	/**
	 * Clears the player's inventory.
	 * 
	 * @param player
	 * @param args
	 * @return True on success
	 */
	public static boolean handleClearCommand(Player player, String[] args) {
		PlayerInventory inventory = player.getInventory();

		int first = 9;
		int last = inventory.getSize();

		if (args.length > 0) {
			if (args[0].trim().equalsIgnoreCase("all")) {
				first = 0;
			} else if (args[0].trim().equalsIgnoreCase("main")) {
				first = 9;
			} else if (args[0].trim().equalsIgnoreCase("bar")) {
				first = 0;
				last = 9;
			}
		}

		for (int i = first; i < last; i++) {
			inventory.clear(i);
		}

		player.sendMessage(ChatColor.GREEN + "Inventory cleared!");
		return true;
	}

	/**
	 * Mimicks the nearby blocks in the player's inventory.
	 * 
	 * @param player
	 * @param args
	 * @return True on success
	 */
	public static boolean handleMimicCommand(Player player, String[] args) {
		int radius = 30;

		if (args.length > 0) {
			try { // make sure that a number is given
				radius = Integer.parseInt(args[0].trim());
			} catch (NumberFormatException e) {
				radius = 30;
			}
		}
		if (radius > mimicRadius) {
			radius = mimicRadius;
			player.sendMessage(ChatColor.RED + "Radius too large. Reset to maximum of "
					+ mimicRadius);
		}

		Location location = player.getLocation();
		PlayerInventory inventory = player.getInventory();
		World world = player.getWorld();
		Block block;

		int blocks[][] = new int[95][16];
		int xPos = location.getBlockX();
		int yPos = -1;
		if (location.getBlockY() - radius / 5 > 0) {
			yPos = location.getBlockY() - radius / 5;
		} else {
			yPos = location.getBlockY();
		}
		int zPos = location.getBlockZ();

		for (int x = radius; x >= 0; x--) {
			for (int y = radius; y >= 0; y--) {
				for (int z = radius; z >= 0; z--) {
					if (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) > Math.pow(
							(double) radius + 0.5, 2)) {
						continue;
					}
					block = world.getBlockAt(xPos + x, yPos + y, zPos + z);
					blocks[block.getTypeId()][block.getData() > 15 ? 15 : block.getData()]++;
					block = world.getBlockAt(xPos + x, yPos + y, zPos - z);
					blocks[block.getTypeId()][block.getData() > 15 ? 15 : block.getData()]++;
					block = world.getBlockAt(xPos - x, yPos + y, zPos + z);
					blocks[block.getTypeId()][block.getData() > 15 ? 15 : block.getData()]++;
					block = world.getBlockAt(xPos - x, yPos + y, zPos - z);
					blocks[block.getTypeId()][block.getData() > 15 ? 15 : block.getData()]++;
				}
			}
		}

		int sum = 0;
		for (int k = 0; k <= 94; k++) {
			if (k != 8 && k != 10 && k != 18 && k != 23 && k != 25 && k != 26 && k != 46 && k != 50
					&& k != 51 && k != 52 && k != 53 && k != 54 && k != 55 && k != 61 && k != 62
					&& k != 63 && k != 64 && k != 65 && k != 66 && k != 67 && k != 68 && k != 69
					&& k != 70 && k != 71 && k != 72 && k != 75 && k != 76 && k != 77 && k != 84
					&& k != 85 && k != 86 && k != 90 && k != 91 && k != 92 && k != 93 && k != 94) {
				continue;
			}
			sum = 0;
			for (int l = 0; l <= 15; l++) {
				sum += blocks[k][l];
				blocks[k][l] = 0;
			}
			blocks[k][0] = sum;
		}

		for (int k = 0; k <= 94; k++) {
			if (!BlockHandler.isMimicableBlock(k)) {
				for (int l = 0; l <= 15; l++) {
					blocks[k][l] = 0;
				}
			}
		}

		for (int i = 0, max = 0, item = 0, data = 0; i <= 17; i++) {
			max = 0;
			item = 0;
			data = 0;

			for (int j = 1; j <= 94; j++) {
				for (int m = 0; m <= 15; m++) {
					if (blocks[j][m] > max) {
						max = blocks[j][m];
						item = j;
						data = m;
					}
				}
			}

			if (item != 0) {
				ItemStack stack = inventory.getItem(i + 9);
				stack.setTypeId(item);
				stack.setAmount(64);
				stack.setDurability((short) data);
				inventory.setItem(i + 9, stack);
				blocks[item][data] = 0;
			} else {
				break;
			}
		}

		player.sendMessage(ChatColor.LIGHT_PURPLE + "Environment mimicked in inventory.");
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
							&& (!BlockHandler.isScrollableBlock(item.getTypeId()) || item
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
	 * Handles the /more command.
	 * 
	 * Finishes the player's current stack, and gives them the specified number
	 * more stacks.
	 * 
	 * @param player
	 * @param args
	 * @return True on success
	 */
	public static boolean handleMoreCommand(Player player, String[] args) {
		int numStacks = 1;

		if (args.length > 0) {
			try { // make sure that a number is given
				numStacks = Integer.parseInt(args[0].trim());
			} catch (NumberFormatException e) {
				numStacks = 1;
			}
		}

		if (player.getItemInHand().getTypeId() != 0) {
			if (!ToolHandler.isTool(player.getItemInHand().getTypeId())) {
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
				player.sendMessage(ChatColor.RED + "Tools are not able to be duplicated.");
			}
		}
		return false;
	}

	/**
	 * Handles the /pick command
	 * 
	 * Attempts to set the data value of whatever the player is holding to the
	 * specified number.
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
				} catch (NumberFormatException e) {
					newDataValue = 0;
				}

			}

			// if everything is alright, change the data value
			if ((newDataValue <= ToolboxUtils.getMaxDataValue(player.getItemInHand().getTypeId()))
					&& (newDataValue >= 0)) {
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
		if (numStacks > player.getInventory().getSize()) {
			// number of stacks should exceed the inventory space
			numStacks = player.getInventory().getSize();
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
