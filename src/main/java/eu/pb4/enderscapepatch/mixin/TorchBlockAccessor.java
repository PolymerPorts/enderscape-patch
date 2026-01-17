package eu.pb4.enderscapepatch.mixin;

import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.block.TorchBlock.class)
public interface TorchBlockAccessor {
    @Accessor
    SimpleParticleType getFlameParticle();
}
