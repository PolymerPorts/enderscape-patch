package eu.pb4.enderscapepatch.impl.entity;

import eu.pb4.enderscapepatch.impl.entity.model.EntityModels;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.PolyModelInstance;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.EntityModel;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.ModelPart;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.MobAnchorElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import net.bunten.enderscape.entity.rubblemite.RubblemiteVariant;
import net.bunten.enderscape.registry.EnderscapeEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.IdentityHashMap;
import java.util.Map;

public class VanillishEntityModel<T extends Entity> extends ElementHolder {
    public static final IdentityHashMap<EntityType<?>, PolyModelInstance<?>> BY_TYPE = Util.make(() -> {
        var m = new IdentityHashMap<EntityType<?>, PolyModelInstance<?>>();
        m.put(EnderscapeEntities.DRIFTER, EntityModels.DRIFTER);
        m.put(EnderscapeEntities.DRIFTLET, EntityModels.DRIFTLET);
        m.put(EnderscapeEntities.RUBBLEMITE, EntityModels.RUBBLEMITE.get(RubblemiteVariant.END_STONE));
        m.put(EnderscapeEntities.RUSTLE, EntityModels.RUSTLE);
        return m;
    });

    private static final Matrix4fStack STACK = new Matrix4fStack(64);
    private final Map<ModelPart, ItemDisplayElement> elements = new IdentityHashMap<>();
    private final T entity;
    protected final InteractionElement interaction;
    protected final LeadAttachmentElement leadAttachment = new LeadAttachmentElement();
    private PolyModelInstance<EntityModel<T>> model;
    private boolean hurt = false;

    private boolean noTick = true;

    public VanillishEntityModel(T entity, PolyModelInstance<EntityModel<T>> model) {
        this.entity = entity;
        var interaction = VirtualElement.InteractionHandler.redirect(entity);
        this.interaction = new InteractionElement(interaction);
        this.setModel(model);
        this.interaction.setSendPositionUpdates(false);
        this.leadAttachment.setOffset(new Vec3d(0, entity.getHeight() / 2, 0));
        this.leadAttachment.setInteractionHandler(interaction);
        this.addElement(leadAttachment);
        this.addPassengerElement(this.interaction);
    }

    public <X extends EntityModel<T>> void setModel(PolyModelInstance<X> model) {
        if (this.model == model) {
            return;
        }

        var oldElements = new IdentityHashMap<>(this.elements);
        this.elements.clear();

        for (var part : model.model().getParts()) {
            var stack = model.modelParts().apply(part);
            if (stack != null) {
                var element = oldElements.get(part);
                if (element == null) {
                    element = ItemDisplayElementUtil.createSimple(stack);
                    element.setDisplaySize(this.entity.getWidth() * 2, this.entity.getHeight() * 2);
                    element.setInterpolationDuration(1);
                    element.setTeleportDuration(3);
                    element.setViewRange(2);
                    element.setOffset(new Vec3d(0, 0.1, 0));
                } else {
                    element.setItem(stack);
                    oldElements.remove(part);
                }
                this.elements.put(part, element);
                this.addElement(element);
            }
        }
        for (var old : oldElements.values()) {
            this.removeElement(old);
        }

        this.model = (PolyModelInstance<EntityModel<T>>) model;

        if (!noTick) {
            this.tick();
        }
    }

    private static float getYaw(Direction direction) {
        return switch (direction) {
            case SOUTH -> 90.0F;
            case WEST -> 0.0F;
            case NORTH -> 270.0F;
            case EAST -> 180.0F;
            default -> 0.0F;
        };
    }

    @Override
    public boolean startWatching(ServerPlayNetworkHandler player) {
        if (noTick) {
            onTick();
        }
        return super.startWatching(player);
    }

    @Override
    protected void onTick() {
        noTick = false;
        this.interaction.setSize(entity.getWidth(), entity.getHeight());
        this.interaction.setCustomName(this.entity.getCustomName());
        this.interaction.setCustomNameVisible(this.entity.isCustomNameVisible());

        STACK.pushMatrix();
        STACK.translate(0.0F, -0.1f, 0.0F);

        if (entity instanceof LivingEntity livingEntity) {
            var hurt = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
            if (this.hurt != hurt) {
                this.hurt = hurt;

                var map = hurt ? this.model.damagedModelParts() : this.model.modelParts();

                for (var entry : elements.entrySet()) {
                    entry.getValue().setItem(map.apply(entry.getKey()));
                }
            }

            if (entity.isInPose(EntityPose.SLEEPING)) {
                Direction direction = livingEntity.getSleepingDirection();
                if (direction != null) {
                    float f = livingEntity.getStandingEyeHeight() - 0.1F;
                    STACK.translate((float) (-direction.getOffsetX()) * f, 0.0F, (float) (-direction.getOffsetZ()) * f);
                }
            }

            float g = livingEntity.getScale();
            STACK.scale(g);
            this.setupTransforms(livingEntity, STACK, livingEntity.getBodyYaw(), g);
            STACK.scale(-1.0F, -1.0F, 1.0F);
            STACK.scale(livingEntity.getScaleFactor());
            //this.scale(livingEntityRenderState, matrixStack);
            STACK.translate(0.0F, -1.501F, 0.0F);
        }

        this.model.model().setAngles(this.entity);
        this.model.model().render(STACK, this::updateElement);
        STACK.popMatrix();


        super.onTick();
    }

    private void updateElement(ModelPart part, Matrix4f matrix4f, boolean hidden) {
        var element = this.elements.get(part);
        if (element == null) {
            return;
        }

        if (hidden) {
            this.removeElement(element);
        } else {
            element.setTransformation(matrix4f);
            element.startInterpolationIfDirty();
            this.addElement(element);
        }
    }

    protected void setupTransforms(LivingEntity entity, Matrix4fStack matrices, float bodyYaw, float baseHeight) {
        if (entity.isFrozen()) {
            bodyYaw += (float) (Math.cos((float) MathHelper.floor(entity.age) * 3.25F) * Math.PI * 0.4000000059604645);
        }

        if (!entity.isInPose(EntityPose.SLEEPING)) {
            matrices.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
        }

        if (entity.deathTime > 0.0F) {
            float f = (entity.deathTime - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrices.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(f * 90));
        } else if (entity.isUsingRiptide()) {
            matrices.rotate(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - entity.getPitch()));
            matrices.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(entity.age * -75.0F));
        } else if (entity.isInPose(EntityPose.SLEEPING)) {
            Direction direction = entity.getSleepingDirection();
            float g = direction != null ? getYaw(direction) : bodyYaw;
            matrices.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(90));
            matrices.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
        } else {
            var name = entity.getDisplayName().getString();
            if ("Dinnerbone".equals(name) || "Grumm".equals(name)) {
                matrices.translate(0.0F, (entity.getHeight() + 0.1F) / baseHeight, 0.0F);
                matrices.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            }
        }

    }
}
