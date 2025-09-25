package eu.pb4.enderscapepatch.mixin.mod;

import net.bunten.enderscape.entity.magnia.MagniaMoveable;
import net.bunten.enderscape.particle.MagniaParticleOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MagniaMoveable.class)
public interface MagniaMoveableMixin {
    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    static void sendEntityEffectParticles(ServerWorld level, Entity entity, MagniaParticleOptions options, float chance) {
        if (level != null && entity != null && entity.isAlive() && (entity.getVelocity().lengthSquared() > 0.02 || entity.getRandom().nextInt(12) == 0)) {
            Box box = entity.getBoundingBox();
            Vec3d pos = entity.getPos().add(0.0, box.getLengthY() / (double)(entity instanceof ItemEntity ? 0.5F : 2.0F), 0.0);
            if (level.random.nextFloat() <= chance) {
                level.spawnParticles(new DustColorTransitionParticleEffect(options.color(), options.fadeColor(), options.colorFadeRate() * 4), pos.x, pos.y, pos.z, 1, box.getLengthX() * 0.6, box.getLengthY() * 0.6, box.getLengthZ() * 0.6, 1.0);
            }
        }

    }
}
