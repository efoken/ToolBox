package de.disposed.bukkit.toolbox;

import java.util.HashMap;

import org.bukkit.block.Block;

public class BlockHandler {

	// these blocks play nicely with the paint tool
	private static int[] safeBlocks = { 1, 2, 3, 4, 5, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
			24, 25, 35, 41, 42, 43, 44, 45, 47, 48, 49, 53, 56, 57, 58, 60, 67, 73, 74, 80, 82, 84,
			85, 86, 87, 88, 89, 91, 92 };

	protected static HashMap<Integer, Boolean> unduplicatableBlocks = new HashMap<Integer, Boolean>();

	protected static HashMap<Integer, Boolean> unmimicableBlocks = new HashMap<Integer, Boolean>();

	protected static HashMap<Integer, Boolean> scrollableBlocks = new HashMap<Integer, Boolean>();

	/**
	 * Checks if the given block is duplicatable.
	 * 
	 * @param block
	 * @return
	 */
	public static boolean isDuplicatableBlock(Block block) {
		return isDuplicatableBlock(block.getTypeId());
	}

	/**
	 * Checks if the given ID is a duplicatable block.
	 * 
	 * @param blockId
	 * @return
	 */
	public static boolean isDuplicatableBlock(int blockId) {
		return !unduplicatableBlocks.containsKey(blockId);
	}

	/**
	 * Checks if the given block is mimicable.
	 * 
	 * @param block
	 * @return
	 */
	public static boolean isMimicableBlock(Block block) {
		return isMimicableBlock(block.getTypeId());
	}

	/**
	 * Checks if the given ID is a mimicable block.
	 * 
	 * @param blockId
	 * @return
	 */
	public static boolean isMimicableBlock(int blockId) {
		return !unmimicableBlocks.containsKey(blockId);
	}

	/**
	 * Checks if the given block is scrollable.
	 * 
	 * @param block
	 * @return
	 */
	public static boolean isScrollableBlock(Block block) {
		return isScrollableBlock(block.getTypeId());
	}

	/**
	 * Checks if the given ID is a scrollable block.
	 * 
	 * @param blockId
	 * @return
	 */
	public static boolean isScrollableBlock(int blockId) {
		return scrollableBlocks.containsKey(blockId);
	}

	/**
	 * Checks if the given block is paintable.
	 * 
	 * @param block
	 * @return
	 */
	public static boolean isPaintableBlock(Block block) {
		return isPaintableBlock(block.getTypeId());
	}

	/**
	 * Checks if the given ID is a paintable block.
	 * 
	 * @param blockId
	 * @return
	 */
	public static boolean isPaintableBlock(int blockId) {
		for (int block : safeBlocks) {
			if (block == blockId) {
				return true;
			}
		}
		return false;
	}

}
