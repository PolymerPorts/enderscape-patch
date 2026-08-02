package eu.pb4.enderscapepatch.impl.res;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.BooleanProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.SelectProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.penumbra.enderscape.item.ItemStackContext;
import net.penumbra.enderscape.item.component.Enabled;
import net.penumbra.enderscape.item.component.FueledTool;
import org.jetbrains.annotations.Nullable;

public record EnderscapeDyeColorProperty() implements SelectProperty<DyeColor> {
    public static final Type<EnderscapeDyeColorProperty, DyeColor> TYPE = new Type<>(MapCodec.unit(EnderscapeDyeColorProperty::new), DyeColor.CODEC);
    @Override
    public Type<? extends SelectProperty<DyeColor>, DyeColor> type() {
        return TYPE;
    }
}
