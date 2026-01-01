package eu.pb4.enderscapepatch.impl.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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
        return state.getValue(LanternBlock.HANGING)
                ? (state.getValue(LanternBlock.WATERLOGGED) ? HANGING_WATERLOGGED : HANGING)
                : (state.getValue(LanternBlock.WATERLOGGED) ? REGULAR_WATERLOGGED : REGULAR);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, pos);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.tick;
    }

    public LanternFactoryBlock withModel(BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        return new LanternFactoryBlock(this.tick, modelFunction);
    }

    public LanternFactoryBlock withTick(boolean tick) {
        return new LanternFactoryBlock(tick, this.modelFunction);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }
}
