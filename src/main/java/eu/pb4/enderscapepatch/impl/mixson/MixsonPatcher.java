package eu.pb4.enderscapepatch.impl.mixson;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.SharedConstants;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.text.Text;
import net.ramixin.mixson.inline.Mixson;

import java.io.IOException;
import java.util.*;

public class MixsonPatcher {
    private static final PackResourceMetadata METADATA = new PackResourceMetadata(Text.translatable("resourcePack.vanilla.description"),
        SharedConstants.getGameVersion().packVersion(ResourceType.CLIENT_RESOURCES).majorRange());
    private static final ResourceMetadataMap METADATA_MAP = ResourceMetadataMap.of(PackResourceMetadata.CLIENT_RESOURCES_SERIALIZER, METADATA);

    private static final ResourcePackInfo INFO = new ResourcePackInfo("vanilla", Text.translatable("resourcePack.vanilla.name"), ResourcePackSource.BUILTIN, Optional.of(VersionedIdentifier.createVanilla("resourcepacks")));


    public static void setup() {
        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(builder -> {
            var pack = new DefaultResourcePackBuilder().withMetadataMap(METADATA_MAP).withNamespaces("minecraft", "realms").withRoot(PolymerCommonUtils.getClientJarRoot()).build(INFO);

            var mut = new MutablePackMap(pack, ResourceType.CLIENT_RESOURCES, new HashMap<>());
            Mixson.runStandardEvents(mut);
            mut.overrides().forEach((id, res) -> {
                try {
                    builder.addData("assets/" + id.getNamespace() + "/" + id.getPath(), res.getInputStream().readAllBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
