package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.bunten.enderscape.registry.EnderscapeEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderscapeEntities.class)
public class EnderscapeEntitiesMixin {
    @Inject(method = "register(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/entity/EntityType$Builder;)Lnet/minecraft/entity/EntityType;", at = @At("TAIL"))
    private static void polymerify(RegistryKey<EntityType<?>> resourceKey, EntityType.Builder<?> builder, CallbackInfoReturnable<EntityType<Entity>> cir) {
        PolymerEntityUtils.registerOverlay(cir.getReturnValue(), x -> new BasePolymerEntity((LivingEntity) x));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/object/builder/v1/entity/FabricTrackedDataRegistry;register(Lnet/minecraft/util/Identifier;Lnet/minecraft/entity/data/TrackedDataHandler;)V"))
    private static void noop(Identifier id, TrackedDataHandler<?> handler) {}
}
