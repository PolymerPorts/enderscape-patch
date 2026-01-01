package eu.pb4.enderscapepatch.impl.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.BiFunction;

public record WaterloggedFactoryBlock(BlockState clientState, BlockState clientStateWaterlogged, boolean tick, BiFunction<BlockState, BlockPos, BlockModel> modelFunction) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final WaterloggedFactoryBlock BARRIER = new WaterloggedFactoryBlock(Blocks.BARRIER.defaultBlockState(),
            Blocks.BARRIER.defaultBlockState().setValue(BarrierBlock.WATERLOGGED, true), false, BlockStateModel::longRange);

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? clientStateWaterlogged : clientState;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, pos);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.tick;
    }

    public WaterloggedFactoryBlock withModel(BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        return new WaterloggedFactoryBlock(this.clientState, this.clientStateWaterlogged, this.tick, modelFunction);
    }

    public WaterloggedFactoryBlock withTick(boolean tick) {
        return new WaterloggedFactoryBlock(this.clientState, this.clientStateWaterlogged, tick, this.modelFunction);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }
}
