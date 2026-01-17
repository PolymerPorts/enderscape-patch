package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.enderscapepatch.mixin.AgeableMobAccessor;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.virtualentity.emuvanilla2.PolyModelInstance;
import eu.pb4.factorytools.api.virtualentity.emuvanilla2.model.EntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla2.poly.SimpleEntityModel;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import net.bunten.enderscape.entity.drifter.Drifter;
import net.bunten.enderscape.entity.rustle.Rustle;
import net.bunten.enderscape.registry.EnderscapeBlocks;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Rustle.class)
public abstract class RustleMixin extends Animal {

    protected RustleMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (data == AgeableMobAccessor.getDATA_BABY_ID()) {
            var model = UniqueIdentifiableAttachment.get(this, BasePolymerEntity.MODEL);
            if (model != null && model.holder() instanceof SimpleEntityModel<?> entityModel) {
                //noinspection unchecked,rawtypes
                ((SimpleEntityModel<Rustle>) entityModel).setModel(this.isBaby() ? (PolyModelInstance) EntityModels.BABY_RUSTLE : EntityModels.RUSTLE);
            }
        }
    }
}
