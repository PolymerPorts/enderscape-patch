package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.bunten.enderscape.registry.EnderscapeEntities;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderscapeEntities.class)
public class EnderscapeEntitiesMixin {
    @Inject(method = "register(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/EntityType$Builder;)Lnet/minecraft/world/entity/EntityType;", at = @At("TAIL"))
    private static void polymerify(ResourceKey<EntityType<?>> resourceKey, EntityType.Builder<?> builder, CallbackInfoReturnable<EntityType<Entity>> cir) {
        PolymerEntityUtils.registerOverlay(cir.getReturnValue(), x -> new BasePolymerEntity((LivingEntity) x));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/object/builder/v1/entity/FabricTrackedDataRegistry;register(Lnet/minecraft/resources/Identifier;Lnet/minecraft/network/syncher/EntityDataSerializer;)V"))
    private static void noop(Identifier id, EntityDataSerializer<?> handler) {}
}
