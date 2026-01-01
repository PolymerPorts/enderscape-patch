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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    private @Nullable UUID target;
    @Unique
    private final ItemEntity entity = (ItemEntity) (Object) this;
    @Unique
    private static final EntityDataAccessor<Integer> MAGNIA_COOLDOWN_DATA;

    @Unique
    private Vec3 lastDir = new Vec3(0, 0, 0);

    @Shadow
    public abstract ItemStack getItem();

    public ReplacementItemEntityMixin(EntityType<?> type, Level world) {
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
            this.entity.setPickUpDelay(20);
            this.entity.setNoGravity(true);
            if (this.random.nextInt(16) == 0) {
                Level patt0$temp = this.level();
                if (patt0$temp instanceof ServerLevel) {
                    ServerLevel server = (ServerLevel)patt0$temp;
                    server.sendParticles(ParticleTypes.END_ROD, this.position().x, this.position().y + 0.5, this.position().z, 1, 0.30000001192092896, 0.3, 0.30000001192092896, 0.0);
                }
            }

        }, (item) -> {
            item.setNoGravity(false);
        });
    }

    @Unique
    public EntityDataAccessor<Integer> Enderscape$magniaCooldownData() {
        return MAGNIA_COOLDOWN_DATA;
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"defineSynchedData"}
    )
    public void Enderscape$addAdditionalSaveData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        this.defineMagniaData(builder);
    }

    @Inject(
        at = {@At("TAIL")},
        method = {"tick"}
    )
    private void Enderscape$tick(CallbackInfo info) {
        MagniaMoveable.tickMagniaCooldown(this.entity);
        var delta = entity.getDeltaMovement();
        if (delta.lengthSqr() > 1e-5) {
            lastDir = delta.normalize();
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"playerTouch"},
        cancellable = true
    )
    private void Enderscape$playerTouch(Player player, CallbackInfo info) {
        if (!this.level().isClientSide()) {
            ItemStack stack = this.getItem();
            int count = stack.getCount();
            if (pickupDelay == 0 && (target == null || target.equals(player.getUUID())) && EntityMagnet.tryAddToBundle(player.getInventory(), stack)) {
                player.take(this.entity, count);
                if (stack.isEmpty()) {
                    this.discard();
                    stack.setCount(count);
                }

                player.awardStat(Stats.ITEM_PICKED_UP.get(stack.getItem()), count);
                player.onItemPickup(this.entity);
                info.cancel();
            }
        }

    }

    @Inject(
        at = {@At(
    value = "INVOKE",
    target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;I)V",
    shift = Shift.AFTER
)},
        method = {"playerTouch"}
    )
    private void Enderscape$awardItemsPulledStat(Player player, CallbackInfo info) {
        if (player instanceof ServerPlayer server) {
            if (MagniaMoveable.wasMovedByMagnia(this.entity)) {
                server.awardStat(EnderscapeStats.ITEMS_ATTRACTED, this.getItem().getCount());
            }
        }

    }

    static {
        MAGNIA_COOLDOWN_DATA = new EntityDataAccessor<>(EnderscapePolymerPatch.FAKE_TRACKER_INDEX, EntityDataSerializers.INT);
    }
}
