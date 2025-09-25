package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import net.bunten.enderscape.entity.magnia.MagniaMoveable;
import net.bunten.enderscape.entity.magnia.MagniaProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractMinecartEntity.class})
public abstract class ReplacementAbstractMinecartMixin extends Entity implements MagniaMoveable {
    @Unique
    private final AbstractMinecartEntity entity = (AbstractMinecartEntity) (Object) this;
    @Unique
    private static final TrackedData<Integer> MAGNIA_COOLDOWN_DATA;

    public ReplacementAbstractMinecartMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    public MagniaProperties createMagniaProperties() {
        return new MagniaProperties((entity) -> {
            return true;
        }, (entity) -> {
            return 0.6F;
        }, (entity) -> {
            return 0.2F;
        }, DEFAULT_MAGNIA_PREDICATE, (entity) -> {
            entity.setNoGravity(true);
        }, (entity) -> {
            entity.setNoGravity(false);
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
        MagniaMoveable.tickMagniaCooldown(this.entity);
    }

    static {
        MAGNIA_COOLDOWN_DATA = new TrackedData<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, TrackedDataHandlerRegistry.INTEGER);
    }
}
