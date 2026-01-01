package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.bunten.enderscape.registry.EnderscapeAttributes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EnderscapeAttributes.class)
public class EnderscapeAttributesMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String string, Attribute attribute, CallbackInfoReturnable<Holder<Attribute>> cir) {
        PolymerEntityUtils.registerAttribute(cir.getReturnValue());
    }


    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;setSyncable(Z)Lnet/minecraft/world/entity/ai/attributes/Attribute;"))
    private static Attribute untrack(RangedAttribute instance, boolean b) {
        return instance.setSyncable(false);
    }
}
