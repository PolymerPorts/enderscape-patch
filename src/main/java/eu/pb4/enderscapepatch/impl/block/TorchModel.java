package eu.pb4.enderscapepatch.impl.block;

import eu.pb4.enderscapepatch.mixin.TorchBlockAccessor;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TorchModel extends BlockStateModel {
    final RandomSource randomSource = RandomSource.create();
    final ParticleOptions particle;

    public TorchModel(BlockState state, BlockPos pos) {
        super(state, pos, 100.0F);
        this.particle = new DustParticleOptions(0xf94aff, 2f);//((TorchBlockAccessor) state.getBlock()).getFlameParticle();
    }

    @Override
    protected void onTick() {
        var blockPos = this.blockPos();

        if (this.randomSource.nextInt(16) == 0) {
            double x = (double) blockPos.getX() + (double) 0.5F;
            double y = (double) blockPos.getY() + 0.7;
            double z = (double) blockPos.getZ() + (double) 0.5F;
            addParticle(ParticleTypes.SMOKE, false, x, y, z, 0.0F, 0.0F, 0.0F);
            addParticle(this.particle, false, x, y, z, 0.0F, 0.0F, 0.0F);
        }
    }

    protected void addParticle(ParticleOptions type, boolean alwaysVisible, double x, double y, double z, double dx, double dy, double dz) {
        if (this.getAttachment() != null) {
            this.getAttachment().getWorld().sendParticles(type, false, alwaysVisible, x, y, z, 0, dx, dy, dz, 1);
        }
    }

    public static class Wall extends TorchModel {
        public Wall(BlockState state, BlockPos pos) {
            super(state, pos);
        }

        @Override
        protected void onTick() {
            var blockPos = this.blockPos();
            var blockState = this.blockState();

            if (this.randomSource.nextInt(16) == 0) {
                Direction direction = blockState.getValue(WallTorchBlock.FACING);
                double x = (double) blockPos.getX() + 0.5F;
                double y = (double) blockPos.getY() + 0.7;
                double z = (double) blockPos.getZ() + 0.5F;
                Direction direction2 = direction.getOpposite();
                addParticle(ParticleTypes.SMOKE, false, x + 0.27 * (double) direction2.getStepX(), y + 0.22, z + 0.27 * (double) direction2.getStepZ(), 0.0F, 0.0F, 0.0F);
                addParticle(this.particle, false, x + 0.27 * (double) direction2.getStepX(), y + 0.22, z + 0.27 * (double) direction2.getStepZ(), 0.0F, 0.0F, 0.0F);
            }
        }
    }
}