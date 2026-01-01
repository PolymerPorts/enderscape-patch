package eu.pb4.enderscapepatch.impl.mixson;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;
import net.ramixin.mixson.inline.Mixson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class MixsonPatcher {
    private static final PackMetadataSection METADATA = new PackMetadataSection(Component.translatable("resourcePack.vanilla.description"),
            SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).minorRange());
    private static final BuiltInMetadata METADATA_MAP = BuiltInMetadata.of(PackMetadataSection.CLIENT_TYPE, METADATA);

    private static final PackLocationInfo INFO = new PackLocationInfo("vanilla", Component.translatable("resourcePack.vanilla.name"), PackSource.BUILT_IN, Optional.of(KnownPack.vanilla("resourcepacks")));


    public static void setup() {
        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(builder -> {
            var pack = new VanillaPackResourcesBuilder().setMetadata(METADATA_MAP).exposeNamespace("minecraft", "realms").pushUniversalPath(PolymerCommonUtils.getClientJarRoot()).build(INFO);

            var mut = new MutablePackMap(pack, PackType.CLIENT_RESOURCES, new HashMap<>());
            Mixson.runStandardEvents(mut);
            mut.overrides().forEach((id, res) -> {
                try {
                    builder.addData("assets/" + id.getNamespace() + "/" + id.getPath(), res.open().readAllBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
