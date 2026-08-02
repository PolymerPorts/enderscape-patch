package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.penumbra.enderscape.registry.item.EnderscapePotions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(EnderscapePotions.class)
public class EnderscapePotionsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(ResourceKey<Potion> key, String potionName, MobEffectInstance instance, CallbackInfoReturnable<Holder.Reference<Potion>> cir) {
        PolymerSyncedObject.setSyncedObject(BuiltInRegistries.POTION, cir.getReturnValue().value(), (_, _) -> Potions.LUCK.value());
    }
}
