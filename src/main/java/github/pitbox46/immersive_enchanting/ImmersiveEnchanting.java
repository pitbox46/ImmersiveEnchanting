package github.pitbox46.immersive_enchanting;

import com.mojang.logging.LogUtils;
import github.pitbox46.immersive_enchanting.cap.Events;
import github.pitbox46.immersive_enchanting.enchantment_tree.EnchantmentTreeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ImmersiveEnchanting.ID)
public class ImmersiveEnchanting {
    public static final String ID = "immersive_enchanting";
    static final Logger LOGGER = LogUtils.getLogger();

    public ImmersiveEnchanting() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(EnchantmentTreeManager::addResourceListener);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EnchantmentDatagen::onGatherDataEvent);
        MinecraftForge.EVENT_BUS.register(Events.class);
    }

}
