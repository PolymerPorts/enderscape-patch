package eu.pb4.enderscapepatch.impl.entity.model.rubblemite;

import eu.pb4.factorytools.api.virtualentity.emuvanilla2.EntityValueExtraction;
import eu.pb4.factorytools.api.virtualentity.emuvanilla2.animation.KeyframeAnimation;
import eu.pb4.factorytools.api.virtualentity.emuvanilla2.model.*;
import net.bunten.enderscape.entity.rubblemite.Rubblemite;
import net.minecraft.util.Mth;

public class RubblemiteModel extends EntityModel<Rubblemite> {
    private final ModelPart shell;
    private final ModelPart head;

    private final KeyframeAnimation dashAnimation;
    private final KeyframeAnimation prepareDashAnimation;
    private final KeyframeAnimation insideShellAnimation;

    public RubblemiteModel(ModelPart root) {
        super(root);
        shell = root.getChild("shell");
        head = shell.getChild("head");

        dashAnimation = RubblemiteAnimations.DASH.bake(root);
        prepareDashAnimation = RubblemiteAnimations.PREPARE_DASH.bake(root);
        insideShellAnimation = RubblemiteAnimations.INSIDE_SHELL.bake(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition data = new MeshDefinition();
        PartDefinition rootData = data.getRoot();

        PartDefinition shell = rootData.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.5F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.5F, 0.0F));
        shell.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -4.0F));

        return LayerDefinition.create(data, 32, 32);
    }

    @Override
    public void setupAnim(Rubblemite state) {
        super.setupAnim(state);

        float age = state.tickCount;
        float animPos = state.walkAnimation.position();
        float animSpeed = state.walkAnimation.speed();

        if (state.deathTime > 0) {
            head.zRot = Mth.lerp(0.1F, head.zRot, 0);
            shell.xRot = Mth.lerp(0.1F, head.zRot, 0);
            shell.yRot = Mth.lerp(0.1F, head.zRot, 0);
            shell.zRot = Mth.lerp(0.1F, head.zRot, 0);
        } else {
            head.zRot = Mth.sin(age + (animPos / 3) * 0.06F) * animSpeed * 0.1F;

            shell.xRot += (state.getXRot() * (Mth.PI / 180)) / 2;
            shell.yRot += (EntityValueExtraction.getRelativeHeadYaw(state) * (Mth.PI / 180)) / 2;
            shell.zRot = Mth.sin(age + (animPos / 3) * 0.03F + Mth.HALF_PI) * animSpeed * 0.15F;
        }

        dashAnimation.apply(state.dashAnimationState, age);
        prepareDashAnimation.apply(state.prepareDashAnimationState, age);
        insideShellAnimation.apply(state.insideShellAnimationState, age);
    }
}