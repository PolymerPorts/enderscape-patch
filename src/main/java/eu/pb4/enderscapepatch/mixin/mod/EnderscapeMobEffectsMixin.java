package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import net.bunten.enderscape.registry.EnderscapeDataComponents;
import net.bunten.enderscape.registry.EnderscapeMobEffects;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(EnderscapeMobEffects.class)
public class EnderscapeMobEffectsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String name, StatusEffect effect, CallbackInfoReturnable<RegistryEntry.Reference<StatusEffect>> cir) {
        PolymerStatusEffect.registerOverlay(effect);
    }
}
