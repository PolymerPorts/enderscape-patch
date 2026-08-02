package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.penumbra.enderscape.registry.block.EnderscapeFluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EnderscapeFluids.class)
public class EnderscapeFluidsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(ResourceKey<Fluid> key, Fluid fluid, CallbackInfoReturnable<Fluid> cir) {
        PolymerSyncedObject.setSyncedObject(BuiltInRegistries.FLUID, cir.getReturnValue(), (gameEvent, packetContext) -> Fluids.LAVA);
    }
}
