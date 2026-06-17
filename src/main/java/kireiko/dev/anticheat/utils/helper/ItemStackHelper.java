package kireiko.dev.anticheat.utils.helper;

import net.minestom.server.item.ItemStack;

public final class ItemStackHelper {

    public static void setUnbreakable(ItemStack.Builder builder, boolean unbreakable) {
        // Unbreakable component removed - not available in this Minestom version
    }

    public static ItemStack setUnbreakable(ItemStack item, boolean unbreakable) {
        return item;
    }
}
