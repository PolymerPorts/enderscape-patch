package eu.pb4.enderscapepatch.mixin.mod;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.penumbra.enderscape.manager.VoidManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VoidManager.class)
public class VoidManagerMixin {
    @Shadow
    public static float getVoidTicksPercentage(Entity entity) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "tickTail", at = @At("TAIL"))
    private static void emulateVoidOverlay(Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayer player) {
            if (getVoidTicksPercentage(player) > 0.45) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 99, true, false, false));
            }
        }
    }

    @Redirect(method = "tickVoidedParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private static void serverSideParticles(Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
        if (instance instanceof ServerLevel level) {
            level.sendParticles(particle, x, y, z, 0, xd, yd, zd, 1);
        }
    }
}
