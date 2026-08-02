package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.enderscapepatch.mixin.AgeableMobAccessor;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.PolyModelInstance;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.SimpleEntityModel;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.penumbra.enderscape.entity.rustle.Rustle;
import net.penumbra.enderscape.entity.rustle.RustleConversionPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Rustle.class)
public abstract class RustleMixin extends Animal {

    @Shadow
    public abstract RustleConversionPhase getConversionPhase();

    @Shadow
    protected abstract void soloConversionAnimation(AnimationState value);

    @Shadow
    @Final
    public AnimationState conversionBeginAnimationState;

    @Shadow
    @Final
    public AnimationState conversionAnimationState;

    @Shadow
    @Final
    public AnimationState conversionEndAnimationState;

    @Shadow
    @Final
    private static EntityDataAccessor<RustleConversionPhase> CONVERSION_PHASE;

    protected RustleMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (CONVERSION_PHASE.equals(data)) {
            switch (this.getConversionPhase()) {
                case BEGINNING -> this.soloConversionAnimation(this.conversionBeginAnimationState);
                case CONVERTING -> this.soloConversionAnimation(this.conversionAnimationState);
                case ENDING -> this.soloConversionAnimation(this.conversionEndAnimationState);
            }
        }

        super.onSyncedDataUpdated(data);
        if (data == AgeableMobAccessor.getDATA_BABY_ID()) {
            var model = UniqueIdentifiableAttachment.get(this, BasePolymerEntity.MODEL);
            if (model != null && model.holder() instanceof SimpleEntityModel<?> entityModel) {
                //noinspection unchecked,rawtypes
                ((SimpleEntityModel<Rustle>) entityModel).setModel(this.isBaby() ? (PolyModelInstance) EntityModels.BABY_RUSTLE : EntityModels.RUSTLE);
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean runClientLogic(Level instance) {
        return true;
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;defineId(Ljava/lang/Class;Lnet/minecraft/network/syncher/EntityDataSerializer;)Lnet/minecraft/network/syncher/EntityDataAccessor;"))
    private static EntityDataAccessor<Object> forceFakeForSerializerCheckSkip(Class<? extends SyncedDataHolder> entityClass, EntityDataSerializer<Object> dataHandler) {
        return new EntityDataAccessor<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, dataHandler);
    }
}
