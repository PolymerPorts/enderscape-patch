package eu.pb4.enderscapepatch.impl.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public record CampfireFactoryBlock(boolean tick, BiFunction<BlockState, BlockPos, BlockModel> modelFunction) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final CampfireFactoryBlock INSTANCE = new CampfireFactoryBlock(true, Model::new);

    private static final BlockState REGULAR = PolymerBlockResourceUtils.requestEmpty(BlockModelType.CAMPFIRE);
    private static final BlockState REGULAR_WATERLOGGED = PolymerBlockResourceUtils.requestEmpty(BlockModelType.CAMPFIRE_WATERLOGGED);

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return state.getValue(CampfireBlock.WATERLOGGED) ? REGULAR_WATERLOGGED : REGULAR;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, pos);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.tick;
    }

    public CampfireFactoryBlock withModel(BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        return new CampfireFactoryBlock(this.tick, modelFunction);
    }

    public CampfireFactoryBlock withTick(boolean tick) {
        return new CampfireFactoryBlock(tick, this.modelFunction);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }

    public static class Model extends BlockStateModel {
        private final RandomSource randomSource = RandomSource.create();
        public Model(BlockState state, BlockPos pos) {
            super(state, pos, 100.0F);
        }

        @Override
        protected void onTick() {
            var state = this.blockState();
            var blockPos = this.blockPos();

            if (state.getValue(CampfireBlock.LIT)) {
                if (randomSource.nextFloat() < 0.11F) {
                    var smoke = state.getValue(CampfireBlock.SIGNAL_FIRE) ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;

                    for(int i = 0; i < randomSource.nextInt(2) + 2; ++i) {
                        addParticle(smoke, true, (double)blockPos.getX() + (double)0.5F + randomSource.nextDouble() / (double)3.0F * (double)(randomSource.nextBoolean() ? 1 : -1), (double)blockPos.getY() + randomSource.nextDouble() + randomSource.nextDouble(), (double)blockPos.getZ() + (double)0.5F + randomSource.nextDouble() / (double)3.0F * (double)(randomSource.nextBoolean() ? 1 : -1), (double)0.0F, 0.07, (double)0.0F);
                    }
                }
                if (randomSource.nextInt(64) == 0) {
                    if (randomSource.nextInt(10) == 0) {
                        playLocalSound((double) blockPos.getX() + (double) 0.5F, (double) blockPos.getY() + (double) 0.5F, (double) blockPos.getZ() + (double) 0.5F, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + randomSource.nextFloat(), randomSource.nextFloat() * 0.7F + 0.6F, false);
                    }

                    if (randomSource.nextInt(5) == 0) {
                        for (int i = 0; i < randomSource.nextInt(1) + 1; ++i) {
                            addParticle(ParticleTypes.LAVA, false, (double) blockPos.getX() + (double) 0.5F, (double) blockPos.getY() + (double) 0.5F, (double) blockPos.getZ() + (double) 0.5F, (double) (randomSource.nextFloat() / 2.0F), 5.0E-5, (double) (randomSource.nextFloat() / 2.0F));
                        }
                    }
                }
            }

            super.onTick();
        }

        private void addParticle(ParticleOptions type, boolean alwaysVisible, double x, double y, double z, double dx, double dy, double dz) {
            if (this.getAttachment() != null) {
                this.getAttachment().getWorld().sendParticles(type, false, alwaysVisible, x, y, z, 0, dx, dy, dz, 1);
            }
        }

        private void playLocalSound(double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, boolean force) {
            if (this.getAttachment() != null) {
                this.getAttachment().getWorld().playSound(null, x, y, z, soundEvent, soundSource, volume, pitch);
            }
        }
    }
}
