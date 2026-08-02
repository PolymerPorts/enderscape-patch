package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.other.PolymerMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.penumbra.enderscape.registry.entity.EnderscapeMobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderscapeMobEffects.class)
public class EnderscapeMobEffectsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String name, MobEffect effect, CallbackInfoReturnable<Holder.Reference<MobEffect>> cir) {
        PolymerMobEffect.registerOverlay(effect);
    }
}
