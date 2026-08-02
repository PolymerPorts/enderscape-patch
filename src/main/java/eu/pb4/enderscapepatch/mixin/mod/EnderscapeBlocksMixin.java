package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import eu.pb4.enderscapepatch.impl.block.*;
import eu.pb4.enderscapepatch.mixin.AbstractOvergrowthBlockAccessor;
import eu.pb4.enderscapepatch.mixin.DirectionSetAccessor;
import eu.pb4.factorytools.api.block.model.SignModel;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(EnderscapeBlocks.class)
public class EnderscapeBlocksMixin {
    @Inject(method = "registerWithItem", at = @At("TAIL"))
    private static void polymerify1(BlockItemId id, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties, CallbackInfoReturnable<Block> cir) {
        polymerify(id.block().identifier().getPath(), cir.getReturnValue());
    }


    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify2(ResourceKey<Block> key, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties, CallbackInfoReturnable<Block> cir) {
        polymerify(key.identifier().getPath(), cir.getReturnValue());
    }

    @Unique
    private static void polymerify(String path, Block block) {
        PolymerBlock overlay = null;

        if (path.contains("_path")) {
            overlay = BaseFactoryBlock.BARRIER;
        } else if (block instanceof CampfireBlock) {
            overlay = CampfireFactoryBlock.INSTANCE;
        } else if (block instanceof WallTorchBlock) {
            overlay = BaseFactoryBlock.SAPLING.withTick(true).withModel(TorchModel.Wall::new);
        } else if (block instanceof TorchBlock) {
            overlay = BaseFactoryBlock.SAPLING.withTick(true).withModel(TorchModel::new);
        } else if (block instanceof ChainBlock) {
            overlay = ChainFactoryBlock.INSTANCE;
        } else if (block instanceof IronBarsBlock) {
            overlay = MapPolymerBlock.ofPaneBlock(block);
        } else if (block instanceof ChorusCakeRollBlock) {
            overlay = BaseFactoryBlock.CAMPFIRE;
        } else if (block instanceof VeiledVinesBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.VINES, BaseFactoryBlock.VINE, x -> x.getValue(VeiledVinesBlock.FACING) == Direction.UP);
        } else if (block instanceof PuruberryVine || block instanceof BlinklightVinesBodyBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.VINES);
        } else if (block instanceof AbstractOvergrowthBlockAccessor block1) {
            overlay = StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK, BaseFactoryBlock.BARRIER, x -> block1.getProperties().supports(x.getValue(AbstractOvergrowthBlock.FACING)));
        } else if (block instanceof DirectionalVegetationBlock block1 && ((DirectionSetAccessor) (Object) block1.directionSet).getList().size() == 1
                && ((DirectionSetAccessor) (Object) block1.directionSet).getList().getFirst().getAxis() == Direction.Axis.Y) {
            overlay = StatePolymerBlock.of(block, BlockModelType.TRIPWIRE_FLAT, BaseFactoryBlock.SAPLING, x -> block1.directionSet.supports(x.getValue(DirectionalVegetationBlock.FACING)));
        } else if (block instanceof CorruptGrowthBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.TRIPWIRE, BaseFactoryBlock.SAPLING,
                    x -> x.getValue(CorruptGrowthBlock.FACING).getAxis() == Direction.Axis.Y);
        } else if (block instanceof VeiledLeafPileBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.TRIPWIRE, BaseFactoryBlock.SAPLING, x -> x.getValue(VeiledLeafPileBlock.LAYERS) < 3);
        } else if (block instanceof MurublightBracketBlock) {
            overlay = BaseFactoryBlock.SAPLING_SHORT;
        } else if (block instanceof MagniaSproutBlock) {
            overlay = WaterloggedFactoryBlock.BARRIER;
        } else if (block instanceof SlabBlock) {
            overlay = SlabFactoryBlock.INSTANCE;
        } else if (block instanceof StairBlock) {
            overlay = StateCopyFactoryBlock.STAIR;
        } else if (block instanceof WallBlock) {
            overlay = StateCopyFactoryBlock.WALL;
        } else if (block instanceof TrapDoorBlock) {
            overlay = TrapdoorPolymerBlock.INSTANCE;
        } else if (block instanceof DoorBlock) {
            overlay = DoorPolymerBlock.INSTANCE;
        } else if (block instanceof FenceBlock) {
            overlay = StateCopyFactoryBlock.FENCE;
        } else if (block instanceof FenceGateBlock) {
            overlay = StateCopyFactoryBlock.FENCE_GATE;
        } else if (block instanceof PressurePlateBlock) {
            overlay = StateCopyFactoryBlock.PRESSURE_PLATE;
        } else if (block instanceof ButtonBlock) {
            overlay = StateCopyFactoryBlock.BUTTON;
        } else if (block instanceof StandingSignBlock) {
            overlay = StateCopyFactoryBlock.SIGN;
        } else if (block instanceof WallSignBlock) {
            overlay = StateCopyFactoryBlock.WALL_SIGN;
        } else if (block instanceof CeilingHangingSignBlock) {
            overlay = StateCopyFactoryBlock.HANGING_SIGN;
        } else if (block instanceof WallHangingSignBlock) {
            overlay = StateCopyFactoryBlock.HANGING_WALL_SIGN;
        } else if (block instanceof ShelfBlock) {
            overlay = ShelfFactoryBlock.INSTANCE;
        } else if (block instanceof LanternBlock) {
            overlay = LanternFactoryBlock.INSTANCE;
        } else if (block instanceof WispSproutsBlock) {
            overlay = RealSingleStatePolymerBlock.of(block, BlockModelType.TRIPWIRE);
        } else if (block instanceof VeiledLeavesBlock) {
            overlay = RealSingleStatePolymerBlock.of(block, BlockModelType.LEAVES);
        } else if (!(block instanceof EntityBlock) && !path.equals("drift_jelly_block") && block.defaultBlockState().isCollisionShapeFullBlock(PolymerCommonUtils.getFakeWorld(), BlockPos.ZERO)) {
            overlay = StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        } else if (block instanceof LiquidBlock) {
            overlay = BaseFactoryBlock.AIR.withModel(VoidLachrymaModel::new);
        }

        if (overlay == null) {
            if (block.defaultBlockState().getCollisionShape(PolymerCommonUtils.getFakeWorld(), BlockPos.ZERO).isEmpty()) {
                overlay = BaseFactoryBlock.SAPLING;
            } else {
                overlay = BaseFactoryBlock.BARRIER;
            }
        }


        EnderscapePolymerPatch.LATE_INIT.add(() -> BlockStateModelManager.addBlock(BuiltInRegistries.BLOCK.getKey(block), block));
        PolymerBlock.registerOverlay(block, overlay);
        if (overlay instanceof BlockWithElementHolder blockWithElementHolder) {
            BlockWithElementHolder.registerOverlay(block, blockWithElementHolder);
        }
    }
}
