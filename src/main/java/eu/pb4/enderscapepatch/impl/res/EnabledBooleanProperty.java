package eu.pb4.enderscapepatch.impl.res;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.BooleanProperty;
import net.bunten.enderscape.item.ItemStackContext;
import net.bunten.enderscape.item.component.Enabled;
import net.bunten.enderscape.item.component.FueledTool;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record EnabledBooleanProperty() implements BooleanProperty {
    public static final MapCodec<EnabledBooleanProperty> MAP_CODEC = MapCodec.unit(new EnabledBooleanProperty());

    public static boolean test(ItemStack stack, @Nullable Level level, @Nullable LivingEntity living) {
        return Enabled.get(stack) && FueledTool.fuelExceedsCost(new ItemStackContext(stack, level, living));
    }

    @Override
    public MapCodec<? extends BooleanProperty> codec() {
        return CODEC;
    }
}
