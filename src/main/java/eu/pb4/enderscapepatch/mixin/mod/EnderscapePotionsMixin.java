package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.bunten.enderscape.registry.EnderscapeDataComponents;
import net.bunten.enderscape.registry.EnderscapePotions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(EnderscapePotions.class)
public class EnderscapePotionsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String name, Potion potion, CallbackInfoReturnable<Holder.Reference<Potion>> cir) {
        PolymerSyncedObject.setSyncedObject(BuiltInRegistries.POTION, potion, (obj, ctx) -> Potions.LUCK.value());
    }
}
