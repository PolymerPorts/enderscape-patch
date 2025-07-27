package eu.pb4.enderscapepatch.impl.res;


import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.resourcepack.ModelModifiers;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ConditionItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.EmptyItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.BooleanProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.CustomModelDataFlagProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelElement;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.bunten.enderscape.Enderscape;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch.id;

public class ResourcePackGenerator {
    private static final Set<String> EXPANDABLE = Set.of("wall", "fence", "slab", "stairs", "pressure_plate", "button");

    public static void setup() {
        BooleanProperty.TYPES.put(Enderscape.id("enabled"), EnabledBooleanProperty.MAP_CODEC);
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(ResourcePackGenerator::build);
    }

    private static void build(ResourcePackBuilder builder) {
        final var expansion = new Vec3d(0.08, 0.08, 0.08);
        var atlas = AtlasAsset.builder();

        for (var model : EntityModels.ALL) {
            model.generateAssets(builder::addData, atlas);
        }

        try {
            var main = ImageIO.read(new ByteArrayInputStream(Objects.requireNonNull(builder.getData("assets/enderscape/textures/entity/drifter/drifter.png"))));
            var jelly = ImageIO.read(new ByteArrayInputStream(Objects.requireNonNull(builder.getData("assets/enderscape/textures/entity/drifter/jelly.png"))));
            var out = new BufferedImage(main.getWidth(), main.getHeight(), BufferedImage.TYPE_INT_ARGB);

            for (int x = 0; x < out.getWidth(); x++) {
                for (int y = 0; y < out.getHeight(); y++) {
                    int color = main.getRGB(x, y);
                    int jellyColor = jelly.getRGB(x, y);
                    out.setRGB(x, y, jellyColor != 0 ? jellyColor : color);
                }
            }
            var b = new ByteArrayOutputStream();

            ImageIO.write(out, "png", b);

            builder.addData("assets/enderscape/textures/entity/drifter/drifter_with_jelly.png", b.toByteArray());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        builder.forEachFile(((string, bytes) -> {
            for (var expandable : EXPANDABLE) {
                if (string.contains(expandable) && string.startsWith("assets/enderscape/models/block/")) {
                    var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));

                        builder.addData(AssetPaths.model("enderscape-patch", parentId.getPath()) + ".json", new ModelAsset(parentAsset.parent(), parentAsset.elements().map(x -> x.stream()
                                .map(element -> new ModelElement(element.from().subtract(expansion), element.to().add(expansion),
                                        element.faces(), element.rotation(), element.shade(), element.lightEmission())
                                ).toList()), parentAsset.textures(), parentAsset.display(), parentAsset.guiLight(), parentAsset.ambientOcclusion()).toBytes());
                    }
                }
            }
        }));

        for (var entry : BlockStateModelManager.UV_LOCKED_MODELS.get("enderscape").entrySet()) {
            var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().contains(expandable) && entry.getKey().startsWith("block/")) ? expansion : Vec3d.ZERO;
            for (var v : entry.getValue()) {
                var suffix = "_uvlock_" + v.x() + "_" + v.y();
                var modelId = v.model().withSuffixedPath(suffix);
                var asset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getData(AssetPaths.model(v.model()) + ".json")), StandardCharsets.UTF_8));

                if (asset.parent().isPresent()) {
                    var parentId = asset.parent().get();
                    var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                    builder.addData(AssetPaths.model("enderscape-patch", parentId.getPath() + suffix) + ".json",
                            ModelModifiers.expandModelAndRotateUVLocked(parentAsset, expand, v.x(), v.y()));
                    builder.addData(AssetPaths.model(modelId) + ".json",
                            new ModelAsset(Optional.of(Identifier.of("enderscape-patch", parentId.getPath() + suffix)), asset.elements(),
                                    asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                }
            }
        }

        builder.addWriteConverter(((string, bytes) -> {
            if (!string.contains("_uvlock_")) {
                for (var expandable : EXPANDABLE) {
                    if (string.contains(expandable) && string.startsWith("assets/enderscape/models/block/")) {
                        var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                        return new ModelAsset(asset.parent().map(x -> id(x.getPath())), asset.elements(), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes();
                    }
                }
            }

            if (string.equals("assets/enderscape/items/magnia_attractor.json")) {
                var asset = ItemAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                var replacer = new ItemModel.Replacer[]{null};
                replacer[0] = (parent, model) -> {
                    if (model instanceof ConditionItemModel conditionItemModel && conditionItemModel.property() instanceof EnabledBooleanProperty) {
                        return new ConditionItemModel(new CustomModelDataFlagProperty(0),
                                replacer[0].modifyDeep(model, conditionItemModel.onTrue()),
                                replacer[0].modifyDeep(model, conditionItemModel.onFalse())
                        );
                    }
                    return model;
                };

                return new ItemAsset(replacer[0].modifyDeep(EmptyItemModel.INSTANCE, asset.model()), asset.properties()).toBytes();
            }

            return bytes;
        }));


        ModelModifiers.createSignModel(builder, "enderscape", "veiled", atlas);
        ModelModifiers.createSignModel(builder, "enderscape","celestial", atlas);
        ModelModifiers.createSignModel(builder, "enderscape","murublight", atlas);

        builder.addData("assets/minecraft/atlases/blocks.json", atlas.build());
    }
}