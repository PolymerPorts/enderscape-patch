package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.model.generic.BlockStateModelManager;
import net.bunten.enderscape.block.DriftJellyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(DriftJellyBlock.class)
public class DriftJellyBlockMixin {
    @Inject(method = "onSteppedOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER))
    private void fixBounce(World level, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity player) {
            player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(0,
                    new PlayerPosition(Vec3d.ZERO, new Vec3d(0, entity.getVelocity().y, 0), 0, 0),
                    Set.of(PositionFlag.DELTA_X, PositionFlag.DELTA_Z, PositionFlag.X, PositionFlag.Y, PositionFlag.Z, PositionFlag.X_ROT, PositionFlag.Y_ROT)
            ));


            Vec3d vec3 = pos.toCenterPos().add(0.0, 0.75, 0.0);
            var option = BlockStateModelManager.getParticle(state);

            for(int i = 0; i < 20; ++i) {
                double x = vec3.x;
                double y = vec3.y;
                double z = vec3.z;
                double xd = level.getRandom().nextGaussian() * 0.05000000074505806;
                double yd = level.getRandom().nextGaussian() * 0.05000000074505806;
                double zd = level.getRandom().nextGaussian() * 0.05000000074505806;
                player.networkHandler.sendPacket(new ParticleS2CPacket(option, false, false, x, y, z, (float) xd, (float) yd, (float) zd, 1, 0));
                level.addParticleClient(option, x, y, z, xd, yd, zd);
            }
        }
    }
}
