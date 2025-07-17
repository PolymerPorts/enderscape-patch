package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.bunten.enderscape.registry.EnderscapeDataComponents;
import net.bunten.enderscape.registry.EnderscapeEnchantmentEffectComponents;
import net.minecraft.component.ComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(EnderscapeEnchantmentEffectComponents.class)
public class EnderscapeEnchantmentEffectComponentsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String string, UnaryOperator<ComponentType.Builder<?>> unaryOperator, CallbackInfoReturnable<ComponentType<?>> cir) {
        PolymerComponent.registerEnchantmentEffectComponent(cir.getReturnValue());
    }
}
