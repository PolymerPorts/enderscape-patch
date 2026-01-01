package eu.pb4.enderscapepatch.impl;

import net.bunten.enderscape.network.ClientboundDashJumpPayload;
import net.bunten.enderscape.network.ClientboundDashJumpSoundPayload;
import net.bunten.enderscape.network.ClientboundNebuliteOreSoundPayload;
import net.bunten.enderscape.network.ClientboundRubbleShieldCooldownSoundPayload;
import net.bunten.enderscape.registry.EnderscapeBlockSounds;
import net.bunten.enderscape.registry.EnderscapeItemSounds;
import net.bunten.enderscape.util.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
            var power = payload.power();

            float sinYRot = MathHelper.sin((float) (player.getYaw() * (Math.PI / 180)));
            float cosYRot = MathHelper.cos((float) (player.getYaw() * (Math.PI / 180)));

            player.setVelocity(new Vec3d(travel.x * power.x * cosYRot - travel.z * power.x * sinYRot, power.y, travel.z * power.x * cosYRot + travel.x * power.x * sinYRot));
            player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
        } else if (payloadx instanceof ClientboundDashJumpSoundPayload payload) {
            Entity entity = player.getEntityWorld().getEntityById(payload.entityId());
            if (entity != null && entity.isAlive() && !entity.isSpectator()) {
                var soundEvent = player.getRegistryManager().getOrThrow(RegistryKeys.SOUND_EVENT)
                        .getOrThrow(RegistryKey.of(RegistryKeys.SOUND_EVENT, payload.soundEvent()));

                player.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(soundEvent, entity.getSoundCategory(), entity, 1, 1, entity.getRandom().nextLong()));
            }
        } else if (payloadx instanceof ClientboundNebuliteOreSoundPayload payload) {
            BlockPos nebulite = payload.globalPos().pos();
            RegistryKey<World> dimension = payload.globalPos().dimension();
            var level = player.getEntityWorld();
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

    private static void addParticleClient(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ((ServerWorld) world).spawnParticles(parameters, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
    }

    private static void playEquipmentBreakEffects(LivingEntity entity, ItemStack stack) {
        if (!stack.isEmpty()) {
            RegistryEntry<SoundEvent> registryEntry = stack.get(DataComponentTypes.BREAK_SOUND);
            if (registryEntry != null && !entity.isSilent()) {
                entity.getEntityWorld().playSound(entity, entity.getX(), entity.getY(), entity.getZ(), registryEntry.value(), entity.getSoundCategory(), 0.8F, 0.8F + entity.getEntityWorld().getRandom().nextFloat() * 0.4F);
            }

            spawnItemParticles(entity, stack, 5);
        }
    }

    public static boolean emulateHandleStatus(Entity entity, byte status) {
        var world = entity.getEntityWorld();
        if (entity instanceof AnimalEntity && status == 18) {
            for (int i = 0; i < 7; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.HEART, entity.getParticleX(1.0), entity.getRandomBodyY() + 0.5, entity.getParticleZ(1.0), d, e, f);
            }
            return true;
        }

        if (entity instanceof MobEntity && status == 20) {
            for (int i = 0; i < 20; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.POOF, entity.getParticleX(1.0) - d * 10.0, entity.getRandomBodyY() - e * 10.0, entity.getParticleZ(1.0) - f * 10.0, d, e, f);
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
                        double e = MathHelper.lerp(d, livingEntity.lastX, livingEntity.getX()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getWidth() * 2.0;
                        double k = MathHelper.lerp(d, livingEntity.lastY, livingEntity.getY()) + livingEntity.getRandom().nextDouble() * (double) livingEntity.getHeight();
                        double l = MathHelper.lerp(d, livingEntity.lastZ, livingEntity.getZ()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getWidth() * 2.0;
                        addParticleClient(world, ParticleTypes.PORTAL, e, k, l, f, g, h);
                    }
                    return true;
                case 47:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.MAINHAND));
                    return true;
                case 48:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));
                    return true;
                case 49:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.HEAD));
                    return true;
                case 50:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.CHEST));
                    return true;
                case 51:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.LEGS));
                    return true;
                case 52:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.FEET));
                    return true;
                case 54:
                    BlockState blockState = Blocks.HONEY_BLOCK.getDefaultState();

                    for (int i = 0; i < 10; ++i) {
                        addParticleClient(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
                    }
                    return true;
                case 60:
                    for (int i = 0; i < 20; ++i) {
                        double d = entity.getRandom().nextGaussian() * 0.02;
                        double e = entity.getRandom().nextGaussian() * 0.02;
                        double f = entity.getRandom().nextGaussian() * 0.02;
                        double g = 10.0;
                        addParticleClient(world, ParticleTypes.POOF, entity.getParticleX(1.0) - d * 10.0, entity.getRandomBodyY() - e * 10.0, entity.getParticleZ(1.0) - f * 10.0, d, e, f);
                    }
                    return true;
                case 65:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.BODY));
                    return true;
                case 67:
                    Vec3d vec3d = entity.getVelocity();

                    for (int i = 0; i < 8; ++i) {
                        double d = entity.getRandom().nextTriangular(0.0, 1.0);
                        double e = entity.getRandom().nextTriangular(0.0, 1.0);
                        double f = entity.getRandom().nextTriangular(0.0, 1.0);
                        addParticleClient(world, ParticleTypes.BUBBLE, entity.getX() + d, entity.getY() + e, entity.getZ() + f, vec3d.x, vec3d.y, vec3d.z);
                    }
                    return true;
                case 68:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.SADDLE));
                    return true;
            }
        }

        if (status == 53) {
            BlockState blockState = Blocks.HONEY_BLOCK.getDefaultState();

            for (int i = 0; i < 5; ++i) {
                addParticleClient(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        return false;
    }

    private static void spawnItemParticles(LivingEntity entity, ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3d vec3d = new Vec3d(((double) entity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-entity.getPitch() * 0.017453292F);
            vec3d = vec3d.rotateY(-entity.getYaw() * 0.017453292F);
            double d = (double) (-entity.getRandom().nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double) entity.getRandom().nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-entity.getPitch() * 0.017453292F);
            vec3d2 = vec3d2.rotateY(-entity.getYaw() * 0.017453292F);
            vec3d2 = vec3d2.add(entity.getX(), entity.getEyeY(), entity.getZ());
            addParticleClient(entity.getEntityWorld(), new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }

    }
}
