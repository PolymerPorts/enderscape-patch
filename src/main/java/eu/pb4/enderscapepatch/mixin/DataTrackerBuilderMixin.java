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

@Mixin(DataTracker.Builder.class)
public class DataTrackerBuilderMixin {
    @Unique
    private final IdentityHashMap<TrackedData<Object>, DataTracker.Entry<Object>> fakeEntries = new IdentityHashMap<>();

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void addFakeEntry(TrackedData<Object> data, Object value, CallbackInfoReturnable<DataTracker.Builder> cir) {
        if (data.id() == EnderscapePolymerPatch.FAKE_TRACKER_INDEX) {
            fakeEntries.put(data, new DataTracker.Entry<>(data, value));
            cir.setReturnValue((DataTracker.Builder) (Object) this);
        }
    }

    @Inject(method = "build", at = @At("TAIL"))
    private void addFakeEntryToFinal(CallbackInfoReturnable<DataTracker> cir) {
        ((DataTrackerHack) cir.getReturnValue()).enderscapepatch$getFakes().putAll(this.fakeEntries);
    }
}
