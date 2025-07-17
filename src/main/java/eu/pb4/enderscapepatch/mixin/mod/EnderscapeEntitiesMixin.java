package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.bunten.enderscape.registry.EnderscapeEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderscapeEntities.class)
public class EnderscapeEntitiesMixin {
    @Inject(method = "register(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/entity/EntityType$Builder;)Lnet/minecraft/entity/EntityType;", at = @At("TAIL"))
    private static void polymerify(RegistryKey<EntityType<?>> resourceKey, EntityType.Builder<?> builder, CallbackInfoReturnable<EntityType<Entity>> cir) {
        PolymerEntityUtils.registerOverlay(cir.getReturnValue(), x -> new BasePolymerEntity((Entity) x));
    }
}
