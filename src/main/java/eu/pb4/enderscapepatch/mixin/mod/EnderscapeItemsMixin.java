package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.item.ToggableNebuliteToolPolyItem;
import eu.pb4.enderscapepatch.impl.item.NebuliteToolPolyItem;
import eu.pb4.enderscapepatch.impl.item.PolyBaseItem;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.bunten.enderscape.registry.EnderscapeDataComponents;
import net.bunten.enderscape.registry.EnderscapeItems;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(EnderscapeItems.class)
public class EnderscapeItemsMixin {
    @Inject(method = "registerItem(Lnet/minecraft/registry/RegistryKey;Ljava/util/function/Function;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", at = @At("TAIL"))
    private static void polymerify(RegistryKey<Item> resourceKey, Function<Item.Settings, Item> function, Item.Settings properties, CallbackInfoReturnable<Item> cir) {
        PolymerItem polymerItem;
        var item = cir.getReturnValue();
        if (item.getComponents().contains(EnderscapeDataComponents.ENABLED)) {
            polymerItem = new ToggableNebuliteToolPolyItem();
        } else if (item.getComponents().contains(EnderscapeDataComponents.FUELED_TOOL)) {
            polymerItem = new NebuliteToolPolyItem();
        } else {
            polymerItem = new PolyBaseItem(item);
        }

        PolymerItem.registerOverlay(item, polymerItem);
    }
}
