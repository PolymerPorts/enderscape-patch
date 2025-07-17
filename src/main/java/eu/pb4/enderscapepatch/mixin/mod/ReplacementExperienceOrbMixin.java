//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import net.bunten.enderscape.entity.magnia.MagniaMoveable;
import net.bunten.enderscape.entity.magnia.MagniaProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ExperienceOrbEntity.class})
public abstract class ReplacementExperienceOrbMixin extends Entity implements MagniaMoveable {
    @Unique
    private final ExperienceOrbEntity orb = (ExperienceOrbEntity) (Object) this;
    @Unique
    private static final TrackedData<Integer> MAGNIA_COOLDOWN_DATA;

    public ReplacementExperienceOrbMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    public MagniaProperties createMagniaProperties() {
        return new MagniaProperties((item) -> {
            return false;
        }, (item) -> {
            return 0.6F;
        }, (item) -> {
            return 0.8F;
        }, (item) -> {
            return true;
        }, (item) -> {
            this.orb.setNoGravity(true);
            if (this.random.nextInt(16) == 0) {
                World patt0$temp = this.getWorld();
                if (patt0$temp instanceof ServerWorld) {
                    ServerWorld server = (ServerWorld)patt0$temp;
                    server.spawnParticles(ParticleTypes.END_ROD, this.getPos().x, this.getPos().y + 0.5, this.getPos().z, 1, 0.30000001192092896, 0.3, 0.30000001192092896, 0.0);
                }
            }

        }, (item) -> {
            item.setNoGravity(false);
        });
    }

    @Unique
    public TrackedData<Integer> Enderscape$magniaCooldownData() {
        return MAGNIA_COOLDOWN_DATA;
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"initDataTracker"}
    )
    public void Enderscape$addAdditionalSaveData(DataTracker.Builder builder, CallbackInfo ci) {
        this.defineMagniaData(builder);
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"tick"}
    )
    private void Enderscape$tick(CallbackInfo info) {
        MagniaMoveable.tickMagniaCooldown(this.orb);
    }

    static {
        MAGNIA_COOLDOWN_DATA = new TrackedData<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, TrackedDataHandlerRegistry.INTEGER);
    }
}
