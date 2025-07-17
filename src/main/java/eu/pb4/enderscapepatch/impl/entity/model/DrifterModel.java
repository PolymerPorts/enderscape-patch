package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.*;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.Dilation;
import net.bunten.enderscape.entity.drifter.Drifter;
import net.minecraft.util.math.MathHelper;


public class DrifterModel extends EntityModel<Drifter> {
    private final ModelPart head;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart stem;
    private final ModelPart bell;
    private final ModelPart strandsN;
    private final ModelPart strandsW;
    private final ModelPart strandsS;
    private final ModelPart strandsE;

    public DrifterModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.leftLeg = this.head.getChild("leftLeg");
        this.rightLeg = this.head.getChild("rightLeg");
        this.stem = this.head.getChild("stem");
        this.bell = this.stem.getChild("bell");
        this.strandsN = this.bell.getChild("strandsN");
        this.strandsW = this.bell.getChild("strandsW");
        this.strandsS = this.bell.getChild("strandsS");
        this.strandsE = this.bell.getChild("strandsE");
    }

    public static TexturedModelData createLayer() {
        Dilation dilation = Dilation.NONE;
        ModelData data = new ModelData();
        ModelPartData rootData = data.getRoot();
        ModelPartData headData = rootData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 16.0F, 8.0F, dilation), ModelTransform.origin(0.0F, 10.0F, 0.0F));
        headData.addChild("leftLeg", ModelPartBuilder.create().uv(96, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, dilation), ModelTransform.origin(2.0F, 8.0F, 0.0F));
        headData.addChild("rightLeg", ModelPartBuilder.create().uv(96, 10).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, dilation), ModelTransform.origin(-2.0F, 8.0F, 0.0F));
        ModelPartData stemData = headData.addChild("stem", ModelPartBuilder.create().uv(96, 20).cuboid(-4.0F, -10.0F, 0.0F, 8.0F, 10.0F, 0.0F, dilation), ModelTransform.origin(0.0F, -8.0F, 0.0F));
        stemData.addChild("stem2", ModelPartBuilder.create().uv(96, 20).cuboid(-4.0F, -10.0F, 0.0F, 8.0F, 10.0F, 0.0F, dilation), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        ModelPartData bellData = stemData.addChild("bell", ModelPartBuilder.create().uv(0, 0).cuboid(-16.0F, -16.0F, -16.0F, 32.0F, 16.0F, 32.0F, dilation), ModelTransform.origin(0.0F, -10.0F, 0.0F));
        ModelPartBuilder strandBuilder = ModelPartBuilder.create().uv(0, 48).cuboid(-16.0F, 0.0F, 0.0F, 32.0F, 32.0F, 0.0F, dilation);
        bellData.addChild("strandsN", strandBuilder, ModelTransform.of(0.0F, 0.0F, -15.0F, 0.0F, 0.0F, 0.0F));
        bellData.addChild("strandsW", strandBuilder, ModelTransform.of(15.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        bellData.addChild("strandsS", strandBuilder, ModelTransform.of(0.0F, 0.0F, 15.0F, 0.0F, 3.1416F, 0.0F));
        bellData.addChild("strandsE", strandBuilder, ModelTransform.of(-15.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
        return TexturedModelData.of(data, 128, 80);
    }

    @Override
    public void setAngles(Drifter state) {
        super.setAngles(state);

        float age = state.age;
        float animPos = state.limbAnimator.getAnimationProgress(1);
        float animSpeed = state.limbAnimator.getAmplitude(1);
        this.head.yaw = (state.getHeadYaw() - state.getYaw()) * 0.017453292F;
        this.head.pitch = state.getPitch() * 0.017453292F + MathHelper.sin(age * 0.2F) * 0.1F;
        this.head.roll = 0.1F * MathHelper.sin(animPos * 0.8F) * 2.0F * animSpeed * 0.25F;
        ModelPart var10000 = this.head;
        var10000.pitch += 0.1F * MathHelper.sin(animPos * 0.8F) * 4.0F * animSpeed * 0.25F;
        this.stem.pitch = -this.head.pitch;
        this.bell.pitch = MathHelper.sin(age * 0.2F + 1.5707964F) * 0.1F;
        this.bell.roll = 0.1F * -(MathHelper.sin(animPos * 0.8F) * 3.0F * animSpeed * 0.5F);
        this.strandsN.pitch = -(this.head.pitch * 0.1F) + MathHelper.sin(age * 0.1F + 1.5707964F) * 0.3F;
        var10000 = this.strandsN;
        var10000.pitch += 0.2F * MathHelper.sin(animPos * 0.8F) * animSpeed * 0.5F;
        this.strandsW.pitch = this.strandsN.pitch;
        this.strandsS.pitch = this.strandsN.pitch;
        this.strandsE.pitch = this.strandsN.pitch;
        this.leftLeg.pitch = this.head.pitch / 2.0F + MathHelper.cos(animPos * 0.6662F + 1.5707964F) * 0.6F * animSpeed;
        this.rightLeg.pitch = this.head.pitch / 2.0F + MathHelper.cos(animPos * 0.6662F) * 0.6F * animSpeed;
        var10000 = this.leftLeg;
        var10000.pitch += MathHelper.sin(age * 0.2F) * 0.4F;
        var10000 = this.rightLeg;
        var10000.pitch += MathHelper.sin(age * 0.2F + 1.5707964F) * 0.4F;
        this.leftLeg.roll = -this.head.roll + 0.1F * MathHelper.sin(animPos * 0.4F + 1.5707964F) * 4.0F * animSpeed * 0.5F;
        this.rightLeg.roll = -this.head.roll + 0.1F * MathHelper.sin(animPos * 0.4F) * 4.0F * animSpeed * 0.5F;
    }
}
