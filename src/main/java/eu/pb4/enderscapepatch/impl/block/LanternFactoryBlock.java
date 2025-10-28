package eu.pb4.enderscapepatch.impl.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.EnumMap;
import java.util.function.BiFunction;

public record LanternFactoryBlock(boolean tick, BiFunction<BlockState, BlockPos, BlockModel> modelFunction) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final LanternFactoryBlock INSTANCE = new LanternFactoryBlock(false,  BlockStateModel::longRange);

    private static final BlockState REGULAR = PolymerBlockResourceUtils.requestEmpty(BlockModelType.LANTERN);
    private static final BlockState REGULAR_WATERLOGGED = PolymerBlockResourceUtils.requestEmpty(BlockModelType.LANTERN_WATERLOGGED);
    private static final BlockState HANGING = PolymerBlockResourceUtils.requestEmpty(BlockModelType.LANTERN_HANGING);
    private static final BlockState HANGING_WATERLOGGED = PolymerBlockResourceUtils.requestEmpty(BlockModelType.LANTERN_HANGING_WATERLOGGED);

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return state.get(LanternBlock.HANGING)
                ? (state.get(LanternBlock.WATERLOGGED) ? HANGING_WATERLOGGED : HANGING)
                : (state.get(LanternBlock.WATERLOGGED) ? REGULAR_WATERLOGGED : REGULAR);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, pos);
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.tick;
    }

    public LanternFactoryBlock withModel(BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        return new LanternFactoryBlock(this.tick, modelFunction);
    }

    public LanternFactoryBlock withTick(boolean tick) {
        return new LanternFactoryBlock(tick, this.modelFunction);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return true;
    }
}
