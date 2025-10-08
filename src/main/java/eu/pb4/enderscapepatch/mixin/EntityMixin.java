package eu.pb4.enderscapepatch.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPosition;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(Entity.class)
public class EntityMixin {
    @WrapOperation(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void syncPushign(Entity instance, double deltaX, double deltaY, double deltaZ, Operation<Void> original, Entity otherEntity) {
        original.call(instance, deltaX, deltaY, deltaZ);
        if (instance instanceof ServerPlayerEntity player
                && (PolymerEntity.get((Entity) (Object) this) instanceof BasePolymerEntity || PolymerEntity.get(otherEntity) instanceof BasePolymerEntity) ) {
            player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(0,
                    new EntityPosition(Vec3d.ZERO, new Vec3d(deltaX, deltaY, deltaZ), 0, 0),
                    Set.of(PositionFlag.DELTA_X, PositionFlag.DELTA_Y, PositionFlag.DELTA_Z, PositionFlag.X, PositionFlag.Y, PositionFlag.Z, PositionFlag.X_ROT, PositionFlag.Y_ROT)
            ));
        }
    }
}
