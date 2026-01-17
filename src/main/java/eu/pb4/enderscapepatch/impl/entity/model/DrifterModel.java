package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.factorytools.api.virtualentity.emuvanilla2.EntityValueExtraction;
import eu.pb4.factorytools.api.virtualentity.emuvanilla2.model.*;
import net.bunten.enderscape.entity.drifter.Drifter;
import net.minecraft.util.Mth;


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
        head = root.getChild("head");
        leftLeg = head.getChild("leftLeg");
        rightLeg = head.getChild("rightLeg");
        stem = head.getChild("stem");
        bell = stem.getChild("bell");
        strandsN = bell.getChild("strandsN");
        strandsW = bell.getChild("strandsW");
        strandsS = bell.getChild("strandsS");
        strandsE = bell.getChild("strandsE");
    }

    public static LayerDefinition createDrifterLayer() {
        CubeDeformation dilation = CubeDeformation.NONE;

        MeshDefinition data = new MeshDefinition();
        PartDefinition rootData = data.getRoot();

        PartDefinition headData = rootData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4, -8, -4, 8, 16, 8, dilation), PartPose.offset(0, 10, 0));

        headData.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(96, 0).addBox(-2, 0, -2, 4, 6, 4, dilation), PartPose.offset(2, 8, 0));
        headData.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(96, 10).addBox(-2, 0, -2, 4, 6, 4, dilation), PartPose.offset(-2, 8, 0));

        PartDefinition stemData = headData.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(96, 20).addBox(-4, -10, 0, 8, 10, 0, dilation), PartPose.offset(0, -8, 0));
        stemData.addOrReplaceChild("stem2", CubeListBuilder.create().texOffs(96, 20).addBox(-4, -10, 0, 8, 10, 0, dilation), PartPose.offsetAndRotation(0, 0, 0, 0, -1.5708F, 0));

        PartDefinition bellData = stemData.addOrReplaceChild("bell", CubeListBuilder.create().texOffs(0, 0).addBox(-16, -16, -16, 32, 16, 32, dilation), PartPose.offset(0, -10, 0));
        CubeListBuilder strandBuilder = CubeListBuilder.create().texOffs(0, 48).addBox(-16, 0, 0, 32, 32, 0, dilation);

        bellData.addOrReplaceChild("strandsN", strandBuilder, PartPose.offsetAndRotation(0, 0, -15, 0, 0, 0));
        bellData.addOrReplaceChild("strandsW", strandBuilder, PartPose.offsetAndRotation(15, 0, 0, 0, -1.5708F, 0));
        bellData.addOrReplaceChild("strandsS", strandBuilder, PartPose.offsetAndRotation(0, 0, 15, 0, 3.1416F, 0));
        bellData.addOrReplaceChild("strandsE", strandBuilder, PartPose.offsetAndRotation(-15, 0, 0, 0, 1.5708F, 0));

        return LayerDefinition.create(data, 128, 80);
    }

    public static LayerDefinition createDriftletLayer() {
        CubeDeformation dilation = CubeDeformation.NONE;

        MeshDefinition data = new MeshDefinition();
        PartDefinition rootData = data.getRoot();

        PartDefinition headData = rootData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 24).addBox(-4, -7, -4, 8, 7, 8, dilation), PartPose.offset(0, 20, 0));

        headData.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(48, 7).addBox(-1.5F, 0, -1.5F, 3, 4, 3, dilation), PartPose.offset(1.5F, 0, -0.5F));
        headData.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(48, 0).addBox(-1.5F, 0, -1.5F, 3, 4, 3, dilation), PartPose.offset(-1.5F, 0, -0.5F));

        PartDefinition stemData = headData.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(0, 0).addBox(-2, -6, -2, 4, 6, 4, dilation), PartPose.offset(0, -7, 0));
        PartDefinition bellData = stemData.addOrReplaceChild("bell", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -8, -8, 16, 8, 16, dilation), PartPose.offset(0, -2, 0));
        CubeListBuilder strandBuilder = CubeListBuilder.create().texOffs(0, 24).addBox(-8, 0, 0, 16, 12, 0, dilation);

        bellData.addOrReplaceChild("strandsN", strandBuilder, PartPose.offsetAndRotation(0, 0, -7, 0, 0, 0));
        bellData.addOrReplaceChild("strandsW", strandBuilder, PartPose.offsetAndRotation(7, 0, 0, 0, -1.5708F, 0));
        bellData.addOrReplaceChild("strandsS", strandBuilder, PartPose.offsetAndRotation(0, 0, 7, 0, 3.1416F, 0));
        bellData.addOrReplaceChild("strandsE", strandBuilder, PartPose.offsetAndRotation(-7, 0, 0, 0, 1.5708F, 0));

        return LayerDefinition.create(data, 64, 48);
    }

    @Override
    public void setupAnim(Drifter state) {
        float age = state.tickCount;
        float animPos = state.walkAnimation.position();
        float animSpeed = state.walkAnimation.speed();

        if (state.isBaby()) animPos *= 0.5F;

        head.yRot = (EntityValueExtraction.getRelativeHeadYaw(state) * 0.017453292F);
        head.xRot = (state.getXRot() * 0.017453292F) + (Mth.sin(age * 0.2F) * 0.1F);
        head.zRot = 0.1F * Mth.sin(animPos * 0.8F) * 2 * (animSpeed * 0.25F);
        head.xRot += 0.1F * Mth.sin(animPos * 0.8F) * 4 * (animSpeed * 0.25F);

        stem.xRot = -head.xRot;
        bell.xRot = (Mth.sin(age * 0.2F + Mth.HALF_PI) * 0.1F);
        bell.zRot = 0.1F * -(Mth.sin(animPos * 0.8F) * 3 * (animSpeed * 0.5F));

        strandsN.xRot = -(head.xRot * 0.1F) + (Mth.sin(age * 0.1F + Mth.HALF_PI) * 0.3F);

        strandsN.xRot += 0.2F * Mth.sin(animPos * 0.8F) * (animSpeed * 0.5F);

        strandsW.xRot = strandsN.xRot;
        strandsS.xRot = strandsN.xRot;
        strandsE.xRot = strandsN.xRot;

        leftLeg.xRot = (head.xRot / 2) + Mth.cos(animPos * 0.6662F + (Mth.PI / 2)) * 0.6F * animSpeed;
        rightLeg.xRot = (head.xRot / 2) + Mth.cos(animPos * 0.6662F) * 0.6F * animSpeed;

        leftLeg.xRot += Mth.sin(age * 0.2F) * 0.4F;
        rightLeg.xRot += Mth.sin(age * 0.2F + Mth.HALF_PI) * 0.4F;

        leftLeg.zRot = -head.zRot + 0.1F * Mth.sin(animPos * 0.4F + (Mth.PI / 2)) * 4 * (animSpeed * 0.5F);
        rightLeg.zRot = -head.zRot + 0.1F * Mth.sin(animPos * 0.4F) * 4 * (animSpeed * 0.5F);
    }
}
