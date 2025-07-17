package eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model;

import net.minecraft.entity.Entity;

public abstract class EntityModel<T extends Entity> extends Model {
    protected EntityModel(ModelPart root) {
        super(root);
    }

    public void setAngles(T state) {
        this.resetTransforms();
    }
}