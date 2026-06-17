package kireiko.dev.anticheat.api.player.fun;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.entity.Player;

public final class PhantomWorld {

    public static void setBlock(Player player, Pos location, Material blockType) {
        Instance instance = player.getInstance();
        if (instance != null) {
            instance.setBlock((int) location.x(), (int) location.y(), (int) location.z(), Block.fromKey(blockType.key()));
        }
    }

    public static void setLocalTime(Player player, long time) {
        Instance instance = player.getInstance();
        if (instance != null) {
            instance.setTime(time);
        }
    }

    public static void setLocalHealthAndHunger(Player player, float hp, int hunger) {
        // Minestom handles health/hunger differently
        // This is a simplified version
    }
}
