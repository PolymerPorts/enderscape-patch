package eu.pb4.enderscapepatch.impl;

import net.bunten.enderscape.network.ClientboundDashJumpPayload;
import net.bunten.enderscape.network.ClientboundDashJumpSoundPayload;
import net.bunten.enderscape.network.ClientboundNebuliteOreSoundPayload;
import net.bunten.enderscape.network.ClientboundRubbleShieldCooldownSoundPayload;
import net.bunten.enderscape.registry.EnderscapeBlockSounds;
import net.bunten.enderscape.registry.EnderscapeItemSounds;
import net.bunten.enderscape.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class PacketHandler {
    public static void handler(ServerPlayer player, CustomPacketPayload payloadx) {
        if (payloadx instanceof ClientboundDashJumpPayload payload) {
            if (player == null || !player.isAlive() || player.isSpectator()) return;
            var vec2f = applyMovementSpeedFactors(getMovementInput(player.getLastClientInput()), player);
            var travel = new Vec3(vec2f.x, 0, vec2f.y).normalize();
            var power = payload.power();

            float sinYRot = Mth.sin((float) (player.getYRot() * (Math.PI / 180)));
            float cosYRot = Mth.cos((float) (player.getYRot() * (Math.PI / 180)));

            player.setDeltaMovement(new Vec3(travel.x * power.x * cosYRot - travel.z * power.x * sinYRot, power.y, travel.z * power.x * cosYRot + travel.x * power.x * sinYRot));
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        } else if (payloadx instanceof ClientboundDashJumpSoundPayload payload) {
            Entity entity = player.level().getEntity(payload.entityId());
            if (entity != null && entity.isAlive() && !entity.isSpectator()) {
                var soundEvent = player.registryAccess().lookupOrThrow(Registries.SOUND_EVENT)
                        .getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, payload.soundEvent()));

                player.connection.send(new ClientboundSoundEntityPacket(soundEvent, entity.getSoundSource(), entity, 1, 1, entity.getRandom().nextLong()));
            }
        } else if (payloadx instanceof ClientboundNebuliteOreSoundPayload payload) {
            BlockPos nebulite = payload.globalPos().pos();
            ResourceKey<Level> dimension = payload.globalPos().dimension();
            var level = player.level();
            Entity entity = player.getCamera();
            if (level != null && level.dimension() == dimension && entity instanceof LivingEntity mob) {
                SoundEvent sound;
                if (BlockUtil.isBlockObstructed(level, nebulite)) {
                    sound = EnderscapeBlockSounds.NEBULITE_ORE_IDLE_OBSTRUCTED;
                } else if (mob.blockPosition().closerThan(nebulite, 12.0)) {
                    sound = EnderscapeBlockSounds.NEBULITE_ORE_IDLE;
                } else {
                    sound = EnderscapeBlockSounds.NEBULITE_ORE_IDLE_FAR;
                }

                float range = Mth.clamp((float) ((double) nebulite.getY() - mob.getY()), -8.0F, 0.0F) / 20.0F + Mth.nextFloat(level.getRandom(), 0.9F, 1.1F);
                player.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.BLOCKS, nebulite.getX(), nebulite.getY(), nebulite.getZ(), range, range, 0));
            }
        } else if (payloadx instanceof ClientboundRubbleShieldCooldownSoundPayload payload) {
            player.connection.send(new ClientboundSoundEntityPacket(EnderscapeItemSounds.RUBBLE_SHIELD_COOLDOWN_OVER, SoundSource.MASTER, player, 1, 1, 0));

        }
    }


    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    public static Vec2 getMovementInput(Input input) {
        float f = getMovementMultiplier(input.forward(), input.backward());
        float g = getMovementMultiplier(input.left(), input.right());
        return (new Vec2(g, f)).normalized();
    }

    private static Vec2 applyMovementSpeedFactors(Vec2 input, ServerPlayer player) {
        if (input.lengthSquared() == 0.0F) {
            return input;
        } else {
            Vec2 vec2f = input.scale(0.98F);
            if (player.isUsingItem() && !player.isPassenger()) {
                vec2f = vec2f.scale(0.2F);
            }

            if (player.isCrouching() || player.isVisuallyCrawling()) {
                float f = (float) player.getAttributeValue(Attributes.SNEAKING_SPEED);
                vec2f = vec2f.scale(f);
            }

            return applyDirectionalMovementSpeedFactors(vec2f);
        }
    }

    private static Vec2 applyDirectionalMovementSpeedFactors(Vec2 vec) {
        float f = vec.length();
        if (f <= 0.0F) {
            return vec;
        } else {
            Vec2 vec2f = vec.scale(1.0F / f);
            float g = getDirectionalMovementSpeedMultiplier(vec2f);
            float h = Math.min(f * g, 1.0F);
            return vec2f.scale(h);
        }
    }

    private static float getDirectionalMovementSpeedMultiplier(Vec2 vec) {
        float f = Math.abs(vec.x);
        float g = Math.abs(vec.y);
        float h = g > f ? f / g : g / f;
        return Mth.sqrt(1.0F + Mth.square(h));
    }

    private static void addParticleClient(Level world, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ((ServerLevel) world).sendParticles(parameters, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
    }

    private static void playEquipmentBreakEffects(LivingEntity entity, ItemStack stack) {
        if (!stack.isEmpty()) {
            Holder<SoundEvent> registryEntry = stack.get(DataComponents.BREAK_SOUND);
            if (registryEntry != null && !entity.isSilent()) {
                entity.level().playSound(entity, entity.getX(), entity.getY(), entity.getZ(), registryEntry.value(), entity.getSoundSource(), 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
            }

            spawnItemParticles(entity, stack, 5);
        }
    }

    public static boolean emulateHandleStatus(Entity entity, byte status) {
        var world = entity.level();
        if (entity instanceof Animal && status == 18) {
            for (int i = 0; i < 7; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.HEART, entity.getRandomX(1.0), entity.getRandomY() + 0.5, entity.getRandomZ(1.0), d, e, f);
            }
            return true;
        }

        if (entity instanceof Mob && status == 20) {
            for (int i = 0; i < 20; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.POOF, entity.getRandomX(1.0) - d * 10.0, entity.getRandomY() - e * 10.0, entity.getRandomZ(1.0) - f * 10.0, d, e, f);
            }
            return true;
        }

        if (entity instanceof LivingEntity livingEntity) {
            switch (status) {
                case 46:
                    for (int j = 0; j < 128; ++j) {
                        double d = (double) j / 127.0;
                        float f = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        float g = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        float h = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        double e = Mth.lerp(d, livingEntity.xo, livingEntity.getX()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getBbWidth() * 2.0;
                        double k = Mth.lerp(d, livingEntity.yo, livingEntity.getY()) + livingEntity.getRandom().nextDouble() * (double) livingEntity.getBbHeight();
                        double l = Mth.lerp(d, livingEntity.zo, livingEntity.getZ()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getBbWidth() * 2.0;
                        addParticleClient(world, ParticleTypes.PORTAL, e, k, l, f, g, h);
                    }
                    return true;
                case 47:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.MAINHAND));
                    return true;
                case 48:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.OFFHAND));
                    return true;
                case 49:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.HEAD));
                    return true;
                case 50:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.CHEST));
                    return true;
                case 51:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.LEGS));
                    return true;
                case 52:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.FEET));
                    return true;
                case 54:
                    BlockState blockState = Blocks.HONEY_BLOCK.defaultBlockState();

                    for (int i = 0; i < 10; ++i) {
                        addParticleClient(world, new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
                    }
                    return true;
                case 60:
                    for (int i = 0; i < 20; ++i) {
                        double d = entity.getRandom().nextGaussian() * 0.02;
                        double e = entity.getRandom().nextGaussian() * 0.02;
                        double f = entity.getRandom().nextGaussian() * 0.02;
                        double g = 10.0;
                        addParticleClient(world, ParticleTypes.POOF, entity.getRandomX(1.0) - d * 10.0, entity.getRandomY() - e * 10.0, entity.getRandomZ(1.0) - f * 10.0, d, e, f);
                    }
                    return true;
                case 65:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.BODY));
                    return true;
                case 67:
                    Vec3 vec3d = entity.getDeltaMovement();

                    for (int i = 0; i < 8; ++i) {
                        double d = entity.getRandom().triangle(0.0, 1.0);
                        double e = entity.getRandom().triangle(0.0, 1.0);
                        double f = entity.getRandom().triangle(0.0, 1.0);
                        addParticleClient(world, ParticleTypes.BUBBLE, entity.getX() + d, entity.getY() + e, entity.getZ() + f, vec3d.x, vec3d.y, vec3d.z);
                    }
                    return true;
                case 68:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.SADDLE));
                    return true;
            }
        }

        if (status == 53) {
            BlockState blockState = Blocks.HONEY_BLOCK.defaultBlockState();

            for (int i = 0; i < 5; ++i) {
                addParticleClient(world, new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        return false;
    }

    private static void spawnItemParticles(LivingEntity entity, ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3 vec3d = new Vec3(((double) entity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.xRot(-entity.getXRot() * 0.017453292F);
            vec3d = vec3d.yRot(-entity.getYRot() * 0.017453292F);
            double d = (double) (-entity.getRandom().nextFloat()) * 0.6 - 0.3;
            Vec3 vec3d2 = new Vec3(((double) entity.getRandom().nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.xRot(-entity.getXRot() * 0.017453292F);
            vec3d2 = vec3d2.yRot(-entity.getYRot() * 0.017453292F);
            vec3d2 = vec3d2.add(entity.getX(), entity.getEyeY(), entity.getZ());
            addParticleClient(entity.level(), new ItemParticleOption(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }

    }
}
