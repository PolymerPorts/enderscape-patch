package eu.pb4.enderscapepatch.mixin.mod.client;

import com.google.common.collect.Multiset;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.property.select.SelectProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(SelectProperty.Type.class)
public class SelectPropertyTypeMixin {
    @WrapOperation(method = "validateCases", at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I"))
    private static int replaceSize(Set instance, Operation<Integer> original, @Local Multiset multiset) {
        return multiset.size();
    }
}