package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.factorytools.api.virtualentity.emuvanilla.EntityValueExtraction;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.*;
import net.bunten.enderscape.entity.drifter.Driftlet;
import net.minecraft.util.Mth;

public class DriftletModel extends EntityModel<Driftlet> {
    private final ModelPart head;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart stem;
    private final ModelPart bell;
    private final ModelPart strandsN;
    private final ModelPart strandsW;
    private final ModelPart strandsS;
    private final ModelPart strandsE;

    public DriftletModel(ModelPart root) {
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
        ModelPartData headData = rootData.addChild("head", ModelPartBuilder.create().uv(32, 24).cuboid(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F, dilation), ModelTransform.origin(0.0F, 20.0F, 0.0F));
        headData.addChild("leftLeg", ModelPartBuilder.create().uv(48, 7).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, dilation), ModelTransform.origin(1.5F, 0.0F, -0.5F));
        headData.addChild("rightLeg", ModelPartBuilder.create().uv(48, 0).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, dilation), ModelTransform.origin(-1.5F, 0.0F, -0.5F));
        ModelPartData stemData = headData.addChild("stem", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 4.0F, dilation), ModelTransform.origin(0.0F, -7.0F, 0.0F));
        ModelPartData bellData = stemData.addChild("bell", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F, dilation), ModelTransform.origin(0.0F, -2.0F, 0.0F));
        ModelPartBuilder strandBuilder = ModelPartBuilder.create().uv(0, 24).cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 12.0F, 0.0F, dilation);
        bellData.addChild("strandsN", strandBuilder, ModelTransform.of(0.0F, 0.0F, -7.0F, 0.0F, 0.0F, 0.0F));
        bellData.addChild("strandsW", strandBuilder, ModelTransform.of(7.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        bellData.addChild("strandsS", strandBuilder, ModelTransform.of(0.0F, 0.0F, 7.0F, 0.0F, 3.1416F, 0.0F));
        bellData.addChild("strandsE", strandBuilder, ModelTransform.of(-7.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
        return TexturedModelData.of(data, 64, 48);
    }

    @Override
    public void setAngles(Driftlet state) {
        super.setAngles(state);
        float age = state.tickCount;
        float animPos = state.walkAnimation.position(1);
        float animSpeed = state.walkAnimation.speed(1);
        animPos = (float)((double)animPos * 0.5);
        animSpeed = (float)((double)animSpeed * 0.5);
        this.head.yaw = EntityValueExtraction.getRelativeHeadYaw(state) * 0.017453292F;
        this.head.pitch = state.getXRot() * 0.017453292F + Mth.sin(age * 0.2F) * 0.1F;
        this.head.roll = 0.1F * Mth.sin(animPos * 0.8F) * 2.0F * animSpeed * 0.25F;
        ModelPart var10000 = this.head;
        var10000.pitch += 0.1F * Mth.sin(animPos * 0.8F) * 4.0F * animSpeed * 0.25F;
        this.stem.pitch = -this.head.pitch;
        this.bell.pitch = Mth.sin(age * 0.2F + 1.5707964F) * 0.1F;
        this.bell.roll = 0.1F * -(Mth.sin(animPos * 0.8F) * 3.0F * animSpeed * 0.5F);
        this.strandsN.pitch = -(this.head.pitch * 0.1F) + Mth.sin(age * 0.1F + 1.5707964F) * 0.3F;
        var10000 = this.strandsN;
        var10000.pitch += 0.2F * Mth.sin(animPos * 0.8F) * animSpeed * 0.5F;
        this.strandsW.pitch = this.strandsN.pitch;
        this.strandsS.pitch = this.strandsN.pitch;
        this.strandsE.pitch = this.strandsN.pitch;
        this.leftLeg.pitch = this.head.pitch / 2.0F + Mth.cos(animPos * 0.6662F + 1.5707964F) * 0.6F * animSpeed;
        this.rightLeg.pitch = this.head.pitch / 2.0F + Mth.cos(animPos * 0.6662F) * 0.6F * animSpeed;
        var10000 = this.leftLeg;
        var10000.pitch += Mth.sin(age * 0.2F) * 0.4F;
        var10000 = this.rightLeg;
        var10000.pitch += Mth.sin(age * 0.2F + 1.5707964F) * 0.4F;
        this.leftLeg.roll = -this.head.roll + 0.1F * Mth.sin(animPos * 0.4F + 1.5707964F) * 4.0F * animSpeed * 0.5F;
        this.rightLeg.roll = -this.head.roll + 0.1F * Mth.sin(animPos * 0.4F) * 4.0F * animSpeed * 0.5F;
    }
}