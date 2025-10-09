package eu.pb4.enderscapepatch.mixin.mod;

import net.bunten.enderscape.registry.EnderscapeEntityDataSerializers;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.UnaryOperator;

@Mixin(EnderscapeEntityDataSerializers.class)
public class EnderscapeEntityDataSerializersMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/object/builder/v1/entity/FabricTrackedDataRegistry;register(Lnet/minecraft/util/Identifier;Lnet/minecraft/entity/data/TrackedDataHandler;)V"))
    private static void noop(Identifier id, TrackedDataHandler<?> handler) {}
}
