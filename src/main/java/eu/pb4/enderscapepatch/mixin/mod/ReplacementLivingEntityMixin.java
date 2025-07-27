//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.pb4.enderscapepatch.mixin.mod;

import java.util.Iterator;
import java.util.Set;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import net.bunten.enderscape.EnderscapeConfig;
import net.bunten.enderscape.entity.DashJumpUser;
import net.bunten.enderscape.entity.EndTrialSpawnable;
import net.bunten.enderscape.entity.magnia.MagniaMoveable;
import net.bunten.enderscape.entity.magnia.MagniaProperties;
import net.bunten.enderscape.item.MagniaAttractorItem;
import net.bunten.enderscape.item.NebuliteToolContext;
import net.bunten.enderscape.item.NebuliteToolItem;
import net.bunten.enderscape.particle.DashJumpShockwaveParticleOptions;
import net.bunten.enderscape.registry.EnderscapeEnchantments;
import net.bunten.enderscape.registry.EnderscapeItemSounds;
import net.bunten.enderscape.registry.EnderscapeItems;
import net.bunten.enderscape.registry.EnderscapeMobEffects;
import net.bunten.enderscape.registry.EnderscapeParticles;
import net.bunten.enderscape.registry.tag.EnderscapeEntityTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class ReplacementLivingEntityMixin extends Entity implements MagniaMoveable, DashJumpUser, EndTrialSpawnable {
    @Unique
    private final LivingEntity mob = (LivingEntity) (Object) this;
    @Unique
    private int Enderscape$elytraGroundTicks = 0;
    @Unique
    private static final TrackedData<Integer> MAGNIA_COOLDOWN_DATA;
    @Unique
    private static final TrackedData<Boolean> DASHED_DATA;
    @Unique
    private static final TrackedData<Integer> DASH_TICKS_DATA;
    @Unique
    private static final TrackedData<Boolean> SPAWNED_FROM_END_TRIAL_SPAWNER_DATA;

    @Shadow
    public abstract boolean isGliding();

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Shadow
    public abstract float getHeadYaw();

    public ReplacementLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    public MagniaProperties createMagniaProperties() {
        return new MagniaProperties((entity) -> {
            return true;
        }, (entity) -> {
            return entity.getType().isIn(EnderscapeEntityTags.AFFECTED_BY_MAGNIA) ? 0.05F : 0.01F * MagniaMoveable.getMagnetismFactor(entity);
        }, (entity) -> {
            return entity.getType().isIn(EnderscapeEntityTags.AFFECTED_BY_MAGNIA) ? 0.05F : 0.02F * MagniaMoveable.getMagnetismFactor(entity);
        }, DEFAULT_MAGNIA_PREDICATE, (entity) -> {
            if (entity instanceof LivingEntity living) {
                if (!(entity instanceof PlayerEntity)) {
                    EntityAttributeInstance gravity = living.getAttributeInstance(EntityAttributes.GRAVITY);
                    if (!gravity.hasModifier(MAGNIA_GRAVITY_MODIFIER.id())) {
                        gravity.addTemporaryModifier(MAGNIA_GRAVITY_MODIFIER);
                    }
                }
            }

            entity.fallDistance = 0.0;
            if (this.random.nextInt(16) == 0) {
                World patt0$temp = this.getWorld();
                if (patt0$temp instanceof ServerWorld) {
                    ServerWorld server = (ServerWorld)patt0$temp;
                    server.spawnParticles(ParticleTypes.END_ROD, this.getPos().x, this.getPos().y + 0.5, this.getPos().z, 1, 0.30000001192092896, 0.3, 0.30000001192092896, 0.0);
                }
            }

        }, (entity) -> {
            if (entity instanceof LivingEntity living) {
                if (!(entity instanceof PlayerEntity)) {
                    EntityAttributeInstance gravity = living.getAttributeInstance(EntityAttributes.GRAVITY);
                    if (gravity.hasModifier(MAGNIA_GRAVITY_MODIFIER.id())) {
                        gravity.removeModifier(MAGNIA_GRAVITY_MODIFIER);
                    }
                }
            }

            entity.fallDistance = 0.0;
        });
    }

    @Unique
    public TrackedData<Integer> Enderscape$magniaCooldownData() {
        return MAGNIA_COOLDOWN_DATA;
    }

    @Unique
    public TrackedData<Boolean> Enderscape$dashed() {
        return DASHED_DATA;
    }

    @Unique
    public TrackedData<Integer> Enderscape$dashTicks() {
        return DASH_TICKS_DATA;
    }

    @Unique
    public TrackedData<Boolean> Enderscape$spawnedFromEndTrialSpawner() {
        return SPAWNED_FROM_END_TRIAL_SPAWNER_DATA;
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"initDataTracker"}
    )
    public void Enderscape$addAdditionalSaveData(DataTracker.Builder builder, CallbackInfo ci) {
        this.defineMagniaData(builder);
        this.defineDashJumpData(builder);
        this.defineEndTrialSpawnableData(builder);
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"canWalkOnFluid"},
        cancellable = true
    )
    public void Enderscape$canStandOnFluid(FluidState state, CallbackInfoReturnable<Boolean> info) {
        if (this.isGliding() && EnderscapeEnchantments.hasRebound(this.getWorld(), this.getEquippedStack(EquipmentSlot.CHEST)) && this.getVelocity().getY() > -0.9) {
            info.setReturnValue(true);
        }

    }

    @Redirect(
        method = {"canGlide"},
        at = @At(
    value = "INVOKE",
    target = "Lnet/minecraft/entity/LivingEntity;isOnGround()Z"
)
    )
    private boolean Enderscape$canGlide(LivingEntity instance) {
        return EnderscapeEnchantments.hasRebound(this.getWorld(), instance.getEquippedStack(EquipmentSlot.CHEST)) ? this.Enderscape$elytraGroundTicks >= 10 : instance.isOnGround();
    }

    @Inject(
        method = {"tickGliding"},
        at = {@At(
    value = "INVOKE",
    target = "Lnet/minecraft/entity/LivingEntity;canGlide()Z",
    shift = Shift.BEFORE
)}
    )
    private void Enderscape$updateFallFlying(CallbackInfo info) {
        if (this.isOnGround()) {
            if (this.Enderscape$elytraGroundTicks % 3 == 0 && this.Enderscape$elytraGroundTicks < 10 && this.mob.isGliding()) {
                Iterator var2 = EquipmentSlot.VALUES.stream().filter(EquipmentSlot::isArmorSlot).toList().iterator();

                while(var2.hasNext()) {
                    EquipmentSlot slot = (EquipmentSlot)var2.next();
                    ItemStack stack = this.mob.getEquippedStack(slot);
                    if (stack.contains(DataComponentTypes.GLIDER) && stack.contains(DataComponentTypes.EQUIPPABLE) && stack.isDamageable() && !stack.willBreakNextUse()) {
                        stack.damage(1, this.mob, this.mob.getPreferredEquipmentSlot(stack));
                        break;
                    }
                }
            }

            ++this.Enderscape$elytraGroundTicks;
        }

    }

    @Inject(
        at = {@At("HEAD")},
        method = {"sendPickup"}
    )
    private void Enderscape$take(Entity entity, int i, CallbackInfo ci) {
        if (this.isAlive() && !this.isSpectator() && MagniaMoveable.wasMovedByMagnia(entity)) {
            LivingEntity var5 = this.mob;
            if (var5 instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity)var5;
                ItemStack stack = MagniaAttractorItem.getValidAttractor(player.getInventory());
                if (!stack.isEmpty()) {
                    NebuliteToolContext context = new NebuliteToolContext(stack, this.getWorld(), player);
                    if (stack.getItem() instanceof MagniaAttractorItem && NebuliteToolItem.fuelExceedsCost(context)) {
                        MagniaAttractorItem.incrementEntitiesPulled(stack, 1);
                        MagniaAttractorItem.tryUseFuel(context, 1 - MagniaAttractorItem.getEntitiesPulledToUseFuel(stack));
                    }
                }
            }
        }

    }

    @Inject(
        at = {@At("HEAD")},
        method = {"tick"}
    )
    private void Enderscape$headTick(CallbackInfo info) {
        if (DashJumpUser.dashed(this.mob) && (this.isOnGround() || this.getVehicle() != null || this.isInFluid() || this.isSpectator())) {
            DashJumpUser.setDashed(this.mob, false);
        }

    }

    @Inject(
        at = {@At("TAIL")},
        method = {"tick"}
    )
    private void Enderscape$tailTick(CallbackInfo info) {
        MagniaMoveable.tickMagniaCooldown(this.mob);
        Vec3d vel;
        if (this.Enderscape$hasDriftPhysics() && !this.Enderscape$shouldCancelDriftPhysics()) {
            vel = this.mob.getVelocity();
            double maxSpeed = 2.0;
            double frictionMod = Math.min(1.0, 0.96 + Math.max(0.0, (Math.hypot(vel.x, vel.z) - maxSpeed) / maxSpeed) * 0.5);
            this.mob.setVelocity(vel.x / frictionMod, vel.y, vel.z / frictionMod);
            // Patch change start -->
            if (this.mob instanceof ServerPlayerEntity player) {
                var mov = player.getMovement();
                player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(0,
                        new PlayerPosition(Vec3d.ZERO, new Vec3d(mov.getX() / frictionMod - mov.getX(), 0, mov.getZ() / frictionMod - mov.getZ()), 0, 0),
                        Set.of(PositionFlag.DELTA_X, PositionFlag.DELTA_Y, PositionFlag.DELTA_Z, PositionFlag.X, PositionFlag.Y, PositionFlag.Z, PositionFlag.X_ROT, PositionFlag.Y_ROT)
                ));
            }
            // <-- Patch change end
        }

        if (!this.isGliding() && this.Enderscape$elytraGroundTicks > 0) {
            --this.Enderscape$elytraGroundTicks;
        }

        if (DashJumpUser.dashed(this.mob)) {
            vel = this.mob.getPos().subtract(this.mob.getLastRenderPos()).multiply(-1.0);
            if (this.mob.isSneaking()) {
                this.mob.setVelocity(this.mob.getVelocity().multiply(0.92, 1.0, 0.92));
            }

            int ticks = DashJumpUser.dashTicks(this.mob);
            double x = this.random.nextGaussian() * 0.02 + vel.x;
            double y = this.random.nextGaussian() * 0.02 + vel.y;
            double z = this.random.nextGaussian() * 0.02 + vel.z;
            // Patch change start -->

            if (this.getWorld() instanceof ServerWorld serverWorld) {
                if (ticks > 40 && ticks % 5 == 0 && ticks != 60) {
                    serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + (double)(this.getHeight() / 2.0F), this.getZ(), 0, vel.x, vel.y, vel.z, 1);
                }
                serverWorld.spawnParticles(ParticleTypes.FIREWORK, this.getParticleX(1.0) - vel.x / 2.0, this.getRandomBodyY() - vel.y / 2.0, this.getParticleZ(1.0) - vel.z / 2.0, 0,  x, y, z, 1);
            }

            // <-- Patch change end

            DashJumpUser.setDashTicks(this.mob, ticks - 1);
            if (ticks <= 0 || ticks < 50 && vel.lengthSquared() < 0.6) {
                DashJumpUser.setDashed(this.mob, false);
            }
        }

    }

    @Inject(
        at = {@At("HEAD")},
        method = {"checkGlidingCollision"}
    )
    private void Enderscape$handleFallFlyingCollisions(double start, double last, CallbackInfo info) {
        if (this.isOnGround() && this.mob.getVelocity().lengthSquared() > 0.4 && !EnderscapeEnchantments.hasRebound(this.getWorld(), this.getEquippedStack(EquipmentSlot.CHEST))) {
            this.Enderscape$playLandingEffects(start, last);
        }

    }

    @Inject(
        at = {@At("HEAD")},
        method = {"addDeathParticles"},
        cancellable = true
    )
    public void Enderscape$makePoofParticles(CallbackInfo info) {
        if (EnderscapeConfig.getInstance().voidPoofParticlesUponDeath && this.mob.getType().isIn(EnderscapeEntityTags.CREATES_VOID_PARTICLES_UPON_DEATH)) {
            info.cancel();

            for(int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.getWorld().addParticleClient(EnderscapeParticles.VOID_POOF, this.getParticleX(1.0) - d * 10.0, this.getRandomBodyY() - e * 10.0, this.getParticleZ(1.0) - f * 10.0, d, e, f);
            }
        }

    }

    @Unique
    private void Enderscape$playLandingEffects(double start, double last) {
        this.mob.getWorld().playSound((Entity)null, this.mob.getX(), this.mob.getY(), this.mob.getZ(), EnderscapeItemSounds.ELYTRA_LAND, SoundCategory.PLAYERS, 1.0F, 1.0F);
        World var6 = this.getWorld();
        if (var6 instanceof ServerWorld server) {
            double difference = start - last;
            float severity = (float)(difference * 10.0 - 3.0);
            Vec3d pos = this.getPos();
            int count = (int)(MathHelper.clamp(this.mob.getVelocity().lengthSquared() * 40.0, 5.0, 100.0) + (this.fallDistance + (double)severity) * 5.0);
            server.spawnParticles(ParticleTypes.POOF, pos.x, pos.y + 0.2, pos.z, count, 0.0, 0.0, 0.0, 0.3);
            server.spawnParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 0.2, pos.z, 1, 0.0, 0.0, 0.0, 0.3);
        }

    }

    @Unique
    protected boolean Enderscape$hasDriftPhysics() {
        return this.mob.getEquippedStack(EquipmentSlot.LEGS).isOf(EnderscapeItems.DRIFT_LEGGINGS) || this.mob.hasStatusEffect(EnderscapeMobEffects.LOW_GRAVITY);
    }

    @Unique
    protected boolean Enderscape$shouldCancelDriftPhysics() {
        if (this.mob.isSpectator()) {
            return true;
        } else if (this.mob.isSneaking()) {
            return true;
        } else if (this.mob.isOnGround()) {
            return true;
        } else if (this.mob.isGliding()) {
            return true;
        } else if (this.mob.hasVehicle()) {
            return true;
        } else if (this.mob.isInFluid()) {
            return true;
        } else if (this.mob.hasStatusEffect(StatusEffects.LEVITATION)) {
            return true;
        } else {
            LivingEntity var2 = this.mob;
            if (var2 instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity)var2;
                if (player.getAbilities().flying) {
                    return true;
                }
            }

            return false;
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"stopGliding"}
    )
    public void Enderscape$stopFallFlying(CallbackInfo info) {
        if (EnderscapeConfig.getInstance().elytraAddOpenCloseSounds && this.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            this.playSound(EnderscapeItemSounds.ELYTRA_STOP_GLIDING, 1.0F, MathHelper.nextFloat(this.getRandom(), 0.8F, 1.2F));
        }

    }

    static {
        // Patch change start -->
        MAGNIA_COOLDOWN_DATA = new TrackedData<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, TrackedDataHandlerRegistry.INTEGER);
        DASHED_DATA = new TrackedData<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, TrackedDataHandlerRegistry.BOOLEAN);
        DASH_TICKS_DATA = new TrackedData<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, TrackedDataHandlerRegistry.INTEGER);
        SPAWNED_FROM_END_TRIAL_SPAWNER_DATA = new TrackedData<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, TrackedDataHandlerRegistry.BOOLEAN);
        // <-- Patch change end
    }
}
