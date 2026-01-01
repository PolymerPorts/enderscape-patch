//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.pb4.enderscapepatch.mixin.mod;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import net.bunten.enderscape.EnderscapeConfig;
import net.bunten.enderscape.entity.DashJumpUser;
import net.bunten.enderscape.entity.EndTrialSpawnable;
import net.bunten.enderscape.entity.magnia.MagniaMoveable;
import net.bunten.enderscape.entity.magnia.MagniaProperties;
import net.bunten.enderscape.item.component.EntityMagnet;
import net.bunten.enderscape.registry.*;
import net.bunten.enderscape.registry.tag.EnderscapeEntityTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Set;

@Mixin({LivingEntity.class})
public abstract class ReplacementLivingEntityMixin extends Entity implements MagniaMoveable, DashJumpUser, EndTrialSpawnable {
    @Unique
    private static final EntityDataAccessor<Integer> MAGNIA_COOLDOWN_DATA;
    @Unique
    private static final EntityDataAccessor<Boolean> DASHED_DATA;
    @Unique
    private static final EntityDataAccessor<Integer> DASH_TICKS_DATA;
    @Unique
    private static final EntityDataAccessor<Boolean> SPAWNED_FROM_END_TRIAL_SPAWNER_DATA;

    static {
        // Patch change start -->
        MAGNIA_COOLDOWN_DATA = new EntityDataAccessor<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, EntityDataSerializers.INT);
        DASHED_DATA = new EntityDataAccessor<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, EntityDataSerializers.BOOLEAN);
        DASH_TICKS_DATA = new EntityDataAccessor<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, EntityDataSerializers.INT);
        SPAWNED_FROM_END_TRIAL_SPAWNER_DATA = new EntityDataAccessor<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, EntityDataSerializers.BOOLEAN);
        // <-- Patch change end
    }

    @Unique
    private final LivingEntity mob = (LivingEntity) (Object) this;
    @Unique
    private int Enderscape$elytraGroundTicks = 0;

    public ReplacementLivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyReturnValue(method = "createLivingAttributes", at = @At(value = "RETURN"))
    private static AttributeSupplier.Builder Enderscape$createLivingAttributes(AttributeSupplier.Builder builder) {
        return builder.add(EnderscapeAttributes.BACKSTAB_DAMAGE).add(EnderscapeAttributes.STEALTH);
    }

    @Shadow
    public abstract boolean isFallFlying();

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot var1);

    @Shadow
    public abstract float getYHeadRot();

    @Unique
    public MagniaProperties createMagniaProperties() {
        return new MagniaProperties((entity) -> {
            return true;
        }, (entity) -> {
            return entity.getType().is(EnderscapeEntityTags.AFFECTED_BY_MAGNIA) ? 0.05F : 0.01F * MagniaMoveable.getMagnetismFactor(entity);
        }, (entity) -> {
            return entity.getType().is(EnderscapeEntityTags.AFFECTED_BY_MAGNIA) ? 0.05F : 0.02F * MagniaMoveable.getMagnetismFactor(entity);
        }, DEFAULT_MAGNIA_PREDICATE, (entity) -> {
            if (entity instanceof LivingEntity living) {
                if (!(entity instanceof Player)) {
                    AttributeInstance gravity = living.getAttribute(Attributes.GRAVITY);
                    if (!gravity.hasModifier(MAGNIA_GRAVITY_MODIFIER.id())) {
                        gravity.addTransientModifier(MAGNIA_GRAVITY_MODIFIER);
                    }
                }
            }

            entity.fallDistance = 0.0;
        }, (entity) -> {
            if (entity instanceof LivingEntity living) {
                if (!(entity instanceof Player)) {
                    AttributeInstance gravity = living.getAttribute(Attributes.GRAVITY);
                    if (gravity.hasModifier(MAGNIA_GRAVITY_MODIFIER.id())) {
                        gravity.removeModifier(MAGNIA_GRAVITY_MODIFIER);
                    }
                }
            }

            entity.fallDistance = 0.0;
        });
    }

    @Unique
    public EntityDataAccessor<Integer> Enderscape$magniaCooldownData() {
        return MAGNIA_COOLDOWN_DATA;
    }

    @Unique
    public EntityDataAccessor<Boolean> Enderscape$dashed() {
        return DASHED_DATA;
    }

    @Unique
    public EntityDataAccessor<Integer> Enderscape$dashTicks() {
        return DASH_TICKS_DATA;
    }

    @Unique
    public EntityDataAccessor<Boolean> Enderscape$spawnedFromEndTrialSpawner() {
        return SPAWNED_FROM_END_TRIAL_SPAWNER_DATA;
    }

    @Inject(at = @At("HEAD"), method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    public void Enderscape$canAttack(LivingEntity target, CallbackInfoReturnable<Boolean> info) {
        if (EnderscapeMobEffects.isStunned(mob)) info.setReturnValue(false);
    }

    @Inject(
            at = {@At("TAIL")},
            method = {"defineSynchedData"}
    )
    public void Enderscape$addAdditionalSaveData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        this.defineMagniaData(builder);
        this.defineDashJumpData(builder);
        this.defineEndTrialSpawnableData(builder);
    }

    @Unique
    private Vec3 Enderscape$tryCancelMovementVec(Vec3 vec3) {
        if (EnderscapeMobEffects.isStunned(this)) return Vec3.ZERO;
        return vec3;
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travelInFluid(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 Enderscape$modifyFluidVec(Vec3 vec3) {
        return Enderscape$tryCancelMovementVec(vec3);
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travelFallFlying(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 Enderscape$modifyFallFlyingVec(Vec3 vec3) {
        return Enderscape$tryCancelMovementVec(vec3);
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travelInAir(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 Enderscape$modifyAirVec(Vec3 vec3) {
        return Enderscape$tryCancelMovementVec(vec3);
    }

    @Inject(at = @At("TAIL"), method = "travel")
    public void Enderscape$travel(Vec3 vec3, CallbackInfo ci) {
        if (EnderscapeMobEffects.isStunned(this) && mob instanceof Mob m) {
            if (m.getNavigation().getPath() != null) m.getNavigation().stop();
        }
    }

    @Inject(
            at = {@At("TAIL")},
            method = {"canStandOnFluid"},
            cancellable = true
    )
    public void Enderscape$canStandOnFluid(FluidState state, CallbackInfoReturnable<Boolean> info) {
        if (this.isFallFlying() && EnderscapeEnchantments.hasRebound(this.level(), this.getItemBySlot(EquipmentSlot.CHEST)) && this.getDeltaMovement().y() > -0.9) {
            info.setReturnValue(true);
        }

    }

    @WrapOperation(
            method = {"canGlide"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;onGround()Z"
            )
    )
    private boolean Enderscape$canGlide(LivingEntity instance, Operation<Boolean> original) {
        return EnderscapeEnchantments.hasRebound(this.level(), instance.getItemBySlot(EquipmentSlot.CHEST)) ? this.Enderscape$elytraGroundTicks >= 10 : original.call(instance);
    }

    @Inject(
            method = {"updateFallFlying"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;canGlide()Z",
                    shift = Shift.BEFORE
            )}
    )
    private void Enderscape$updateFallFlying(CallbackInfo info) {
        if (this.onGround()) {
            if (this.Enderscape$elytraGroundTicks % 3 == 0 && this.Enderscape$elytraGroundTicks < 10 && this.mob.isFallFlying()) {
                Iterator var2 = EquipmentSlot.VALUES.stream().filter(EquipmentSlot::isArmor).toList().iterator();

                while (var2.hasNext()) {
                    EquipmentSlot slot = (EquipmentSlot) var2.next();
                    ItemStack stack = this.mob.getItemBySlot(slot);
                    if (stack.has(DataComponents.GLIDER) && stack.has(DataComponents.EQUIPPABLE) && stack.isDamageableItem() && !stack.nextDamageWillBreak()) {
                        stack.hurtAndBreak(1, this.mob, this.mob.getEquipmentSlotForItem(stack));
                        break;
                    }
                }
            }

            ++this.Enderscape$elytraGroundTicks;
        }

    }

    @Inject(
            at = {@At("HEAD")},
            method = {"take"}
    )
    private void Enderscape$take(Entity entity, int i, CallbackInfo ci) {
        if (this.isAlive() && !this.isSpectator() && MagniaMoveable.wasMovedByMagnia(entity)) {
            LivingEntity var5 = this.mob;
            if (var5 instanceof Player player) {
                ItemStack stack = EntityMagnet.getFirstUsableMagnet(player.getInventory());
                if (!stack.isEmpty() && EntityMagnet.is(stack))
                    stack.hurtAndBreak(1, mob, mob.getEquipmentSlotForItem(stack));
            }
        }

    }

    @Inject(
            at = {@At("HEAD")},
            method = {"tick"}
    )
    private void Enderscape$headTick(CallbackInfo info) {
        if (DashJumpUser.dashed(this.mob) && (this.onGround() || this.getVehicle() != null || this.isInLiquid() || this.isSpectator())) {
            DashJumpUser.setDashed(this.mob, false);
        }

    }

    @Inject(
            at = {@At("TAIL")},
            method = {"tick"}
    )
    private void Enderscape$tailTick(CallbackInfo info) {
        MagniaMoveable.tickMagniaCooldown(this.mob);
        Vec3 vel;
        if (this.Enderscape$hasDriftPhysics() && !this.Enderscape$shouldCancelDriftPhysics()) {
            vel = this.mob.getDeltaMovement();
            double maxSpeed = 2.0;
            double frictionMod = Math.min(1.0, 0.96 + Math.max(0.0, (Math.hypot(vel.x, vel.z) - maxSpeed) / maxSpeed) * 0.5);
            this.mob.setDeltaMovement(vel.x / frictionMod, vel.y, vel.z / frictionMod);
            // Patch change start -->
            if (this.mob instanceof ServerPlayer player) {
                var mov = player.getKnownMovement();
                player.connection.send(new ClientboundPlayerPositionPacket(0,
                        new PositionMoveRotation(Vec3.ZERO, new Vec3(mov.x() / frictionMod - mov.x(), 0, mov.z() / frictionMod - mov.z()), 0, 0),
                        Set.of(Relative.DELTA_X, Relative.DELTA_Y, Relative.DELTA_Z, Relative.X, Relative.Y, Relative.Z, Relative.X_ROT, Relative.Y_ROT)
                ));
            }
            // <-- Patch change end
        }

        if (!this.isFallFlying() && this.Enderscape$elytraGroundTicks > 0) {
            --this.Enderscape$elytraGroundTicks;
        }

        if (DashJumpUser.dashed(this.mob)) {
            vel = this.mob.position().subtract(this.mob.oldPosition()).scale(-1.0);
            if (this.mob.isShiftKeyDown()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().multiply(0.92, 1.0, 0.92));
            }

            int ticks = DashJumpUser.dashTicks(this.mob);
            double x = this.random.nextGaussian() * 0.02 + vel.x;
            double y = this.random.nextGaussian() * 0.02 + vel.y;
            double z = this.random.nextGaussian() * 0.02 + vel.z;
            // Patch change start -->

            if (this.level() instanceof ServerLevel serverWorld) {
                if (ticks > 40 && ticks % 5 == 0 && ticks != 60) {
                    serverWorld.sendParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + (double) (this.getBbHeight() / 2.0F), this.getZ(), 0, vel.x, vel.y, vel.z, 1);
                }
                serverWorld.sendParticles(ParticleTypes.FIREWORK, this.getRandomX(1.0) - vel.x / 2.0, this.getRandomY() - vel.y / 2.0, this.getRandomZ(1.0) - vel.z / 2.0, 0, x, y, z, 1);
            }

            // <-- Patch change end

            DashJumpUser.setDashTicks(this.mob, ticks - 1);
            if (ticks <= 0 || ticks < 50 && vel.lengthSqr() < 0.6) {
                DashJumpUser.setDashed(this.mob, false);
            }
        }

    }

    @ModifyVariable(method = "getVisibilityPercent", at = @At(value = "STORE"), ordinal = 0)
    public double Enderscape$getVisibilityPercent(double original) {
        return original * EnderscapeAttributes.getStealthMultiplier(mob);
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"handleFallFlyingCollisions"}
    )
    private void Enderscape$handleFallFlyingCollisions(double start, double last, CallbackInfo info) {
        if (this.onGround() && this.mob.getDeltaMovement().lengthSqr() > 0.4 && !EnderscapeEnchantments.hasRebound(this.level(), this.getItemBySlot(EquipmentSlot.CHEST))) {
            this.Enderscape$playLandingEffects(start, last);
        }

    }

    @Inject(
            at = {@At("HEAD")},
            method = {"makePoofParticles"},
            cancellable = true
    )
    public void Enderscape$makePoofParticles(CallbackInfo info) {
        if (EnderscapeConfig.getInstance().voidPoofParticlesUponDeath && this.mob.getType().is(EnderscapeEntityTags.CREATES_VOID_PARTICLES_UPON_DEATH)) {
            info.cancel();

            for (int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(EnderscapeParticles.VOID_POOF, this.getRandomX(1.0) - d * 10.0, this.getRandomY() - e * 10.0, this.getRandomZ(1.0) - f * 10.0, d, e, f);
            }
        }

    }

    @Unique
    private void Enderscape$playLandingEffects(double start, double last) {
        this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(), EnderscapeItemSounds.ELYTRA_LAND, SoundSource.PLAYERS, 1.0F, 1.0F);
        Level var6 = this.level();
        if (var6 instanceof ServerLevel server) {
            double difference = start - last;
            float severity = (float) (difference * 10.0 - 3.0);
            Vec3 pos = this.position();
            int count = (int) (Mth.clamp(this.mob.getDeltaMovement().lengthSqr() * 40.0, 5.0, 100.0) + (this.fallDistance + (double) severity) * 5.0);
            server.sendParticles(ParticleTypes.POOF, pos.x, pos.y + 0.2, pos.z, count, 0.0, 0.0, 0.0, 0.3);
            server.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 0.2, pos.z, 1, 0.0, 0.0, 0.0, 0.3);
        }

    }

    @Unique
    protected boolean Enderscape$hasDriftPhysics() {
        return this.mob.getItemBySlot(EquipmentSlot.LEGS).is(EnderscapeItems.DRIFT_LEGGINGS) || this.mob.hasEffect(EnderscapeMobEffects.LOW_GRAVITY);
    }

    @Unique
    protected boolean Enderscape$shouldCancelDriftPhysics() {
        if (this.mob.isSpectator()) {
            return true;
        } else if (this.mob.isShiftKeyDown()) {
            return true;
        } else if (this.mob.onGround()) {
            return true;
        } else if (this.mob.isFallFlying()) {
            return true;
        } else if (this.mob.isPassenger()) {
            return true;
        } else if (this.mob.isInLiquid()) {
            return true;
        } else if (this.mob.hasEffect(MobEffects.LEVITATION)) {
            return true;
        } else {
            LivingEntity var2 = this.mob;
            if (var2 instanceof Player player) {
                return player.getAbilities().flying;
            }

            return false;
        }
    }

    @Inject(
            at = {@At("TAIL")},
            method = {"stopFallFlying"}
    )
    public void Enderscape$stopFallFlying(CallbackInfo info) {
        if (EnderscapeConfig.getInstance().elytraAddOpenCloseSounds && this.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            // Patch change start -->
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), EnderscapeItemSounds.ELYTRA_STOP_GLIDING, this.getSoundSource(), 1.0F, Mth.nextFloat(this.getRandom(), 0.8F, 1.2F));
            // <-- Patch change end
        }
    }
}
