package github.pitbox46.immersive_enchanting.cap;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Events {
    public static Capability<EnchantmentKnowledgeCap> ENCHANTMENT_KNOWLEDGE = CapabilityManager.get(new CapabilityToken<>(){});

    @SubscribeEvent
    public static void attachEntityCap(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            event.addCapability(EnchantmentKnowledgeCap.RL, EnchantmentKnowledgeCap.createProvider());
        }
    }

    @SubscribeEvent
    public static void copyEntityCap(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(ENCHANTMENT_KNOWLEDGE).ifPresent(oldCap ->
                event.getPlayer().getCapability(ENCHANTMENT_KNOWLEDGE).ifPresent(newCap -> newCap.deserializeNBT(oldCap.serializeNBT()))
        );
        event.getOriginal().invalidateCaps();
    }
}
