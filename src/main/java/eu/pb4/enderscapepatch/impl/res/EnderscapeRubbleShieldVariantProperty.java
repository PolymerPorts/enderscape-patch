package eu.pb4.enderscapepatch.impl.res;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.SelectProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public record EnderscapeRubbleShieldVariantProperty() implements SelectProperty<Identifier> {
    public static final Type<EnderscapeRubbleShieldVariantProperty, Identifier> TYPE = new Type<>(MapCodec.unit(EnderscapeRubbleShieldVariantProperty::new), Identifier.CODEC);
    @Override
    public Type<? extends SelectProperty<Identifier>, Identifier> type() {
        return TYPE;
    }
}
