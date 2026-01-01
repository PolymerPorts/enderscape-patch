package eu.pb4.enderscapepatch.impl.mixson;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.Resource;

public record MutablePackMap(PackResources pack, PackType type, Map<Identifier, Resource> overrides) implements Map<Identifier, Resource> {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof Identifier identifier && this.pack.getResource(type, identifier) != null || this.overrides.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Resource get(Object key) {
        if (this.overrides.containsKey(key)) return this.overrides.get(key);

        if (key instanceof Identifier identifier) {
            return new Resource(this.pack, this.pack.getResource(PackType.CLIENT_RESOURCES, identifier));
        }
        return null;
    }

    @Nullable
    @Override
    public Resource put(Identifier key, Resource value) {
        return this.overrides.put(key, value);
    }

    @Override
    public Resource remove(Object key) {
        return overrides.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends Identifier, ? extends Resource> m) {
        overrides.putAll(m);
    }

    @Override
    public void clear() {
        overrides.clear();
    }

    @NotNull
    @Override
    public Set<Identifier> keySet() {
        var set = new HashSet<>(overrides.keySet());
        findResources(type, "minecraft", (id, file) -> {
            set.add(id);
        });

        return set;
    }

    private void findResources(PackType type, String namespace, PackResources.ResourceOutput consumer) {
        var root = PolymerCommonUtils.getClientJarRoot().resolve(type.getDirectory()).resolve(namespace);
        PathPackResources.listPath(namespace, root, List.of(), consumer);
    }

    @NotNull
    @Override
    public Collection<Resource> values() {
        return List.of();
    }

    @NotNull
    @Override
    public Set<Entry<Identifier, Resource>> entrySet() {
        var set = new HashSet<Entry<Identifier, Resource>>();
        findResources(type, "minecraft", (id, file) -> {
            set.add(Map.entry(id, new Resource(pack, file)));
        });

        set.addAll(overrides.entrySet());

        return set;
    }
}
