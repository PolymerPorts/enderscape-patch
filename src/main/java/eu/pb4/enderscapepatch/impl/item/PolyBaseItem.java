package eu.pb4.enderscapepatch.impl.item;

import eu.pb4.enderscapepatch.impl.res.EnabledBooleanProperty;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.penumbra.enderscape.registry.component.EnderscapeDataComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record PolyBaseItem(Item item) implements PolymerItem {
    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return item instanceof ShieldItem ? Items.SHIELD : Items.TRIAL_KEY;
    }

    @Override
    public boolean isPolymerBlockInteraction(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult, InteractionResult actionResult) {
        return actionResult.consumesAction();
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        var bools = new BooleanArrayList();
        var strings = new ArrayList<String>();

        if (stack.has(EnderscapeDataComponents.ENABLED)) {
            var player = PolymerCommonUtils.getPlayer(context);

            bools.add(EnabledBooleanProperty.test(stack, player != null ? player.level() : null, player));
        }

        if (stack.has(EnderscapeDataComponents.ENABLED) || stack.has(EnderscapeDataComponents.FUELED_TOOL)) {
            out.set(DataComponents.MAX_DAMAGE, 13);
            out.set(DataComponents.DAMAGE, 13 - stack.getBarWidth());
        }

        if (stack.has(EnderscapeDataComponents.DYE_COLOR)) {
            out.set(DataComponents.DYE, stack.get(EnderscapeDataComponents.DYE_COLOR));
        }

        if (stack.has(EnderscapeDataComponents.RUBBLE_SHIELD_VARIANT)) {
            strings.add(Objects.requireNonNull(stack.get(EnderscapeDataComponents.RUBBLE_SHIELD_VARIANT)).toString());
        }

        if (!bools.isEmpty() || !strings.isEmpty()) {
            out.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), bools, strings, List.of()));
        }
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return item instanceof BlockItem;
    }
}
