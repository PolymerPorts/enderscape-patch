package eu.pb4.enderscapepatch.impl.item;

import eu.pb4.enderscapepatch.impl.res.EnabledBooleanProperty;
import eu.pb4.polymer.core.api.item.PolymerItem;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.TooltipDisplay;

public record ToggableNebuliteToolPolyItem() implements PolymerItem {
    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        out.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(EnabledBooleanProperty.test(stack,
                context.getPlayer() != null ? context.getPlayer().level() : null, context.getPlayer())), List.of(), List.of()));

        out.set(DataComponents.MAX_DAMAGE, 13);
        out.set(DataComponents.DAMAGE, 13 - stack.getBarWidth());

        out.set(DataComponents.TOOLTIP_DISPLAY, out.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT).withHidden(DataComponents.DAMAGE, true));
    }
}
