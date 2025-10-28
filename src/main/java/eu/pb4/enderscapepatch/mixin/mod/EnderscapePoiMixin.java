package eu.pb4.enderscapepatch.mixin.mod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.bunten.enderscape.registry.EnderscapePoi;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderscapePoi.class)
public abstract class EnderscapePoiMixin {
    @WrapOperation(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/object/builder/v1/world/poi/PointOfInterestHelper;register(Lnet/minecraft/util/Identifier;IILjava/lang/Iterable;)Lnet/minecraft/world/poi/PointOfInterestType;"
        )
    )
    private static PointOfInterestType polymerify(Identifier id, int ticketCount, int searchDistance, Iterable<BlockState> blocks, Operation<PointOfInterestType> original) {
        PointOfInterestType pointOfInterestType = original.call(id, ticketCount, searchDistance, blocks);
        RegistrySyncUtils.setServerEntry(Registries.POINT_OF_INTEREST_TYPE, pointOfInterestType);
        return pointOfInterestType;
    }
}
