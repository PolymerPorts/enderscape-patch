package eu.pb4.enderscapepatch.impl.res;


import com.mojang.math.Transformation;
import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.resourcepack.ModelModifiers;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PackResource;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.*;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.BooleanProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.CustomModelDataFlagProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.ComponentSelectProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.CustomModelDataStringProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.SelectProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.special.EndCubeSpecialModel;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelElement;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.penumbra.enderscape.Enderscape;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
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
    private static final Set<String> EXPANDABLE = Set.of("wall", "fence", "slab", "stairs", "pressure_plate", "button", "sign");

    public static void setup() {
        BooleanProperty.TYPES.put(Enderscape.id("enabled"), EnabledBooleanProperty.MAP_CODEC);
        SelectProperty.TYPES.put(Enderscape.id("dye_color"), EnderscapeDyeColorProperty.TYPE);
        SelectProperty.TYPES.put(Enderscape.id("rubble_shield_variant"), EnderscapeRubbleShieldVariantProperty.TYPE);
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(ResourcePackGenerator::build);
    }

    private static void build(ResourcePackBuilder builder) {
        final var expansion = new Vec3(0.08, 0.08, 0.08);
        final var expansionSign = new Vec3(0.04, 0.04, 0.04);
        var atlas = AtlasAsset.builder();

        for (var model : EntityModels.ALL) {
            model.generateAssets(builder::addData, atlas);
        }

        var voidLachrymaTexture = Enderscape.id("block/void_lachryma_still");
        var voidLachrymaTextureSide = Enderscape.id("block/void_lachryma_flow");
        for (int i = 0; i <= BlockStateProperties.MAX_LEVEL_15; i++) {
            var height = i < 8 ? (float) Math.floor(Mth.lerp(i / 8f, 12, 2)) : 16;
            builder.addData("assets/enderscape-patch/models/block/fluid/void_lachryma/" + i + ".json", ModelAsset.builder()
                            .texture("top", voidLachrymaTexture)
                            .texture("side", voidLachrymaTextureSide)
                            .textureReference("particle", "top")
                            .element(Vec3.ZERO, new Vec3(16, height, 16), element -> {
                                for (var dir : Direction.values()) {
                                    if (dir.getAxis() == Direction.Axis.Y) {
                                        element.face(dir, "top");
                                    } else {
                                        element.face(dir, 0, 8 - height / 2f, 8, 8, "side", null);
                                    }
                                }
                            })
                    .build());
        }

        try {
            var main = ImageIO.read(new ByteArrayInputStream(Objects.requireNonNull(builder.getData("assets/enderscape/textures/entity/drifter/drifter.png"))));
            var jelly = ImageIO.read(new ByteArrayInputStream(Objects.requireNonNull(builder.getData("assets/enderscape/textures/entity/drifter/drifter_jelly.png"))));
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

        builder.forEachResource((string, resource) -> {
            for (var expandable : EXPANDABLE) {
                if (string.contains(expandable) && string.startsWith("assets/enderscape/models/block/")) {
                    var asset = ModelAsset.fromJson(Objects.requireNonNull(resource.asString()));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                        builder.addData(AssetPaths.model("enderscape-patch", parentId.getPath()) + ".json",
                                ModelModifiers.expandModel(parentAsset, expandable.contains("sign") ? expansionSign : expansion));
                    }
                }
            }
        });

        for (var entry : BlockStateModelManager.UV_LOCKED_MODELS.get("enderscape").entrySet()) {
            var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().contains(expandable) && entry.getKey().startsWith("block/")) ? expansion : Vec3.ZERO;
            for (var v : entry.getValue()) {
                var suffix = "_uvlock_" + v.x() + "_" + v.y();
                var modelId = v.model().withSuffix(suffix);
                var asset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getData(AssetPaths.model(v.model()) + ".json")), StandardCharsets.UTF_8));

                if (asset.parent().isPresent()) {
                    var parentId = asset.parent().get();
                    var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                    builder.addData(AssetPaths.model("enderscape-patch", parentId.getPath() + suffix) + ".json",
                            ModelModifiers.expandModelAndRotateUVLocked(parentAsset, expand, v.x(), v.y()));
                    builder.addData(AssetPaths.model(modelId) + ".json",
                            new ModelAsset(Optional.of(Identifier.fromNamespaceAndPath("enderscape-patch", parentId.getPath() + suffix)), asset.elements(),
                                    asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                }
            }
        }

        builder.addResourceConverter(((string, resource) -> {
            if (!string.contains("_uvlock_")) {
                for (var expandable : EXPANDABLE) {
                    if (string.contains(expandable) && string.startsWith("assets/enderscape/models/block/")) {
                        var asset = ModelAsset.fromJson(Objects.requireNonNull(resource.asString()));
                        return PackResource.fromAsset(new ModelAsset(asset.parent().map(x -> id(x.getPath())), asset.elements(), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()));
                    }
                }
            }

            if (string.equals("assets/enderscape/items/magnia_attractor.json")) {
                var asset = ItemAsset.fromJson(Objects.requireNonNull(resource.asString()));
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

                return PackResource.fromAsset(new ItemAsset(replacer[0].modifyDeep(EmptyItemModel.INSTANCE, asset.model()), asset.properties()));
            }

            if (string.equals("assets/enderscape/items/mirror.json")) {
                var asset = ItemAsset.fromJson(Objects.requireNonNull(resource.asString()));
                var replacer = new ItemModel.Replacer[]{null};
                replacer[0] = (parent, model) -> {
                    if (model instanceof SelectItemModel<?, ?> selectItemModel && selectItemModel.switchValue().property() instanceof EnderscapeDyeColorProperty) {
                        //noinspection unchecked
                        var select = (SelectItemModel<?, DyeColor>) selectItemModel;

                        return new SelectItemModel<>(new SelectItemModel.Switch<>(
                                new ComponentSelectProperty<>(DataComponents.DYE), select.switchValue().cases()
                                .stream().map(x -> new SelectItemModel.Case<>(x.values(), replacer[0].modifyDeep(model, x.model()))).toList()),
                                select.fallback().map(x -> replacer[0].modifyDeep(model, x)),
                                select.transformation()
                        );
                    }
                    return model;
                };

                return PackResource.fromAsset(new ItemAsset(replacer[0].modifyDeep(EmptyItemModel.INSTANCE, asset.model()), asset.properties()));
            }

            if (string.equals("assets/enderscape/items/rubble_shield.json")) {
                var asset = ItemAsset.fromJson(Objects.requireNonNull(resource.asString()));
                var replacer = new ItemModel.Replacer[]{null};
                replacer[0] = (parent, model) -> {
                    if (model instanceof SelectItemModel<?, ?> selectItemModel && selectItemModel.switchValue().property() instanceof EnderscapeRubbleShieldVariantProperty) {
                        //noinspection unchecked
                        var select = (SelectItemModel<?, Identifier>) selectItemModel;

                        return new SelectItemModel<>(new SelectItemModel.Switch<>(
                                new CustomModelDataStringProperty(0), select.switchValue().cases()
                                .stream().map(x -> new SelectItemModel.Case<>(x.values().stream().map(Identifier::toString).toList()
                                        , replacer[0].modifyDeep(model, x.model()))).toList()),
                                select.fallback().map(x -> replacer[0].modifyDeep(model, x)),
                                select.transformation()
                        );
                    }
                    return model;
                };

                return PackResource.fromAsset(new ItemAsset(replacer[0].modifyDeep(EmptyItemModel.INSTANCE, asset.model()), asset.properties()));
            }

            return resource;
        }));

        builder.addData("assets/enderscape/items/-/block/end_haven_core_active.json", new ItemAsset(new CompositeItemModel(List.of(
                new SpecialItemModel(Enderscape.id("block/end_haven_core_active"), new EndCubeSpecialModel(EndCubeSpecialModel.Type.GATEWAY),
                        Optional.of(new Transformation(new Matrix4f().translate(0.05f, 0.05f, 0.05f).scale(0.9f)))),
                new BasicItemModel(Enderscape.id("block/end_haven_core_active"))
        ))));


        ModelModifiers.createSignModel(builder, "enderscape", "veiled", atlas);
        ModelModifiers.createSignModel(builder, "enderscape","celestial", atlas);
        ModelModifiers.createSignModel(builder, "enderscape","murublight", atlas);

        builder.addData("assets/minecraft/atlases/items.json", atlas.build());
    }
}