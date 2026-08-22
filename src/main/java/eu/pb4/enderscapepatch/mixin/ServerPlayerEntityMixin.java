package eu.pb4.enderscapepatch.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import eu.pb4.enderscapepatch.impl.EnderscapePolymerPatch;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.penumbra.enderscape.manager.EndHavenManager;
import net.penumbra.enderscape.manager.VoidManager;
import net.penumbra.enderscape.registry.entity.EnderscapeMobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(value = ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {
    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Shadow
    public abstract ServerLevel level();

    @Unique
    private float previousVoidDamage = 0;

    @Unique
    boolean isStunned = false;

    public ServerPlayerEntityMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void emulateVoidDamage(CallbackInfo ci) {
        if (this.isDeadOrDying()) {
            return;
        }

        var voided = VoidManager.getVoidedHealth(this);
        var list = new ArrayList<AttributeInstance>();

        if (voided != previousVoidDamage) {
            this.previousVoidDamage = voided;

            var instance = new AttributeInstance(Attributes.MAX_HEALTH, _ -> {});
            instance.setBaseValue(this.getMaxHealth() - voided);
            list.add(instance);
        }

        var stunned = EnderscapeMobEffects.isStunned(this);

        if (stunned && !this.isStunned) {
            for (var attr : List.of(Attributes.MOVEMENT_SPEED, Attributes.SNEAKING_SPEED, Attributes.JUMP_STRENGTH)) {
                var instance = new AttributeInstance(attr, _ -> {});
                instance.setBaseValue(0);
                list.add(instance);
            }
            this.isStunned = true;
        } else if (!stunned && this.isStunned) {
            for (var attr : List.of(Attributes.MOVEMENT_SPEED,Attributes.SNEAKING_SPEED, Attributes.JUMP_STRENGTH)) {
                var instance = new AttributeInstance(attr, _ -> {});
                instance.setBaseValue(this.getAttributeValue(attr));
                list.add(instance);
            }
            this.isStunned = false;
        }

        if (stunned) {
            this.closeContainer();
        }

        if (!list.isEmpty()) {
            this.connection.send(new ClientboundUpdateAttributesPacket(this.getId(), list));
        }
    }

    @Unique
    private void restoreAttributes() {
        var list = new ArrayList<AttributeInstance>();

        if (this.previousVoidDamage != 0) {
            var attr = new AttributeInstance(Attributes.MAX_HEALTH, _ -> {
            });
            attr.setBaseValue(this.getMaxHealth());
            list.add(attr);
        }

        if (this.isStunned) {
            for (var attr : List.of(Attributes.MOVEMENT_SPEED, Attributes.SNEAKING_SPEED, Attributes.JUMP_STRENGTH)) {
                var instance = new AttributeInstance(attr, _ -> {});
                instance.setBaseValue(this.getAttributeValue(attr));
                list.add(instance);
            }
        }

        if (!list.isEmpty()) {
            this.connection.send(new ClientboundUpdateAttributesPacket(this.getId(), list));
        }
    }


    @WrapOperation(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void handleEndHavenDeathScreen(ServerGamePacketListenerImpl instance, Packet packet, Operation<Void> original) {
        original.call(instance, packet);
        this.restoreAttributes();

        if (packet instanceof ClientboundPlayerCombatKillPacket(int playerId, Component message)
                && playerId == this.getId() && EndHavenManager.promptHavenRespawnChoice(this)) {
            this.sendEndHavenDeathScreen(message);
        }
    }


    @WrapOperation(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;)V"))
    private void handleEndHavenDeathScreen2(ServerGamePacketListenerImpl instance, Packet packet, ChannelFutureListener channelFutureListener, Operation<Void> original) {
        original.call(instance, packet, channelFutureListener);
        this.restoreAttributes();

        if (packet instanceof ClientboundPlayerCombatKillPacket(int playerId, Component message)
                && playerId == this.getId() && EndHavenManager.promptHavenRespawnChoice(this)) {
            this.sendEndHavenDeathScreen(message);
        }
    }

    @Unique
    private void sendEndHavenDeathScreen(Component message) {
        var hardcore = this.level().getServer().isHardcore();
        this.connection.send(new ClientboundSetEntityDataPacket(this.getId(), List.of(
                SynchedEntityData.DataValue.create(LivingEntityAccessor.getDATA_HEALTH_ID(), 0.01f)
        )));
        this.connection.send(new ClientboundShowDialogPacket(Holder.direct(new MultiActionDialog(
                new CommonDialogData(Component.empty()
                        .append(Component.literal("ČĕČ").setStyle(Style.EMPTY.withFont(new FontDescription.Resource(EnderscapePolymerPatch.id("gui")))))
                        .append(Component.translatable(hardcore ? "deathScreen.title.hardcore" : "deathScreen.title").setStyle(Style.EMPTY.withBold(true))),
                        Optional.empty(), false, false,
                        DialogAction.NONE,
                        List.of(new PlainMessage(Component.empty().append(message)
                                .append("\n".repeat(2))
                                .append(Component.translatable("deathScreen.score.value", Component.literal(Integer.toString(this.getScore())).withStyle(ChatFormatting.YELLOW)))
                                .append("\n".repeat(4)), 200)),
                        List.of()),
                List.of(
                        new ActionButton(new CommonButtonData(
                                hardcore ? Component.translatable("screen.enderscape.death.spectate_from_end_haven") : Component.translatable("screen.enderscape.death.respawn_from_end_haven"),
                                200
                        ), Optional.of(new StaticAction(new ClickEvent.Custom(EnderscapePolymerPatch.END_HAVEN_RESPAWN_ACTION, Optional.empty())))),
                        new ActionButton(new CommonButtonData(
                                hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn"),
                                200
                        ), Optional.of(new StaticAction(new ClickEvent.Custom(EnderscapePolymerPatch.RESPAWN_ACTION, Optional.empty())))),
                        new ActionButton(new CommonButtonData(
                                Component.translatable("deathScreen.titleScreen"),
                                200
                        ), Optional.of(new StaticAction(new ClickEvent.Custom(EnderscapePolymerPatch.DISCONNECT_ACTION, Optional.empty()))))
                ),
                Optional.empty(),
                1
        ))));
    }
}