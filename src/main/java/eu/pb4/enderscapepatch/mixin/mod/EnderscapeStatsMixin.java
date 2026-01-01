package eu.pb4.enderscapepatch.mixin.mod;

import eu.pb4.polymer.core.api.other.PolymerStat;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.registry.EnderscapePotions;
import net.bunten.enderscape.registry.EnderscapeStats;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderscapeStats.class)
public class EnderscapeStatsMixin {

    @Overwrite
    private static Identifier register(String name, StatFormatter formatter) {
        return PolymerStat.registerStat(name, formatter);
    }
}
