package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.bunten.enderscape.registry.EnderscapeCreativeModeTab;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderscapeCreativeModeTab.class)
public class EnderscapeCreativeModeTabMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;register(Lnet/minecraft/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object polymerify(Registry<?> registry, Identifier id, Object entry) {
        PolymerItemGroupUtils.registerPolymerItemGroup(id, (ItemGroup) entry);
        return entry;
    }
}
