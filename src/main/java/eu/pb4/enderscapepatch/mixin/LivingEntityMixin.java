//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.pb4.enderscapepatch.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z", ordinal = 2))
    private boolean serverWorldIsJustAsGoodAsClientOne(boolean original) {
       return original || PolymerEntity.get(this) instanceof BasePolymerEntity;
   }
}
