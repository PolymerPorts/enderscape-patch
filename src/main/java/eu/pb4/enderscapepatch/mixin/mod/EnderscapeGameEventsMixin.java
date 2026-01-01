package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.bunten.enderscape.registry.EnderscapeGameEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EnderscapeGameEvents.class)
public class EnderscapeGameEventsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String name, int i, CallbackInfoReturnable<Holder.Reference<GameEvent>> cir) {
        PolymerSyncedObject.setSyncedObject(BuiltInRegistries.GAME_EVENT, cir.getReturnValue().value(), (gameEvent, packetContext) -> GameEvent.FLAP.value());
    }
}
