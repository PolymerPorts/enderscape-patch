package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.penumbra.enderscape.registry.block.EnderscapeBlockEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderscapeBlockEntities.class)
public class EnderscapeBlockEntitiesMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(ResourceKey<BlockEntityType<?>> key, BlockEntityType<?> type, CallbackInfoReturnable<BlockEntityType<?>> cir) {
        PolymerBlockUtils.registerBlockEntity(type);
    }
}
