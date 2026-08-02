package eu.pb4.enderscapepatch.mixin.mod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.penumbra.enderscape.registry.level.EnderscapeEnvironmentAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(EnderscapeEnvironmentAttributes.class)
public class EnderscapeEnvironmentAttributesMixin {
    @WrapOperation(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;register(Lnet/minecraft/core/Registry;Lnet/minecraft/resources/Identifier;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object polymerify(Registry<Object> registry, Identifier location, Object value, Operation<Object> original) {
        RegistrySyncUtils.setServerEntry(registry, value);
        return original.call(registry, location, value);
    }

    @Redirect(method = {"lambda$interpolatedVisual$0", "lambda$static$2"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttribute$Builder;syncable()Lnet/minecraft/world/attribute/EnvironmentAttribute$Builder;"))
    private static EnvironmentAttribute.Builder<?> doNotSync(EnvironmentAttribute.Builder<?> instance) {
        return instance;
    }
}
