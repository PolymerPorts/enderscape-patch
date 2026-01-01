//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import net.bunten.enderscape.entity.magnia.MagniaMoveable;
import net.bunten.enderscape.entity.magnia.MagniaProperties;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ExperienceOrb.class})
public abstract class ReplacementExperienceOrbMixin extends Entity implements MagniaMoveable {
    @Unique
    private final ExperienceOrb orb = (ExperienceOrb) (Object) this;
    @Unique
    private static final EntityDataAccessor<Integer> MAGNIA_COOLDOWN_DATA;

    public ReplacementExperienceOrbMixin(EntityType<?> type, Level world) {
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
        }, (item) -> {
            item.setNoGravity(false);
        });
    }

    @Unique
    public EntityDataAccessor<Integer> Enderscape$magniaCooldownData() {
        return MAGNIA_COOLDOWN_DATA;
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"defineSynchedData"}
    )
    public void Enderscape$addAdditionalSaveData(SynchedEntityData.Builder builder, CallbackInfo ci) {
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
        MAGNIA_COOLDOWN_DATA = new EntityDataAccessor<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, EntityDataSerializers.INT);
    }
}
