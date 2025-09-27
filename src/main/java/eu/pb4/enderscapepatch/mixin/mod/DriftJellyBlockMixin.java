package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import net.bunten.enderscape.block.DriftJellyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(DriftJellyBlock.class)
public abstract class DriftJellyBlockMixin {
    @Shadow protected abstract void playBounceEffects(World level, BlockPos pos);

    @Inject(method = "onEntityLand", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER))
    private void fixBounce(BlockView level, Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity player) {
            player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(0,
                    new PlayerPosition(Vec3d.ZERO, new Vec3d(0, entity.getVelocity().y, 0), 0, 0),
                    Set.of(PositionFlag.DELTA_X, PositionFlag.DELTA_Z, PositionFlag.X, PositionFlag.Y, PositionFlag.Z, PositionFlag.X_ROT, PositionFlag.Y_ROT)
            ));
        }
        if (level instanceof World world) {
            this.playBounceEffects(world, entity.getSteppingPos());
        }
    }

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    protected VoxelShape getCollisionShape(BlockState state, BlockView level, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Redirect(method = "onLandedUpon", at = @At(value = "INVOKE", target = "Lnet/bunten/enderscape/block/DriftJellyBlock;playBounceEffects(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    private void dont(DriftJellyBlock instance, World y, BlockPos z) {}

    @Redirect(method = "playBounceEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z", ordinal = 1))
    private boolean fakeClient(World instance) {
        return true;
    }

    @Redirect(method = "playBounceEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void fixParticles(World instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (instance instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(BlockStateModelManager.getParticle(((BlockStateParticleEffect) parameters).getBlockState()), x, y, z, 0, velocityX, velocityY, velocityZ, 1);
        }
    }
}
