package eu.pb4.enderscapepatch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.penumbra.enderscape.manager.VoidManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.OptionalInt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Mixin(value = ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {
    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Unique
    private float previousVoidDamage = 0;

    public ServerPlayerEntityMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void emulateVoidDamage(CallbackInfo ci) {
        var voided = VoidManager.getVoidedHealth(this);

        if (voided == previousVoidDamage) {
            return;
        }

        this.previousVoidDamage = voided;

        var instance = new AttributeInstance(Attributes.MAX_HEALTH, _ -> {});
        instance.setBaseValue(this.getMaxHealth() - voided);

        this.connection.send(new ClientboundUpdateAttributesPacket(this.getId(), List.of(instance)));
    }
}