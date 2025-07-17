package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.animation.Animation;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.*;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.Dilation;
import net.bunten.enderscape.entity.rustle.Rustle;
import net.minecraft.util.math.MathHelper;

public class RustleModel extends EntityModel<Rustle> {
    private final ModelPart body;
    private final ModelPart crossSpines;
    private final ModelPart middleSpines;
    private final ModelPart backSpines;
    private final ModelPart head;
    private final ModelPart rightAntenna;
    private final ModelPart leftAntenna;
    private final ModelPart frontSpines;
    private final Animation sleepingAnimation;

    public RustleModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.crossSpines = this.body.getChild("crossSpines");
        this.middleSpines = this.body.getChild("middleSpines");
        this.backSpines = this.body.getChild("backSpines");
        this.head = root.getChild("head");
        this.rightAntenna = this.head.getChild("rightAntenna");
        this.leftAntenna = this.head.getChild("leftAntenna");
        this.frontSpines = this.head.getChild("frontSpines");
        this.sleepingAnimation = RustleAnimations.SLEEPING.createAnimation(root);
    }

    public static TexturedModelData createLayer() {
        ModelData meshdefinition = new ModelData();
        ModelPartData partdefinition = meshdefinition.getRoot();
        ModelPartData body = partdefinition.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -2.5F, -5.0F, 8.0F, 5.0F, 10.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 21.5F, 2.0F));
        ModelPartData crossSpines = body.addChild("crossSpines", ModelPartBuilder.create().uv(0, 27).cuboid(-7.0F, -2.5F, 0.0F, 14.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
        crossSpines.addChild("cube_r1", ModelPartBuilder.create().uv(0, 27).cuboid(-7.0F, -1.0F, 0.0F, 14.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 1.5708F, 0.0F));
        body.addChild("middleSpines", ModelPartBuilder.create().uv(0, 20).cuboid(-7.0F, -4.0F, 0.0F, 14.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -0.5F, -2.0F));
        body.addChild("backSpines", ModelPartBuilder.create().uv(0, 20).cuboid(-7.0F, -4.0F, 0.0F, 14.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -0.5F, 3.0F));
        ModelPartData head = partdefinition.addChild("head", ModelPartBuilder.create().uv(26, 0).cuboid(-3.0F, -2.5F, -3.0F, 6.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 21.5F, -4.0F));
        head.addChild("rightAntenna", ModelPartBuilder.create().uv(46, -7).cuboid(0.0F, -7.0F, -5.5F, 0.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.origin(-2.0F, -2.5F, -1.5F));
        head.addChild("leftAntenna", ModelPartBuilder.create().uv(46, -7).cuboid(0.0F, -7.0F, -5.5F, 0.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.origin(2.0F, -2.5F, -1.5F));
        head.addChild("frontSpines", ModelPartBuilder.create().uv(0, 15).cuboid(-7.0F, -2.0F, 0.0F, 14.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -0.5F, -2.0F));
        return TexturedModelData.of(meshdefinition, 64, 32);
    }


    @Override
    public void setAngles(Rustle state) {
        super.setAngles(state);

        float age = state.age;
        float animPos = state.limbAnimator.getAnimationProgress(1);
        float animSpeed = state.limbAnimator.getAmplitude(1);
        this.leftAntenna.yaw = MathHelper.sin(age + animPos / 3.0F * 0.1F) * animSpeed * 0.3F;
        this.rightAntenna.yaw = MathHelper.sin(age + animPos / 3.0F * 0.1F + 1.5707964F) * animSpeed * 0.8F;
        this.leftAntenna.pitch = MathHelper.sin(age + animPos / 3.0F * 0.1F + 1.5707964F) * animSpeed * 0.8F;
        this.rightAntenna.pitch = MathHelper.sin(age + animPos / 3.0F * 0.1F + 4.712389F) * animSpeed * 0.8F;
        ModelPart var10000 = this.head;
        var10000.pitch += state.getPitch() * 0.017453292F / 2.0F;
        var10000 = this.head;
        var10000.yaw += (state.getHeadYaw() - state.getYaw()) * 0.017453292F / 2.0F;
        this.head.roll = MathHelper.sin(age + animPos / 3.0F * 0.06F) * animSpeed * 0.5F;
        this.body.roll = MathHelper.sin(age + animPos / 3.0F * 0.03F) * animSpeed * 0.25F;
        this.frontSpines.yaw = MathHelper.sin(age + animPos / 3.0F * 0.1F) * animSpeed * 0.8F;
        this.middleSpines.yaw = MathHelper.sin(age + animPos / 3.0F * 0.1F + 1.5707964F) * animSpeed * 0.8F;
        this.backSpines.yaw = MathHelper.sin(age + animPos / 3.0F * 0.1F + 3.1415927F) * animSpeed * 0.8F;
        this.sleepingAnimation.apply(state.sleepingAnimationState, age);
        this.head.xScale = state.isBaby() ? 1.25F : 1.0F;
        this.head.yScale = state.isBaby() ? 1.25F : 1.0F;
        this.head.zScale = state.isBaby() ? 1.25F : 1.0F;
        this.crossSpines.visible = !state.isBaby() && !state.isSheared();
    }
}