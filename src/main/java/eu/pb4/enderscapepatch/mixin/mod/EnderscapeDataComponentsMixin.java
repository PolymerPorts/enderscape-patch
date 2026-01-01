package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.bunten.enderscape.registry.EnderscapeDataComponents;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(EnderscapeDataComponents.class)
public class EnderscapeDataComponentsMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String string, UnaryOperator<DataComponentType.Builder<?>> unaryOperator, CallbackInfoReturnable<DataComponentType<?>> cir) {
        PolymerComponent.registerDataComponent(cir.getReturnValue());
    }
}
