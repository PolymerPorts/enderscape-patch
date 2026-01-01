package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.SimpleEntityModel;
import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import net.bunten.enderscape.entity.drifter.Drifter;
import net.bunten.enderscape.registry.EnderscapeBlocks;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Drifter.class)
public abstract class DrifterMixin extends LivingEntity {
    @Shadow @Final private static EntityDataAccessor<Boolean> DRIPPING_JELLY;

    protected DrifterMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private ParticleOptions replaceWithBetterParticle(ParticleOptions particleEffect) {
        return BlockStateModelManager.getParticle(EnderscapeBlocks.DRIFT_JELLY_BLOCK.defaultBlockState());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (data == DRIPPING_JELLY) {
            var model = UniqueIdentifiableAttachment.get(this, BasePolymerEntity.MODEL);
            if (model != null && model.holder() instanceof SimpleEntityModel<?> entityModel) {
                //noinspection unchecked
                ((SimpleEntityModel<Drifter>) entityModel).setModel(this.getEntityData().get(DRIPPING_JELLY) ? EntityModels.DRIFTER_WITH_JELLY : EntityModels.DRIFTER);
            }
        }
    }
}
