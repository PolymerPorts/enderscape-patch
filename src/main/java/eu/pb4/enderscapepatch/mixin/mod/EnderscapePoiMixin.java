package eu.pb4.enderscapepatch.mixin.mod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.bunten.enderscape.registry.EnderscapePoi;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderscapePoi.class)
public abstract class EnderscapePoiMixin {
    @WrapOperation(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/object/builder/v1/world/poi/PointOfInterestHelper;register(Lnet/minecraft/resources/Identifier;IILjava/lang/Iterable;)Lnet/minecraft/world/entity/ai/village/poi/PoiType;"
        )
    )
    private static PoiType polymerify(Identifier id, int ticketCount, int searchDistance, Iterable<BlockState> blocks, Operation<PoiType> original) {
        PoiType pointOfInterestType = original.call(id, ticketCount, searchDistance, blocks);
        RegistrySyncUtils.setServerEntry(BuiltInRegistries.POINT_OF_INTEREST_TYPE, pointOfInterestType);
        return pointOfInterestType;
    }
}
