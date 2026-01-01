package eu.pb4.enderscapepatch.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.enderscapepatch.impl.entity.BasePolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public class EntityMixin {
    @WrapOperation(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"))
    private void syncPushign(Entity instance, double deltaX, double deltaY, double deltaZ, Operation<Void> original, Entity otherEntity) {
        original.call(instance, deltaX, deltaY, deltaZ);
        if (instance instanceof ServerPlayer player
                && (PolymerEntity.get((Entity) (Object) this) instanceof BasePolymerEntity || PolymerEntity.get(otherEntity) instanceof BasePolymerEntity) ) {
            player.connection.send(new ClientboundPlayerPositionPacket(0,
                    new PositionMoveRotation(Vec3.ZERO, new Vec3(deltaX, deltaY, deltaZ), 0, 0),
                    Set.of(Relative.DELTA_X, Relative.DELTA_Y, Relative.DELTA_Z, Relative.X, Relative.Y, Relative.Z, Relative.X_ROT, Relative.Y_ROT)
            ));
        }
    }
}
