package eu.pb4.enderscapepatch.impl.entity;

import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.PolyModelInstance;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.EntityModel;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.IdentifiedUniqueEntityAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.bunten.enderscape.Enderscape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Consumer;

public record BasePolymerEntity(Entity entity) implements PolymerEntity {
    public static final Identifier MODEL = Enderscape.id("model");
    public BasePolymerEntity {
        var model = VanillishEntityModel.BY_TYPE.get(entity.getType());
        if (model != null) {
            //noinspection unchecked
            IdentifiedUniqueEntityAttachment.ofTicking(MODEL, new VanillishEntityModel<>(entity, (PolyModelInstance<EntityModel<Entity>>) model), entity);
        }
    }

    @Override
    public void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        if (packet instanceof EntityAnimationS2CPacket) {
            return;
        }
        if (packet instanceof EntityPassengersSetS2CPacket packet1 && packet1.getPassengerIds().length != 0) {
            var model = (VanillishEntityModel<?>) UniqueIdentifiableAttachment.get(entity, MODEL).holder();
            consumer.accept(VirtualEntityUtils.createRidePacket(entity.getId(), IntList.of(model.interaction.getEntityId())));
            consumer.accept(VirtualEntityUtils.createRidePacket(model.interaction.getEntityId(), packet1.getPassengerIds()));
            return;
        }

        if (packet instanceof EntityAttachS2CPacket packet1) {
            var model = (VanillishEntityModel<?>) UniqueIdentifiableAttachment.get(entity, MODEL).holder();
            consumer.accept(VirtualEntityUtils.createEntityAttachPacket(model.leadAttachment.getEntityId(), packet1.getHoldingEntityId()));
            return;
        }
        PolymerEntity.super.onEntityPacketSent(consumer, packet);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        PolymerEntity.super.modifyRawTrackedData(data, player, initial);
        if (initial) {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TELEPORTATION_DURATION, 3));
        }
    }
}
