package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.item.PolyBaseItem;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.bunten.enderscape.registry.EnderscapeBlockEntities;
import net.bunten.enderscape.registry.EnderscapeItems;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(EnderscapeBlockEntities.class)
public class EnderscapeBlockEntitiesMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String name, BlockEntityType<?> type, CallbackInfoReturnable<BlockEntityType<?>> cir) {
        PolymerBlockUtils.registerBlockEntity(type);
    }
}
