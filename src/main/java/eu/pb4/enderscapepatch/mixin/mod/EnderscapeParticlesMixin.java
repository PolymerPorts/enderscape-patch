package eu.pb4.enderscapepatch.mixin.mod;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.other.PolymerParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.penumbra.enderscape.registry.particle.EnderscapeParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(EnderscapeParticles.class)
public class EnderscapeParticlesMixin {

    @Inject(method = "register(Ljava/lang/String;ZLjava/util/function/Function;Ljava/util/function/Function;)Lnet/minecraft/core/particles/ParticleType;", at = @At("RETURN"))
    private static void register(String name, boolean overrideLimiter, Function<ParticleType<?>, MapCodec<?>> codecFunction, Function<ParticleType<?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>> streamCodecFunction, CallbackInfoReturnable<ParticleType<?>> cir) {
        polymerify(cir.getReturnValue(), name);
    }

    @Inject(method = "register(Ljava/lang/String;Z)Lnet/minecraft/core/particles/SimpleParticleType;", at = @At("RETURN"))
    private static <T extends ParticleOptions> void register(String name, boolean overrideLimiter, CallbackInfoReturnable<SimpleParticleType> cir) {
        polymerify(cir.getReturnValue(), name);
    }

    private static void polymerify(ParticleType<?> returnValue, String name) {
        PolymerParticleType.setOverlay(returnValue, switch (name) {
            case "alluring_magnia", "blinklight_spores", "celestial_spores" -> (_, _) -> ParticleTypes.WHITE_ASH;
            case "chorus_pollen", "corrupt_spores" -> (_, _) -> ParticleTypes.CRIMSON_SPORE;
            case "drift_jelly_dripping" -> (_, _) -> ParticleTypes.BUBBLE;
            case "ender_pearl" -> (_, _) -> ParticleTypes.PORTAL;
            case "end_trial_spawner_detection" -> (_, _) -> ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER;
            case "end_trial_spawner_exhale" -> (_, _) -> ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER;
            case "end_vault_connection" -> (_, _) -> ParticleTypes.VAULT_CONNECTION;
            case "mirror_teleport_in", "mirror_teleport_out" -> (_, _) -> ParticleTypes.PORTAL;
            case "nebulite_ore" -> (_, _) -> ParticleTypes.SCRAPE;
            case "rustle_sleeping_bubble" -> (_, _) -> ParticleTypes.BUBBLE;
            case "rustle_sleeping_bubble_pop" -> (_, _) -> ParticleTypes.BUBBLE_POP;
            case "void_poof" -> (_, _) -> ParticleTypes.PORTAL;
            case "void_stars", "dripping_void_lachryma", "void_entity", "void_splash" -> (_, _) -> ParticleTypes.MYCELIUM;
            case "void_entity_destruction" -> (_, _) -> ParticleTypes.SMOKE;
            case "dash_jump_shockwave" -> (_, _) -> ParticleTypes.SONIC_BOOM;
            case "veiled_leaves" -> (_, _) -> ParticleTypes.PALE_OAK_LEAVES;
            case "rustle_converting" -> (_, _) -> ParticleTypes.HAPPY_VILLAGER;
            case null, default -> (_, _) -> ParticleTypes.FIREWORK;
        });
    }
}
