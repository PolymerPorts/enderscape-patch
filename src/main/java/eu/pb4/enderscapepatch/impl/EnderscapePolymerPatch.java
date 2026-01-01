package eu.pb4.enderscapepatch.impl;

import eu.pb4.enderscapepatch.impl.mixson.MixsonPatcher;
import eu.pb4.enderscapepatch.impl.res.ResourcePackGenerator;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.MapColorTintSource;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.impl.HolderHolder;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class EnderscapePolymerPatch implements ModInitializer {
    public static final String MOD_ID = "enderscape-polymer-patch";
    public static final int FAKE_TRACKER_INDEX = -474654;
    public static final Logger LOGGER = LoggerFactory.getLogger("enderscape-polymer-patch");
    public static final List<Runnable> LATE_INIT = new ArrayList<>();


    @Override
    public void onInitialize() {
        MixinEnvironment.getCurrentEnvironment().audit();
        PolymerResourcePackUtils.addModAssets("enderscape");
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        ResourcePackExtras.forDefault().addBridgedModelsFolder(
                Identifier.of("enderscape", "block"),
                Identifier.of("enderscape", "block_sign")
        );
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of("enderscape", "entity"), (id, b) -> {
            return new ItemAsset(new BasicItemModel(id, List.of(new MapColorTintSource(0xFFFFFF))), new ItemAsset.Properties(true, true));
        });

        ResourcePackGenerator.setup();
        MixsonPatcher.setup();

        PolymerItemUtils.syncDefaultComponent(Items.SHULKER_SHELL, DataComponentTypes.EQUIPPABLE);

        //SoundPatcher.convertAllVanillaBlockSoundsIntoServerSounds();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register((dispatcher, a, b) -> {
                dispatcher.register(literal("count_displays").executes(ctx -> {
                    var player = ctx.getSource().getPlayerOrThrow();
                    var map = new Reference2IntOpenHashMap<Block>();
                    for (var holder : ((HolderHolder) player.networkHandler).polymer$getHolders()) {
                        if (holder.getAttachment() instanceof BlockAwareAttachment attachment) {
                            map.put(attachment.getBlockState().getBlock(), map.getInt(attachment.getBlockState().getBlock()) + 1);
                        }
                    }
                    var entries = new ArrayList<>(map.reference2IntEntrySet());
                    entries.sort(Comparator.comparing(Reference2IntMap.Entry::getIntValue));
                    for (var entry : entries.reversed()) {
                        player.sendMessage(Text.literal(Registries.BLOCK.getId(entry.getKey()).toString() + " -> " + entry.getIntValue()));
                    }

                    return 0;
                }));
                dispatcher.register(literal("count_model_types").executes(ctx -> {
                    for (var entry : BlockModelType.values()) {
                        ctx.getSource().sendMessage(Text.literal(entry.name() + " -> " + PolymerBlockResourceUtils.getBlocksLeft(entry)));
                    }

                    return 0;
                }));
            });
        }
    }

    public static Identifier id(String path) {
        return Identifier.of("enderscape-patch", path);
    }
}