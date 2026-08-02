package eu.pb4.enderscapepatch.mixin.mod;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import net.penumbra.enderscape.manager.LowGravityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(LowGravityManager.class)
public class LowGravityManagerMixin {
    @Inject(method = "tickTail", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", shift = At.Shift.AFTER))
    private static void forcePhysicsOnClient(LivingEntity entity, CallbackInfo ci, @Local(name = "frictionMod") double frictionMod) {
        if (entity instanceof ServerPlayer player) {
            var mov = player.getKnownMovement();
            player.connection.send(new ClientboundPlayerPositionPacket(0,
                    new PositionMoveRotation(Vec3.ZERO, new Vec3(mov.x() / frictionMod - mov.x(), 0, mov.z() / frictionMod - mov.z()), 0, 0),
                    Set.of(Relative.DELTA_X, Relative.DELTA_Y, Relative.DELTA_Z, Relative.X, Relative.Y, Relative.Z, Relative.X_ROT, Relative.Y_ROT)
            ));
        }
    }
}
