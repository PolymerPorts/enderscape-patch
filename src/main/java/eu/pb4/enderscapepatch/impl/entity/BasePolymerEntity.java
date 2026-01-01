package eu.pb4.enderscapepatch.impl.entity;

import eu.pb4.enderscapepatch.impl.PacketHandler;
import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.PolyModelInstance;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.EntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.ScalingEntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.SimpleEntityModel;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.IdentifiedUniqueEntityAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.entity.rustle.Rustle;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Consumer;

public record BasePolymerEntity(LivingEntity entity) implements PolymerEntity {
    public static final Identifier MODEL = Enderscape.id("model");
    public BasePolymerEntity {
        var model = (PolyModelInstance<EntityModel<LivingEntity>>) EntityModels.BY_TYPE.get(entity.getType());
        if (model != null) {
            //noinspection unchecked
            IdentifiedUniqueEntityAttachment.ofTicking(MODEL, entity instanceof Rustle
                    ? new ScalingEntityModel<>(entity, model)
                    : new SimpleEntityModel<>(entity, model), entity);
        }
    }

    @Override
    public void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        if (packet instanceof ClientboundAnimatePacket) {
            return;
        }
        if (packet instanceof ClientboundSetPassengersPacket packet1 && packet1.getPassengers().length != 0) {
            var model = (SimpleEntityModel<?>) UniqueIdentifiableAttachment.get(entity, MODEL).holder();
            consumer.accept(VirtualEntityUtils.createRidePacket(entity.getId(), IntList.of(model.rideAttachment.getEntityId())));
            consumer.accept(VirtualEntityUtils.createRidePacket(model.rideAttachment.getEntityId(), packet1.getPassengers()));
            return;
        }

        if (packet instanceof ClientboundSetEntityLinkPacket packet1) {
            var model = (SimpleEntityModel<?>) UniqueIdentifiableAttachment.get(entity, MODEL).holder();
            consumer.accept(VirtualEntityUtils.createEntityAttachPacket(model.leadAttachment.getEntityId(), packet1.getDestId()));
            return;
        }

        if (packet instanceof ClientboundEntityEventPacket packet1 && PacketHandler.emulateHandleStatus(this.entity, packet1.getEventId())) {
            return;
        }

        PolymerEntity.super.onEntityPacketSent(consumer, packet);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        PolymerEntity.super.modifyRawTrackedData(data, player, initial);
        if (initial) {
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.TELEPORTATION_DURATION, 3));
        }
    }
}
