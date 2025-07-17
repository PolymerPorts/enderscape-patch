package eu.pb4.enderscapepatch.impl.model.generic;

import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;

public class ShiftyBlockStateModel extends BlockStateModel {

    public ShiftyBlockStateModel(BlockState state, float viewRange) {
        super(state, viewRange);
    }

    public static ShiftyBlockStateModel longRange(BlockState state) {
        return new ShiftyBlockStateModel(state, 100);
    }

    public static ShiftyBlockStateModel midRange(BlockState state) {
        return new ShiftyBlockStateModel(state, 3);
    }

    public static ShiftyBlockStateModel shortRange(BlockState state) {
        return new ShiftyBlockStateModel(state, 1.1f);
    }

    @Override
    protected void setupElement(ItemDisplayElement element, int i) {
        var pos = this.blockPos();
        i = Math.abs((i + pos.getX() + pos.getY() + pos.getZ()) % 5);
        element.setOffset(new Vec3d(i / 5000f, i / 5000f, i / 5000f).subtract(5 / 5000f / 2));
    }
}
