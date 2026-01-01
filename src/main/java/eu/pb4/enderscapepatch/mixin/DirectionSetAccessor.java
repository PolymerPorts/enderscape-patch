package eu.pb4.enderscapepatch.mixin;

import net.bunten.enderscape.block.properties.DirectionSet;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(DirectionSet.class)
public interface DirectionSetAccessor {
    @Accessor
    List<Direction> getList();
}
