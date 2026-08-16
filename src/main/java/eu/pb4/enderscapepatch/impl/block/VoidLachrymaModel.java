package eu.pb4.enderscapepatch.impl.block;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import eu.pb4.factorytools.api.util.LazyItemStack;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.stream.IntStream;

public class VoidLachrymaModel extends BlockModel {
    public static final LazyItemStack[] MODELS = IntStream.rangeClosed(0, BlockStateProperties.MAX_LEVEL_15)
            .mapToObj(x -> ItemDisplayElementUtil.getModel(EnderscapePolymerPatch.id("block/fluid/void_lachryma/" + x))).toArray(LazyItemStack[]::new);

    private final ItemDisplayElement model;

    public VoidLachrymaModel(BlockState initialState, BlockPos pos) {
        this.model = ItemDisplayElementUtil.createSimple();
        this.updateState(initialState);
        this.addElement(this.model);
    }

    @Override
    public void notifyUpdate(HolderAttachment.UpdateType updateType) {
        if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
            this.updateState(this.blockState());
            this.model.tick();
        }
    }

    private void updateState(BlockState initialState) {
        this.model.setItem(MODELS[initialState.getValue(LiquidBlock.LEVEL)].get());
    }
}
