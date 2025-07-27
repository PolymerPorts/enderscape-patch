package eu.pb4.enderscapepatch.mixin.mod.client;

import net.bunten.enderscape.client.registry.EnderscapeParticleProviders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderscapeParticleProviders.class)
public class EnderscapeParticleProvidersMixin {
    @Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true)
    private static void sayNoToCustomParticles(CallbackInfo ci) {
        ci.cancel();
    }
}
