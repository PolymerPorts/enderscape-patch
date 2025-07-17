package eu.pb4.enderscapepatch.impl.res;


import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.enderscapepatch.impl.model.generic.BlockStateModelManager;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.SingleAtlasSource;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ConditionItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.EmptyItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.BooleanProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.CustomModelDataFlagProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelElement;
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

        final var signExtension = new Vec3d(0.04, 0.04, 0.04);

        builder.forEachFile(((string, bytes) -> {
            for (var expandable : EXPANDABLE) {
                if (string.contains(expandable) && string.startsWith("assets/enderscape/models/block/")) {
                    var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));

                        builder.addData(AssetPaths.model("enderscape-patch", parentId.getPath()) + ".json", new ModelAsset(parentAsset.parent(), parentAsset.elements().map(x -> x.stream()
                                .map(element -> new ModelElement(element.from().subtract(new Vec3d(0.06, 0.06, 0.06)), element.to().add(new Vec3d(0.06, 0.06, 0.06)),
                                        element.faces(), element.rotation(), element.shade(), element.lightEmission())
                                ).toList()), parentAsset.textures(), parentAsset.display(), parentAsset.guiLight(), parentAsset.ambientOcclusion()).toBytes());
                    }
                }
            }
        }));

        for (var entry : BlockStateModelManager.UV_LOCKED_MODELS.entrySet()) {
            var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().getPath().contains(expandable) && entry.getKey().getPath().startsWith("block/")) ? new Vec3d(0.06, 0.06, 0.06) : Vec3d.ZERO;
            for (var v : entry.getValue()) {
                var suffix = "_uvlock_" + v.x() + "_" + v.y();
                var modelId = v.model().withSuffixedPath(suffix);
                var asset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getData(AssetPaths.model(v.model()) + ".json")), StandardCharsets.UTF_8));

                if (asset.parent().isPresent()) {
                    var parentId = asset.parent().get();
                    var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                    builder.addData(AssetPaths.model("enderscape-patch", parentId.getPath() + suffix) + ".json",
                            new ModelAsset(parentAsset.parent(), parentAsset.elements().map(x -> x.stream()
                                    .map(element -> new ModelElement(element.from().subtract(expand), element.to().add(expand),
                                            element.faces().entrySet().stream().map(face -> {
                                                var uv = face.getValue().uv();
                                                /*if ((face.getKey().getAxis() == Direction.Axis.Y && v.y() == 0) || (face.getKey().getAxis() != Direction.Axis.Y && v.x() == 0)) {
                                                    return face;
                                                }*/

                                                if (uv.isEmpty()) {
                                                    Vector2f uv1, uv2;
                                                    int rot;
                                                    if (face.getKey().getAxis() == Direction.Axis.Y) {
                                                        uv1 = new Vector2f((float) element.from().getX(), (float) element.from().getZ());
                                                        uv2 = new Vector2f((float) element.to().getX(), (float) element.to().getZ());
                                                        rot = v.y();
                                                    } else {
                                                        uv1 = new Vector2f((float) element.from().getComponentAlongAxis(face.getKey().rotateYClockwise().getAxis()), 16 - (float) element.to().getY());
                                                        uv2 = new Vector2f((float) element.to().getComponentAlongAxis(face.getKey().rotateYClockwise().getAxis()), 16 - (float) element.from().getY());
                                                        rot = v.x();
                                                    }

                                                    if (rot >= 180) {
                                                        uv1.set(16 - uv1.x, 16 - uv1.y);
                                                        uv2.set(16 - uv2.x, 16 - uv2.y);
                                                    }

                                                    uv = List.of(Math.clamp(Math.min(uv1.x, uv2.x), 0, 16),
                                                            Math.clamp(Math.min(uv1.y, uv2.y), 0, 16),
                                                            Math.clamp(Math.max(uv1.x, uv2.x), 0, 16),
                                                            Math.clamp(Math.max(uv1.y, uv2.y), 0, 16));

                                                    if (rot == 90 || rot == 270 || v.y() == -90) {
                                                        uv = List.of(uv.get(1), uv.get(0), uv.get(3), uv.get(2));
                                                    }

                                                    return Map.entry(face.getKey(), new ModelElement.Face(uv, face.getValue().texture(), face.getValue().cullface(),
                                                            (360 + face.getValue().rotation() - rot * face.getKey().getDirection().offset()) % 360,
                                                            face.getValue().tintIndex()));
                                                }

                                                int xBonus = v.x() == 90 || v.x() == 270 ? 180 : 0;
                                                int yBonus = 0;//v.y() != 90 && v.y() != 270 ? 180 : 0;

                                                if (face.getKey().getAxis() == Direction.Axis.Y && v.y() != 0) {
                                                    if (v.y() == 90 || v.y() == 270 || v.y() == -90) {
                                                        uv = List.of(uv.get(1), uv.get(0), uv.get(3), uv.get(2));
                                                    }

                                                    return Map.entry(face.getKey(), new ModelElement.Face(uv, face.getValue().texture(), face.getValue().cullface(),
                                                            (360 + face.getValue().rotation() - v.y() * face.getKey().getDirection().offset() + xBonus) % 360,
                                                            face.getValue().tintIndex()));
                                                }
                                                if (face.getKey().getAxis() != Direction.Axis.Y && v.x() != 0) {
                                                    if (v.x() == 90 || v.x() == 270 || v.x() == -90) {
                                                        uv = List.of(uv.get(1), uv.get(0), uv.get(3), uv.get(2));
                                                    }

                                                    return Map.entry(face.getKey(), new ModelElement.Face(uv, face.getValue().texture(), face.getValue().cullface(),
                                                            (360 + face.getValue().rotation() - v.x() * face.getKey().getDirection().offset() + yBonus) % 360,
                                                            face.getValue().tintIndex()));
                                                }

                                                return face;
                                            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), element.rotation(), element.shade(), element.lightEmission())
                                    ).toList()), parentAsset.textures(), parentAsset.display(), parentAsset.guiLight(), parentAsset.ambientOcclusion()).toBytes());

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

            if (string.startsWith("assets/enderscape-patch/models/block/template_")) {
                var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                return new ModelAsset(asset.parent(), asset.elements().map(x -> x.stream()
                        .map(element -> new ModelElement(element.from().subtract(signExtension), element.to().add(signExtension),
                                element.faces().entrySet().stream().map(face -> {
                                    var uv = face.getValue().uv();
                                    if (uv.isEmpty()) {
                                        Vector2f uv1, uv2;
                                        if (face.getKey().getAxis() == Direction.Axis.Y) {
                                            uv1 = new Vector2f((float) element.from().getX(), (float) element.from().getZ());
                                            uv2 = new Vector2f((float) element.to().getX(), (float) element.to().getZ());
                                        } else {
                                            uv1 = new Vector2f((float) element.from().getComponentAlongAxis(face.getKey().rotateYClockwise().getAxis()), 16 - (float) element.to().getY());
                                            uv2 = new Vector2f((float) element.to().getComponentAlongAxis(face.getKey().rotateYClockwise().getAxis()), 16 - (float) element.from().getY());
                                        }

                                        uv = List.of(Math.clamp(Math.min(uv1.x, uv2.x), 0, 16),
                                                Math.clamp(Math.min(uv1.y, uv2.y), 0, 16),
                                                Math.clamp(Math.max(uv1.x, uv2.x), 0, 16),
                                                Math.clamp(Math.max(uv1.y, uv2.y), 0, 16));

                                        return Map.entry(face.getKey(), new ModelElement.Face(uv, face.getValue().texture(), face.getValue().cullface(),
                                                face.getValue().rotation(),
                                                face.getValue().tintIndex()));
                                    }
                                    return face;
                                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), element.rotation(), element.shade(), element.lightEmission())
                        ).toList()), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes();
            }
            return bytes;
        }));


        createSignModel(builder, "veiled", atlas);
        createSignModel(builder, "celestial", atlas);
        createSignModel(builder, "murublight", atlas);

        builder.addData("assets/minecraft/atlases/blocks.json", atlas.build());
    }

    private static void createSignModel(ResourcePackBuilder builder, String name, AtlasAsset.Builder atlas) {
        var textureRegular = Enderscape.id("entity/signs/" + name);
        var textureHanging = Enderscape.id("entity/signs/hanging/" + name);

        atlas.add(new SingleAtlasSource(textureRegular, Optional.empty()));
        atlas.add(new SingleAtlasSource(textureHanging, Optional.empty()));

        builder.addData(AssetPaths.blockModel(id(name + "_sign")), ModelAsset.builder()
                .parent(id("block/template_sign"))
                .texture("sign", textureRegular.toString()).build());
        builder.addData(AssetPaths.blockModel(id(name + "_wall_sign")), ModelAsset.builder()
                .parent(id("block/template_wall_sign"))
                .texture("sign", textureRegular.toString()).build());
        builder.addData(AssetPaths.blockModel(id(name + "_hanging_sign")), ModelAsset.builder()
                .parent(id("block/template_hanging_sign"))
                .texture("sign", textureHanging.toString()).build());
        builder.addData(AssetPaths.blockModel(id(name + "_wall_hanging_sign")), ModelAsset.builder()
                .parent(id("block/template_wall_hanging_sign"))
                .texture("sign", textureHanging.toString()).build());
    }
}