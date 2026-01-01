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
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.EnumMap;
import java.util.function.BiFunction;

public record ShelfFactoryBlock(boolean tick, BiFunction<BlockState, BlockPos, BlockModel> modelFunction) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final ShelfFactoryBlock INSTANCE = new ShelfFactoryBlock(false,  BlockStateModel::longRange);

    private static final EnumMap<Direction, BlockState> SHELVES = Util.make(new EnumMap<>(Direction.class),
            x -> {
        for (var dir : Direction.Plane.HORIZONTAL) {
            x.put(dir, PolymerBlockResourceUtils.requestEmpty(BlockModelType.getShelf(dir, false)));
        }
            });
    private static final EnumMap<Direction, BlockState> SHELVES_WATERLOGGED = Util.make(new EnumMap<>(Direction.class),
            x -> {
                for (var dir : Direction.Plane.HORIZONTAL) {
                    x.put(dir, PolymerBlockResourceUtils.requestEmpty(BlockModelType.getShelf(dir, true)));
                }
            });

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return (state.getValue(ShelfBlock.WATERLOGGED) ? SHELVES_WATERLOGGED : SHELVES).get(state.getValue(ShelfBlock.FACING));
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, pos);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.tick;
    }

    public ShelfFactoryBlock withModel(BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        return new ShelfFactoryBlock(this.tick, modelFunction);
    }

    public ShelfFactoryBlock withTick(boolean tick) {
        return new ShelfFactoryBlock(tick, this.modelFunction);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }
}
