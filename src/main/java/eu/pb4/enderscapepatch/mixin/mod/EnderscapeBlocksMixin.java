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
import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.block.*;
import net.bunten.enderscape.registry.EnderscapeBlocks;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(EnderscapeBlocks.class)
public class EnderscapeBlocksMixin {
    @Inject(method = "register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/item/Item$Settings;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", at = @At("TAIL"))
    private static void polymerify1(String string, Function<Item.Settings, Item> itemFunction, Item.Settings itemProperties,
                                   Function<AbstractBlock.Settings, Block> function, AbstractBlock.Settings properties, CallbackInfoReturnable<Block> cir) {
        polymerify(string, cir.getReturnValue());
    }


    @Inject(method = "register(ZLjava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", at = @At("TAIL"))
    private static void polymerify2(boolean hasItem, String string, Function<AbstractBlock.Settings, Block> function, AbstractBlock.Settings properties, CallbackInfoReturnable<Block> cir) {
        polymerify(string, cir.getReturnValue());
    }

    @Unique
    private static void polymerify(String path, Block block) {
        EnderscapePolymerPatch.LATE_INIT.add(() -> BlockStateModelManager.addBlock(Registries.BLOCK.getId(block), block));

        PolymerBlock overlay = null;

        if (path.endsWith("nebulite_ore") || path.equals("void_shale")) {
            overlay = StatePolymerBlock.of(block, BlockModelType.TRANSPARENT_BLOCK);
        } else if (path.contains("_path")) {
            overlay = BaseFactoryBlock.BARRIER;
        }else if (block instanceof VeiledVinesBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.VINES_BLOCK, BaseFactoryBlock.VINE, x -> x.get(VeiledVinesBlock.FACING) == Direction.UP);
        } else if (block instanceof FlangerBerryVine || block instanceof BlinklightVinesBodyBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.VINES_BLOCK);
        } else if (block instanceof AbstractOvergrowthBlockAccessor block1) {
            overlay = StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK, BaseFactoryBlock.BARRIER, x -> block1.getProperties().supports(x.get(AbstractOvergrowthBlock.FACING)));
        } else if (block instanceof DirectionalPlantBlock block1 && ((DirectionSetAccessor) (Object) block1.directionSet).getList().size() == 1
                && ((DirectionSetAccessor) (Object) block1.directionSet).getList().getFirst().getAxis() == Direction.Axis.Y) {
            overlay = StatePolymerBlock.of(block, BlockModelType.TRIPWIRE_BLOCK_FLAT, BaseFactoryBlock.SAPLING, x -> block1.directionSet.supports(x.get(DirectionalPlantBlock.FACING)));
        } else if (block instanceof CorruptGrowthBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.TRIPWIRE_BLOCK, BaseFactoryBlock.SAPLING,
                    x -> x.get(CorruptGrowthBlock.FACING).getAxis() == Direction.Axis.Y);
        } else if (block instanceof VeiledLeafPileBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.TRIPWIRE_BLOCK, BaseFactoryBlock.SAPLING, x -> x.get(VeiledLeafPileBlock.LAYERS) < 3);
        } else if (block instanceof MurublightShelfBlock) {
            overlay = BaseFactoryBlock.SAPLING_SHORT;
        }else if (block instanceof SlabBlock) {
            overlay = SlabFactoryBlock.INSTANCE;
        } else if (block instanceof StairsBlock) {
            overlay = StateCopyFactoryBlock.STAIR;
        } else if (block instanceof WallBlock) {
            overlay = StateCopyFactoryBlock.WALL;
        } else if (block instanceof TrapdoorBlock) {
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
        } else if (block instanceof SignBlock) {
            overlay = StateCopyFactoryBlock.SIGN;
        } else if (block instanceof WallSignBlock) {
            overlay = StateCopyFactoryBlock.WALL_SIGN;
        } else if (block instanceof HangingSignBlock) {
            overlay = StateCopyFactoryBlock.HANGING_SIGN;
        } else if (block instanceof WallHangingSignBlock) {
            overlay = StateCopyFactoryBlock.HANGING_WALL_SIGN;
        } else if (block instanceof WispSproutsBlock) {
            overlay = RealSingleStatePolymerBlock.of(block, BlockModelType.TRIPWIRE_BLOCK);
        } else if (block instanceof VeiledLeavesBlock) {
            overlay = RealSingleStatePolymerBlock.of(block, BlockModelType.TRANSPARENT_BLOCK);
        } else if (!(block instanceof BlockEntityProvider) && !path.equals("drift_jelly_block") && block.getDefaultState().isFullCube(PolymerCommonUtils.getFakeWorld(), BlockPos.ORIGIN)) {
            overlay = StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        }

        if (block instanceof AbstractSignBlock) {
            EnderscapePolymerPatch.LATE_INIT.add(() -> SignModel.setModel(block, Enderscape.id("block_sign/" + path)));
        }

        if (overlay == null) {
            if (block.getDefaultState().getCollisionShape(PolymerCommonUtils.getFakeWorld(), BlockPos.ORIGIN).isEmpty()) {
                overlay = BaseFactoryBlock.SAPLING;
            } else {
                overlay = BaseFactoryBlock.BARRIER;
            }
        }


        PolymerBlock.registerOverlay(block, overlay);
        if (overlay instanceof BlockWithElementHolder blockWithElementHolder) {
            BlockWithElementHolder.registerOverlay(block, blockWithElementHolder);
        }
    }
}
