package eu.pb4.enderscapepatch.mixin.mod;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.bunten.enderscape.registry.EnderscapeRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(EnderscapeRegistries.class)
public class EnderscapeRegistriesMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/registry/DynamicRegistries;registerSynced(Lnet/minecraft/registry/RegistryKey;Lcom/mojang/serialization/Codec;[Lnet/fabricmc/fabric/api/event/registry/DynamicRegistries$SyncOption;)V"))
    private static void safeRegister(RegistryKey<? extends Registry<Object>> key, Codec<Object> codec, DynamicRegistries.SyncOption[] options) {
        DynamicRegistries.register(key, codec);
        PolymerUtils.markAsServerOnlyRegistry(key);
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/registry/DynamicRegistries;registerSynced(Lnet/minecraft/registry/RegistryKey;Lcom/mojang/serialization/Codec;Lcom/mojang/serialization/Codec;[Lnet/fabricmc/fabric/api/event/registry/DynamicRegistries$SyncOption;)V"))
    private static void safeRegister2(RegistryKey<? extends Registry<Object>> key, Codec<Object> codec, Codec<Object> codec2, DynamicRegistries.SyncOption[] options) {
        DynamicRegistries.register(key, codec);
        PolymerUtils.markAsServerOnlyRegistry(key);
    }
}
