package eu.pb4.enderscapepatch.mixin.mod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.bunten.enderscape.registry.EnderscapeGameEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderscapeGameEvents.class)
public abstract class EnderscapeGameEventsMixin {
    @WrapOperation(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/bunten/enderscape/registry/EnderscapeGameEvents;register(Ljava/lang/String;I)Lnet/minecraft/registry/entry/RegistryEntry$Reference;"
        )
    )
    private static RegistryEntry.Reference<GameEvent> polymerify(String name, int i, Operation<RegistryEntry.Reference<GameEvent>> original) {
        RegistryEntry.Reference<GameEvent> reference = original.call(name, i);
        RegistrySyncUtils.setServerEntry(Registries.GAME_EVENT, reference.value());
        return reference;
    }
}
