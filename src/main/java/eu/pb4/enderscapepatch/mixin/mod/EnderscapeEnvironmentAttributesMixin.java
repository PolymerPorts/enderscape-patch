package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.bunten.enderscape.registry.EnderscapeEnvironmentAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.attribute.EnvironmentAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EnderscapeEnvironmentAttributes.class)
public class EnderscapeEnvironmentAttributesMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String name, EnvironmentAttribute.Builder<?> builder, CallbackInfoReturnable<EnvironmentAttribute<?>> cir) {
        RegistrySyncUtils.setServerEntry(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, cir.getReturnValue());
    }


    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttribute$Builder;syncable()Lnet/minecraft/world/attribute/EnvironmentAttribute$Builder;"))
    private static EnvironmentAttribute.Builder<?> untrack(EnvironmentAttribute.Builder<?> instance) {
        return instance;
    }
}
