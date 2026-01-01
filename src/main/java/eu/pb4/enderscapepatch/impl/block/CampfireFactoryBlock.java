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
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.BiFunction;

public record CampfireFactoryBlock(boolean tick, BiFunction<BlockState, BlockPos, BlockModel> modelFunction) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final CampfireFactoryBlock INSTANCE = new CampfireFactoryBlock(false,  BlockStateModel::longRange);

    private static final BlockState REGULAR = PolymerBlockResourceUtils.requestEmpty(BlockModelType.CAMPFIRE);
    private static final BlockState REGULAR_WATERLOGGED = PolymerBlockResourceUtils.requestEmpty(BlockModelType.CAMPFIRE_WATERLOGGED);

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return state.get(CampfireBlock.WATERLOGGED) ? REGULAR_WATERLOGGED : REGULAR;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, pos);
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.tick;
    }

    public CampfireFactoryBlock withModel(BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        return new CampfireFactoryBlock(this.tick, modelFunction);
    }

    public CampfireFactoryBlock withTick(boolean tick) {
        return new CampfireFactoryBlock(tick, this.modelFunction);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return true;
    }
}
