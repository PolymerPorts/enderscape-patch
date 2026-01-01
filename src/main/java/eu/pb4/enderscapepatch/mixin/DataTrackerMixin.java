package eu.pb4.enderscapepatch.mixin;

import eu.pb4.enderscapepatch.impl.DataTrackerHack;
import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

@Mixin(SynchedEntityData.class)
public class DataTrackerMixin implements DataTrackerHack {
    @Unique
    private final IdentityHashMap<EntityDataAccessor<Object>, SynchedEntityData.DataItem<Object>> fakeEntries = new IdentityHashMap<>();

    @Inject(method = "getItem", at = @At("HEAD"), cancellable = true)
    private void handleFakeEntries(EntityDataAccessor<?> key, CallbackInfoReturnable<SynchedEntityData.DataItem<?>> cir) {
        if (key.id() == EnderscapePolymerPatch.FAKE_TRACKER_INDEX) {
            cir.setReturnValue(fakeEntries.get(key));
        }
    }

    @Override
    public IdentityHashMap<EntityDataAccessor<Object>, SynchedEntityData.DataItem<Object>> enderscapepatch$getFakes() {
        return this.fakeEntries;
    }
}
