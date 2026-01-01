package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import net.bunten.enderscape.block.DriftJellyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
    @Shadow protected abstract void playBounceEffects(Level level, BlockPos pos);

    @Inject(method = "updateEntityMovementAfterFallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.AFTER))
    private void fixBounce(BlockGetter level, Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundPlayerPositionPacket(0,
                    new PositionMoveRotation(Vec3.ZERO, new Vec3(0, entity.getDeltaMovement().y, 0), 0, 0),
                    Set.of(Relative.DELTA_X, Relative.DELTA_Z, Relative.X, Relative.Y, Relative.Z, Relative.X_ROT, Relative.Y_ROT)
            ));
        }
        if (level instanceof Level world) {
            this.playBounceEffects(world, entity.getOnPos());
        }
    }

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Redirect(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/bunten/enderscape/block/DriftJellyBlock;playBounceEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    private void dont(DriftJellyBlock instance, Level y, BlockPos z) {}

    @Redirect(method = "playBounceEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z", ordinal = 1))
    private boolean fakeClient(Level instance) {
        return true;
    }

    @Redirect(method = "playBounceEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void fixParticles(Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (instance instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(BlockStateModelManager.getParticle(((BlockParticleOption) parameters).getState()), x, y, z, 0, velocityX, velocityY, velocityZ, 1);
        }
    }
}
