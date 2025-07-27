package eu.pb4.enderscapepatch.impl.mixson;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record MutablePackMap(ResourcePack pack, ResourceType type, Map<Identifier, Resource> overrides) implements Map<Identifier, Resource> {
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
        return key instanceof Identifier identifier && this.pack.open(type, identifier) != null || this.overrides.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Resource get(Object key) {
        if (this.overrides.containsKey(key)) return this.overrides.get(key);

        if (key instanceof Identifier identifier) {
            return new Resource(this.pack, this.pack.open(ResourceType.CLIENT_RESOURCES, identifier));
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

    private void findResources(ResourceType type, String namespace, ResourcePack.ResultConsumer consumer) {
        var root = PolymerCommonUtils.getClientJarRoot().resolve(type.getDirectory()).resolve(namespace);
        DirectoryResourcePack.findResources(namespace, root, List.of(), consumer);
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
