package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import eu.pb4.polymer.core.api.other.PolymerSoundEvent;
import net.bunten.enderscape.Enderscape;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enderscape.class)
public class EnderscapeMixin {
    @Inject(method = "registerSoundEvent", at = @At("TAIL"))
    private static void polymerifySound(String name, CallbackInfoReturnable<SoundEvent> cir) {
        PolymerSoundEvent.registerOverlay(cir.getReturnValue());
    }

    @Inject(method = "registerSoundEventHolder", at = @At("TAIL"))
    private static void polymerifySound2(String name, CallbackInfoReturnable<Holder<SoundEvent>> cir) {
        PolymerSoundEvent.registerOverlay(cir.getReturnValue().value());
    }

    @Inject(method = "onInitialize", at = @At("TAIL"))
    private void lateInit(CallbackInfo ci) {
        EnderscapePolymerPatch.LATE_INIT.forEach(Runnable::run);
        EnderscapePolymerPatch.LATE_INIT.clear();
    }
}
