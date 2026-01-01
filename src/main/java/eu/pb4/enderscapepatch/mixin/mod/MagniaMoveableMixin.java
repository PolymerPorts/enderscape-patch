package eu.pb4.enderscapepatch.mixin.mod;

import net.bunten.enderscape.entity.magnia.MagniaMoveable;
import net.bunten.enderscape.particle.MagniaParticleOptions;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MagniaMoveable.class)
public interface MagniaMoveableMixin {
    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    static void sendEntityEffectParticles(ServerLevel level, Entity entity, MagniaParticleOptions options, float chance) {
        if (level != null && entity != null && entity.isAlive() && (entity.getDeltaMovement().lengthSqr() > 0.02 || entity.getRandom().nextInt(12) == 0)) {
            AABB box = entity.getBoundingBox();
            Vec3 pos = entity.position().add(0.0, box.getYsize() / (double)(entity instanceof ItemEntity ? 0.5F : 2.0F), 0.0);
            if (level.random.nextFloat() <= chance) {
                level.sendParticles(new DustColorTransitionOptions(options.color(), options.fadeColor(), options.colorFadeRate() * 4), pos.x, pos.y, pos.z, 1, box.getXsize() * 0.6, box.getYsize() * 0.6, box.getZsize() * 0.6, 1.0);
            }
        }

    }
}
