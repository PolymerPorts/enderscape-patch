package eu.pb4.enderscapepatch.mixin.mod;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.penumbra.enderscape.manager.DashJumpManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DashJumpManager.class)
public class DashJumpManagerMixin {
    @Redirect(method = "createDashJumpParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private static void serverSideParticles(Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
        if (instance instanceof ServerLevel level) {
            level.sendParticles(particle, x, y, z, 0, xd, yd, zd, 1);
        }
    }
}
