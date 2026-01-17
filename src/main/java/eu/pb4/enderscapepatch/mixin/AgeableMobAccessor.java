package eu.pb4.enderscapepatch.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AgeableMob.class)
public interface AgeableMobAccessor {
    @Accessor
    static EntityDataAccessor<Boolean> getDATA_BABY_ID() {
        throw new UnsupportedOperationException();
    }
}
