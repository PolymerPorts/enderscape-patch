package eu.pb4.enderscapepatch.mixin;

import net.penumbra.enderscape.block.AbstractOvergrowthBlock;
import net.penumbra.enderscape.block.properties.DirectionSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractOvergrowthBlock.class)
public interface AbstractOvergrowthBlockAccessor {
    @Accessor
    DirectionSet getProperties();
}
