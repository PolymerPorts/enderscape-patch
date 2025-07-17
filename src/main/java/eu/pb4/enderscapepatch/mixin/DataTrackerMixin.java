package eu.pb4.enderscapepatch.mixin;

import eu.pb4.enderscapepatch.impl.DataTrackerHack;
import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;

@Mixin(DataTracker.class)
public class DataTrackerMixin implements DataTrackerHack {
    @Unique
    private final IdentityHashMap<TrackedData<Object>, DataTracker.Entry<Object>> fakeEntries = new IdentityHashMap<>();

    @Inject(method = "getEntry", at = @At("HEAD"), cancellable = true)
    private void handleFakeEntries(TrackedData<?> key, CallbackInfoReturnable<DataTracker.Entry<?>> cir) {
        if (key.id() == EnderscapePolymerPatch.FAKE_TRACKER_INDEX) {
            cir.setReturnValue(fakeEntries.get(key));
        }
    }

    @Override
    public IdentityHashMap<TrackedData<Object>, DataTracker.Entry<Object>> enderscapepatch$getFakes() {
        return this.fakeEntries;
    }
}
