package kireiko.dev.anticheat.api.player.fun;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.kyori.adventure.text.Component;

public final class FunItemsService {
    public static void give(final Player player) {
        { // Hook
            final ItemStack rod = ItemStack.builder(Material.FISHING_ROD)
                    .customName(Component.text("§9Hook"))
                    .build();
            player.getInventory().addItemStack(rod);
        }
        { // Rocket Launcher
            final ItemStack rod = ItemStack.builder(Material.DIAMOND_HORSE_ARMOR)
                    .customName(Component.text("§9Rocket Launcher"))
                    .build();
            player.getInventory().addItemStack(rod);
        }
        { // Winter Staff
            final ItemStack rod = ItemStack.builder(Material.DIAMOND_HOE)
                    .customName(Component.text("§9Winter Staff"))
                    .build();
            player.getInventory().addItemStack(rod);
        }
        { // Blaze Staff
            final ItemStack rod = ItemStack.builder(Material.BLAZE_ROD)
                    .customName(Component.text("§9Blaze Staff"))
                    .build();
            player.getInventory().addItemStack(rod);
        }
        { // AK-47
            final ItemStack rod = ItemStack.builder(Material.GOLDEN_HORSE_ARMOR)
                    .customName(Component.text("§9AK-47"))
                    .build();
            player.getInventory().addItemStack(rod);
        }
        { // Cursed Spell
            final ItemStack rod = ItemStack.builder(Material.BOOK)
                    .customName(Component.text("§9Cursed Spell"))
                    .build();
            player.getInventory().addItemStack(rod);
        }
    }
}
