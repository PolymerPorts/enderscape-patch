package eu.pb4.enderscapepatch.mixin;

import net.bunten.enderscape.block.AbstractOvergrowthBlock;
import net.bunten.enderscape.block.properties.DirectionProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractOvergrowthBlock.class)
public interface AbstractOvergrowthBlockAccessor {
    @Accessor
    DirectionProperties getProperties();
}
