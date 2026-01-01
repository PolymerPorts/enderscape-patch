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

@Mixin(SynchedEntityData.Builder.class)
public class DataTrackerBuilderMixin {
    @Unique
    private final IdentityHashMap<EntityDataAccessor<Object>, SynchedEntityData.DataItem<Object>> fakeEntries = new IdentityHashMap<>();

    @Inject(method = "define", at = @At("HEAD"), cancellable = true)
    private void addFakeEntry(EntityDataAccessor<Object> data, Object value, CallbackInfoReturnable<SynchedEntityData.Builder> cir) {
        if (data.id() == EnderscapePolymerPatch.FAKE_TRACKER_INDEX) {
            fakeEntries.put(data, new SynchedEntityData.DataItem<>(data, value));
            cir.setReturnValue((SynchedEntityData.Builder) (Object) this);
        }
    }

    @Inject(method = "build", at = @At("TAIL"))
    private void addFakeEntryToFinal(CallbackInfoReturnable<SynchedEntityData> cir) {
        ((DataTrackerHack) cir.getReturnValue()).enderscapepatch$getFakes().putAll(this.fakeEntries);
    }
}
