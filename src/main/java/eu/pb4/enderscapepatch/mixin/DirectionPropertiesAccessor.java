package eu.pb4.enderscapepatch.mixin;

import net.bunten.enderscape.block.properties.DirectionProperties;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(DirectionProperties.class)
public interface DirectionPropertiesAccessor {
    @Accessor
    List<Direction> getList();
}
