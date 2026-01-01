package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.bunten.enderscape.registry.EnderscapeAttributes;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EnderscapeAttributes.class)
public class EnderscapeAttributesMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String string, EntityAttribute attribute, CallbackInfoReturnable<RegistryEntry<EntityAttribute>> cir) {
        PolymerEntityUtils.registerAttribute(cir.getReturnValue());
    }


    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/ClampedEntityAttribute;setTracked(Z)Lnet/minecraft/entity/attribute/EntityAttribute;"))
    private static EntityAttribute untrack(ClampedEntityAttribute instance, boolean b) {
        return instance.setTracked(false);
    }
}
