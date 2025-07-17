package eu.pb4.enderscapepatch.mixin.mod;

import com.mojang.serialization.MapCodec;
import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.registry.EnderscapeParticles;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Function;

@Mixin(EnderscapeParticles.class)
public class EnderscapeParticlesMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static SimpleParticleType register(String name, boolean alwaysSpawn) {
        var particle = switch (name) {
            case "alluring_magnia", "blinklight_spores", "celestial_spores" -> ParticleTypes.WHITE_ASH;
            case "chorus_pollen", "corrupt_spores" -> ParticleTypes.CRIMSON_SPORE;
            case "drift_jelly_dripping" -> ParticleTypes.BUBBLE;
            case "ender_pearl" -> ParticleTypes.PORTAL;
            case "end_trial_spawner_detection" -> ParticleTypes.TRIAL_SPAWNER_DETECTION;
            case "end_trial_spawner_exhale" -> ParticleTypes.TRIAL_SPAWNER_DETECTION;
            case "end_vault_connection" -> ParticleTypes.VAULT_CONNECTION;
            case "mirror_teleport_in", "mirror_teleport_out" -> ParticleTypes.PORTAL;
            case "nebulite_ore" -> ParticleTypes.SCRAPE;
            case "rustle_sleeping_bubble" -> ParticleTypes.BUBBLE;
            case "rustle_sleeping_bubble_pop" -> ParticleTypes.BUBBLE_POP;
            case "void_poof" -> ParticleTypes.PORTAL;
            case "void_stars" -> ParticleTypes.MYCELIUM;
            case null, default -> ParticleTypes.FIREWORK;
        };
        Registries.PARTICLE_TYPE.addAlias(Enderscape.id(name), Registries.PARTICLE_TYPE.getId(particle));
        return particle;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static <T extends ParticleEffect> ParticleType<T> register(String name, boolean overrideLimiter, final Function<ParticleType<T>, MapCodec<T>> codecFunction, final Function<ParticleType<T>, PacketCodec<? super RegistryByteBuf, T>> streamCodecFunction) {
        Registries.PARTICLE_TYPE.addAlias(Enderscape.id(name), Registries.PARTICLE_TYPE.getId(ParticleTypes.EXPLOSION));

        return (ParticleType<T>) ParticleTypes.EXPLOSION;
    }
}
