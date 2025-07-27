package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.SimpleEntityModel;
import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import net.bunten.enderscape.entity.drifter.Drifter;
import net.bunten.enderscape.registry.EnderscapeBlocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Drifter.class)
public abstract class DrifterMixin extends LivingEntity {
    @Shadow @Final private static TrackedData<Boolean> DRIPPING_JELLY;

    protected DrifterMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private ParticleEffect replaceWithBetterParticle(ParticleEffect particleEffect) {
        return BlockStateModelManager.getParticle(EnderscapeBlocks.DRIFT_JELLY_BLOCK.getDefaultState());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (data == DRIPPING_JELLY) {
            var model = UniqueIdentifiableAttachment.get(this, BasePolymerEntity.MODEL);
            if (model != null && model.holder() instanceof SimpleEntityModel<?> entityModel) {
                //noinspection unchecked
                ((SimpleEntityModel<Drifter>) entityModel).setModel(this.getDataTracker().get(DRIPPING_JELLY) ? EntityModels.DRIFTER_WITH_JELLY : EntityModels.DRIFTER);
            }
        }
    }
}
