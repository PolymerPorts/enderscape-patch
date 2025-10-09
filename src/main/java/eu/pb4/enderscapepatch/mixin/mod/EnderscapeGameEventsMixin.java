package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.bunten.enderscape.registry.EnderscapeGameEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EnderscapeGameEvents.class)
public class EnderscapeGameEventsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String name, int i, CallbackInfoReturnable<RegistryEntry.Reference<GameEvent>> cir) {
        PolymerSyncedObject.setSyncedObject(Registries.GAME_EVENT, cir.getReturnValue().value(), (gameEvent, packetContext) -> GameEvent.FLAP.value());
    }
}
