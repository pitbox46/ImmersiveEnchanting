package github.pitbox46.immersive_enchanting.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(at = @At(value = "HEAD"), method = "canApplyAtEnchantingTable")
    private void canApplyAtEnchantingTable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if()
    }
}
