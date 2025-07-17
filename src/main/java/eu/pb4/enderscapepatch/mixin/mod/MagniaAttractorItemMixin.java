package eu.pb4.enderscapepatch.mixin.mod;

import net.bunten.enderscape.item.MagniaAttractorItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MagniaAttractorItem.class)
public class MagniaAttractorItemMixin {
    @Redirect(method = "tryCyclePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    private void playSoundActually(LivingEntity instance, SoundEvent soundEvent, float v, float p) {
        if (!instance.isSilent()) {
            instance.getWorld().playSound((Entity)null, instance.getX(), instance.getY(), instance.getZ(), soundEvent, instance.getSoundCategory(), v, p);
        }
    }
}
