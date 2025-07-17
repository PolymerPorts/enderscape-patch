package eu.pb4.enderscapepatch.impl;

import net.bunten.enderscape.network.ClientboundDashJumpPayload;
import net.bunten.enderscape.network.ClientboundDashJumpSoundPayload;
import net.bunten.enderscape.network.ClientboundNebuliteOreSoundPayload;
import net.bunten.enderscape.network.ClientboundRubbleShieldCooldownSoundPayload;
import net.bunten.enderscape.registry.EnderscapeBlockSounds;
import net.bunten.enderscape.registry.EnderscapeItemSounds;
import net.bunten.enderscape.util.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PacketHandler {
    public static void handler(ServerPlayerEntity player, CustomPayload payloadx) {
        if (payloadx instanceof ClientboundDashJumpPayload payload) {
            if (player == null || !player.isAlive() || player.isSpectator()) return;

            var vec2f = applyMovementSpeedFactors(getMovementInput(player.getPlayerInput()), player);
            var travel = new Vec3d(vec2f.x, 0, vec2f.y).normalize();

            float sinYRot = MathHelper.sin((float) (player.getYaw() * (Math.PI / 180)));
            float cosYRot = MathHelper.cos((float) (player.getYaw() * (Math.PI / 180)));
            float hozPower = player.isGliding() ? payload.horizontalPower() * payload.glideVelocityFactor() : payload.horizontalPower();
            float verPower = player.isGliding() ? payload.verticalPower() * payload.glideVelocityFactor() : payload.verticalPower();

            player.setVelocity(new Vec3d(travel.x * hozPower * cosYRot - travel.z * hozPower * sinYRot, verPower, travel.z * hozPower * cosYRot + travel.x * hozPower * sinYRot));
            player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
        } else if (payloadx instanceof ClientboundDashJumpSoundPayload payload) {
            Entity entity = player.getWorld().getEntityById(payload.entityId());
            if (entity != null && entity.isAlive() && !entity.isSpectator()) {
                var soundEvent = player.getRegistryManager().getOrThrow(RegistryKeys.SOUND_EVENT)
                        .getOrThrow(RegistryKey.of(RegistryKeys.SOUND_EVENT, payload.soundEvent()));

                player.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(soundEvent, entity.getSoundCategory(), entity, 1, 1, entity.getRandom().nextLong()));
            }
        } else if (payloadx instanceof ClientboundNebuliteOreSoundPayload payload) {
            BlockPos nebulite = payload.globalPos().pos();
            RegistryKey<World> dimension = payload.globalPos().dimension();
            var level = player.getWorld();
            Entity entity = player.getCameraEntity();
            if (level != null && level.getRegistryKey() == dimension && entity instanceof LivingEntity mob) {
                SoundEvent sound;
                if (BlockUtil.isBlockObstructed(level, nebulite)) {
                    sound = EnderscapeBlockSounds.NEBULITE_ORE_IDLE_OBSTRUCTED;
                } else if (mob.getBlockPos().isWithinDistance(nebulite, 12.0)) {
                    sound = EnderscapeBlockSounds.NEBULITE_ORE_IDLE;
                } else {
                    sound = EnderscapeBlockSounds.NEBULITE_ORE_IDLE_FAR;
                }

                float range = MathHelper.clamp((float) ((double) nebulite.getY() - mob.getY()), -8.0F, 0.0F) / 20.0F + MathHelper.nextFloat(level.getRandom(), 0.9F, 1.1F);
                player.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(sound), SoundCategory.BLOCKS, nebulite.getX(), nebulite.getY(), nebulite.getZ(), range, range, 0));
            }
        } else if (payloadx instanceof ClientboundRubbleShieldCooldownSoundPayload payload) {
            player.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(EnderscapeItemSounds.RUBBLE_SHIELD_COOLDOWN_OVER, SoundCategory.MASTER, player, 1, 1, 0));

        }
    }


    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    public static Vec2f getMovementInput(PlayerInput input) {
        float f = getMovementMultiplier(input.forward(), input.backward());
        float g = getMovementMultiplier(input.left(), input.right());
        return (new Vec2f(g, f)).normalize();
    }

    private static Vec2f applyMovementSpeedFactors(Vec2f input, ServerPlayerEntity player) {
        if (input.lengthSquared() == 0.0F) {
            return input;
        } else {
            Vec2f vec2f = input.multiply(0.98F);
            if (player.isUsingItem() && !player.hasVehicle()) {
                vec2f = vec2f.multiply(0.2F);
            }

            if (player.isInSneakingPose() || player.isCrawling()) {
                float f = (float) player.getAttributeValue(EntityAttributes.SNEAKING_SPEED);
                vec2f = vec2f.multiply(f);
            }

            return applyDirectionalMovementSpeedFactors(vec2f);
        }
    }

    private static Vec2f applyDirectionalMovementSpeedFactors(Vec2f vec) {
        float f = vec.length();
        if (f <= 0.0F) {
            return vec;
        } else {
            Vec2f vec2f = vec.multiply(1.0F / f);
            float g = getDirectionalMovementSpeedMultiplier(vec2f);
            float h = Math.min(f * g, 1.0F);
            return vec2f.multiply(h);
        }
    }

    private static float getDirectionalMovementSpeedMultiplier(Vec2f vec) {
        float f = Math.abs(vec.x);
        float g = Math.abs(vec.y);
        float h = g > f ? f / g : g / f;
        return MathHelper.sqrt(1.0F + MathHelper.square(h));
    }
}
