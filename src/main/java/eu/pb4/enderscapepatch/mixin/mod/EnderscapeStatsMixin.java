package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.other.PolymerStat;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.penumbra.enderscape.registry.entity.EnderscapeStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnderscapeStats.class)
public class EnderscapeStatsMixin {

    @Overwrite
    private static Identifier register(String name, StatFormatter formatter) {
        return PolymerStat.registerStat(name, formatter);
    }
}
