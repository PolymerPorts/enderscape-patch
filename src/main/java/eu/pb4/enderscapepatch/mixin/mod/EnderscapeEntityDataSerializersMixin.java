package eu.pb4.enderscapepatch.mixin.mod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.enderscapepatch.mixin.FabricTrackedDataRegistryImplAccessor;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.bunten.enderscape.registry.EnderscapeEntityDataSerializers;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderscapeEntityDataSerializers.class)
public abstract class EnderscapeEntityDataSerializersMixin {
    @WrapOperation(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/object/builder/v1/entity/FabricTrackedDataRegistry;register(Lnet/minecraft/util/Identifier;Lnet/minecraft/entity/data/TrackedDataHandler;)V"
        )
    )
    private static void dontRegister(Identifier id, TrackedDataHandler<?> handler, Operation<Void> original) {
        original.call(id, handler);
        RegistrySyncUtils.setServerEntry(FabricTrackedDataRegistryImplAccessor.getHandlerRegistry(), handler);
    }
}
