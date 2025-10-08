package eu.pb4.enderscapepatch.mixin;

import net.fabricmc.fabric.impl.object.builder.FabricTrackedDataRegistryImpl;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FabricTrackedDataRegistryImpl.class)
public interface FabricTrackedDataRegistryImplAccessor {
    @Accessor
    static Registry<TrackedDataHandler<?>> getHandlerRegistry() {
        throw new AssertionError();
    };
}
