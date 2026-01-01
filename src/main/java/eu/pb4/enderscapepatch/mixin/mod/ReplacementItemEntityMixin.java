//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.pb4.enderscapepatch.mixin.mod;

import java.util.UUID;

import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import net.bunten.enderscape.entity.magnia.MagniaMoveable;
import net.bunten.enderscape.entity.magnia.MagniaProperties;
import net.bunten.enderscape.item.component.EntityMagnet;
import net.bunten.enderscape.registry.EnderscapeStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemEntity.class})
public abstract class ReplacementItemEntityMixin extends Entity implements MagniaMoveable {
    @Shadow
    private int pickupDelay;
    @Shadow
    private @Nullable UUID owner;
    @Unique
    private final ItemEntity entity = (ItemEntity) (Object) this;
    @Unique
    private static final TrackedData<Integer> MAGNIA_COOLDOWN_DATA;

    @Unique
    private Vec3d lastDir = new Vec3d(0, 0, 0);

    @Shadow
    public abstract ItemStack getStack();

    public ReplacementItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    public MagniaProperties createMagniaProperties() {
        return new MagniaProperties((item) -> {
            return false;
        }, (item) -> {
            return 0.6F;
        }, (item) -> {
            return 0.8F;
        }, (item) -> {
            return true;
        }, (item) -> {
            this.entity.setPickupDelay(20);
            this.entity.setNoGravity(true);
            if (this.random.nextInt(16) == 0) {
                World patt0$temp = this.getEntityWorld();
                if (patt0$temp instanceof ServerWorld) {
                    ServerWorld server = (ServerWorld)patt0$temp;
                    server.spawnParticles(ParticleTypes.END_ROD, this.getEntityPos().x, this.getEntityPos().y + 0.5, this.getEntityPos().z, 1, 0.30000001192092896, 0.3, 0.30000001192092896, 0.0);
                }
            }

        }, (item) -> {
            item.setNoGravity(false);
        });
    }

    @Unique
    public TrackedData<Integer> Enderscape$magniaCooldownData() {
        return MAGNIA_COOLDOWN_DATA;
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"initDataTracker"}
    )
    public void Enderscape$addAdditionalSaveData(DataTracker.Builder builder, CallbackInfo ci) {
        this.defineMagniaData(builder);
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"tick"}
    )
    private void Enderscape$tick(CallbackInfo info) {
        MagniaMoveable.tickMagniaCooldown(this.entity);
        var delta = entity.getVelocity();
        if (delta.lengthSquared() > 1e-5) {
            lastDir = delta.normalize();
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"onPlayerCollision"},
        cancellable = true
    )
    private void Enderscape$playerTouch(PlayerEntity player, CallbackInfo info) {
        if (!this.getEntityWorld().isClient()) {
            ItemStack stack = this.getStack();
            int count = stack.getCount();
            if (pickupDelay == 0 && (owner == null || owner.equals(player.getUuid())) && EntityMagnet.tryAddToBundle(player.getInventory(), stack)) {
                player.sendPickup(this.entity, count);
                if (stack.isEmpty()) {
                    this.discard();
                    stack.setCount(count);
                }

                player.increaseStat(Stats.PICKED_UP.getOrCreateStat(stack.getItem()), count);
                player.triggerItemPickedUpByEntityCriteria(this.entity);
                info.cancel();
            }
        }

    }

    @Inject(
        at = {@At(
    value = "INVOKE",
    target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/stat/Stat;I)V",
    shift = Shift.AFTER
)},
        method = {"onPlayerCollision"}
    )
    private void Enderscape$awardItemsPulledStat(PlayerEntity player, CallbackInfo info) {
        if (player instanceof ServerPlayerEntity server) {
            if (MagniaMoveable.wasMovedByMagnia(this.entity)) {
                server.increaseStat(EnderscapeStats.ITEMS_ATTRACTED, this.getStack().getCount());
            }
        }

    }

    static {
        MAGNIA_COOLDOWN_DATA = new TrackedData<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, TrackedDataHandlerRegistry.INTEGER);
    }
}
