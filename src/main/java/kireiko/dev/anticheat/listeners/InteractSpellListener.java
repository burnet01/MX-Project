package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public final class InteractSpellListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerUseItemEvent.class, event -> {
            final var player = event.getPlayer();
            final PlayerProfile profile = PlayerContainer.getProfile(player);
            if (profile == null) return;

            ItemStack item = event.getItemStack();
            if (item.isAir()) return;

            if (item.material() == Material.FISHING_ROD) {
                // Handle hook item
                kireiko.dev.anticheat.api.player.fun.Hook hook = new kireiko.dev.anticheat.api.player.fun.Hook(
                        profile, profile.getTo().add(0, 1.63, 0));
                kireiko.dev.anticheat.services.FunThingsService.add(hook);
            } else if (item.material() == Material.DIAMOND_HOE) {
                // Handle winter staff
                kireiko.dev.anticheat.api.player.fun.Spell spell = new kireiko.dev.anticheat.api.player.fun.Spell(
                        profile, profile.getTo().add(0, 1.63, 0), 0.7, 8,
                        kireiko.dev.anticheat.utils.enums.ParticleTypes.SNOWBALL,
                        kireiko.dev.anticheat.utils.enums.ParticleTypes.SNOWBALL, null);
                kireiko.dev.anticheat.services.FunThingsService.add(spell);
            } else if (item.material() == Material.BLAZE_ROD) {
                // Handle blaze staff
                kireiko.dev.anticheat.api.player.fun.Spell spell = new kireiko.dev.anticheat.api.player.fun.Spell(
                        profile, profile.getTo().add(0, 1.63, 0), 0.6, 10,
                        kireiko.dev.anticheat.utils.enums.ParticleTypes.FLAME,
                        kireiko.dev.anticheat.utils.enums.ParticleTypes.LAVA, null);
                kireiko.dev.anticheat.services.FunThingsService.add(spell);
            } else if (item.material() == Material.BOOK) {
                // Handle cursed spell
                kireiko.dev.anticheat.api.player.fun.Spell spell = new kireiko.dev.anticheat.api.player.fun.Spell(
                        profile, profile.getTo().add(0, 1.63, 0), 0.8, 15,
                        kireiko.dev.anticheat.utils.enums.ParticleTypes.FLAME,
                        kireiko.dev.anticheat.utils.enums.ParticleTypes.LAVA, null);
                kireiko.dev.anticheat.services.FunThingsService.add(spell);
            } else if (item.material() == Material.DIAMOND_HORSE_ARMOR) {
                // Handle rocket launcher
                kireiko.dev.anticheat.core.AsyncScheduler.run(() -> {
                    for (PlayerProfile target : kireiko.dev.anticheat.api.data.PlayerContainer.getUuidPlayerProfileMap().values()) {
                        if (target.getPlayer().getUuid().equals(player.getUuid())) continue;
                        if (player.getPosition().distance(target.getPlayer().getPosition()) < 125) {
                            kireiko.dev.anticheat.api.player.fun.Rocket rocket = new kireiko.dev.anticheat.api.player.fun.Rocket(
                                    profile, target, profile.getTo().add(0, 1.63, 0));
                            kireiko.dev.anticheat.services.FunThingsService.add(rocket);
                            kireiko.dev.anticheat.utils.TitleUtils.sendTitle(player,
                                    kireiko.dev.anticheat.utils.MessageUtils.wrapColors("&a[   +   ]"),
                                    "", 0, 20, 20);
                            break;
                        }
                    }
                });
            } else if (item.material() == Material.GOLDEN_HORSE_ARMOR) {
                // Handle AK-47
                kireiko.dev.anticheat.api.player.fun.Spell spell = new kireiko.dev.anticheat.api.player.fun.Spell(
                        profile, profile.getTo().add(0, 1.63, 0), 1.5, 4,
                        kireiko.dev.anticheat.utils.enums.ParticleTypes.CRIT_MAGIC,
                        kireiko.dev.anticheat.utils.enums.ParticleTypes.CRIT_MAGIC, null);
                kireiko.dev.anticheat.services.FunThingsService.add(spell);
            }
        });
    }
}
