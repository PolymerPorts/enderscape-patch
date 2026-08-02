package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.item.PolyBaseItem;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.penumbra.enderscape.registry.item.EnderscapeItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(EnderscapeItems.class)
public class EnderscapeItemsMixin {
    @Inject(method = "registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", at = @At("TAIL"))
    private static void polymerify(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function, Item.Properties properties, CallbackInfoReturnable<Item> cir) {
        PolymerItem polymerItem;
        var item = cir.getReturnValue();

        PolymerItem.registerOverlay(item,  new PolyBaseItem(item));
    }
}
