package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.SimpleEntityModel;
import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.PolyModelInstance;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.EntityModel;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import net.bunten.enderscape.entity.rubblemite.Rubblemite;
import net.bunten.enderscape.entity.rubblemite.RubblemiteVariant;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Rubblemite.class)
public abstract class RubblemiteMixin extends LivingEntity {
    protected RubblemiteMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (data == RubblemiteVariant.DATA) {
            var model = UniqueIdentifiableAttachment.get(this, BasePolymerEntity.MODEL);
            if (model != null && model.holder() instanceof SimpleEntityModel<?> entityModel) {
                //noinspection unchecked
                ((SimpleEntityModel<Rubblemite>) entityModel).setModel(
                        (PolyModelInstance<EntityModel<Rubblemite>>) (Object) EntityModels.RUBBLEMITE.get(RubblemiteVariant.byId(this.getDataTracker().get(RubblemiteVariant.DATA)))
                );
            }
        }
    }
}
