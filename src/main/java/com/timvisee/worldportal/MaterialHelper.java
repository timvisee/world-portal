package com.timvisee.worldportal;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class MaterialHelper {

    /**
     * Check whether the given block allows a teleportable action.
     *
     * @param block The block.
     *
     * @return True if teleportable, false if not.
     */
    public static boolean isTeleportable(Block block) {
        return block != null && isTeleportable(block.getType());
    }

    /**
     * Check whether the given block allows a teleportable action.
     *
     * @param material Material.
     *
     * @return True if teleportable, false if not.
     */
    public static boolean isTeleportable(Material material) {
        switch(material) {
            case STONE_PLATE:
            case WOOD_PLATE:
            case IRON_PLATE:
            case GOLD_PLATE:
            case LEVER:
            case STONE_BUTTON:
            case WOOD_BUTTON:
            case SIGN:
            case SIGN_POST:
            case WALL_SIGN:
            case DISPENSER:
            case CHEST:
            case TRAPPED_CHEST:
            case JUKEBOX:
            case TRAP_DOOR:
            case IRON_TRAPDOOR:
            case WOOD_DOOR:
            case WOODEN_DOOR:
            case IRON_DOOR:
            case IRON_DOOR_BLOCK:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case ENCHANTMENT_TABLE:
            case ENDER_CHEST:
            case COMMAND:
            case COMMAND_REPEATING:
            case COMMAND_CHAIN:
            case ANVIL:
            case HOPPER:
            case DROPPER:
            case BED:
            case BED_BLOCK:
            case END_GATEWAY:
            case PORTAL:
                return true;
            default:
                return false;
        }
    }
}
