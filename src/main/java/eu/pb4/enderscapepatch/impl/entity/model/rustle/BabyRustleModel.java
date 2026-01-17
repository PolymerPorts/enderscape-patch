package eu.pb4.enderscapepatch.impl.entity.model.rustle;

import eu.pb4.factorytools.api.virtualentity.emuvanilla2.animation.KeyframeAnimation;
import eu.pb4.factorytools.api.virtualentity.emuvanilla2.model.*;
import net.bunten.enderscape.entity.rustle.Rustle;
import net.minecraft.util.Mth;

public class BabyRustleModel extends EntityModel<Rustle> {
	private final ModelPart body;
	private final ModelPart rightAntenna;
	private final ModelPart leftAntenna;
	private final ModelPart frontSpines;
	private final ModelPart middleSpines;
	private final ModelPart backSpines;
	private final ModelPart tail;

	private final KeyframeAnimation sleepingAnimation;

	public BabyRustleModel(ModelPart root) {
		super(root);

		body = root.getChild("body");
		rightAntenna = body.getChild("rightAntenna");
		leftAntenna = body.getChild("leftAntenna");
		frontSpines = body.getChild("frontSpines");
		middleSpines = body.getChild("middleSpines");
		backSpines = body.getChild("backSpines");
		tail = body.getChild("tail");

		sleepingAnimation = RustleAnimations.SLEEPING_BABY.bake(root);
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -3.5F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        body.addOrReplaceChild("rightAntenna", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, -4.0F, -3.5F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -4.0F, -3.0F));
        body.addOrReplaceChild("leftAntenna", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, -4.0F, -3.5F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -4.0F, -3.0F));
        body.addOrReplaceChild("frontSpines", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -2.0F, 0.0F, 8.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -2.5F));
        body.addOrReplaceChild("middleSpines", CubeListBuilder.create().texOffs(0, 11).addBox(-5.0F, -4.5F, 0.0F, 10.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, -0.5F));
        body.addOrReplaceChild("backSpines", CubeListBuilder.create().texOffs(0, 11).addBox(-5.0F, -4.5F, 0.0F, 10.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 2.5F));
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 3.5F));

        return LayerDefinition.create(meshdefinition, 32, 32);
	}

    @Override
    public void setupAnim(Rustle state) {
		super.setupAnim(state);

        float age = state.tickCount;
		float animPos = state.walkAnimation.position();
		float animSpeed = state.walkAnimation.speed();

		if (state.isBaby()) animPos *= 0.5F;

		leftAntenna.yRot = Mth.sin(age + (animPos / 3) * 0.1F) * animSpeed * 0.3F;
		rightAntenna.yRot = Mth.sin(age + (animPos / 3) * 0.1F + Mth.HALF_PI) * animSpeed * 0.8F;

		leftAntenna.xRot = Mth.sin(age + (animPos / 3) * 0.1F + Mth.HALF_PI) * animSpeed * 0.8F;
		rightAntenna.xRot = Mth.sin(age + (animPos / 3) * 0.1F + (Mth.HALF_PI * 3)) * animSpeed * 0.8F;

		body.zRot = Mth.sin(age + (animPos / 3) * 0.03F) * animSpeed * 0.25F;
		tail.zRot = Mth.sin(age + (animPos / 3) * 0.06F) * animSpeed * 0.5F;

        frontSpines.yRot = Mth.sin(age + (animPos / 3) * 0.1F) * animSpeed * 0.8F;
        middleSpines.yRot = Mth.sin(age + (animPos / 3) * 0.1F + Mth.HALF_PI) * animSpeed * 0.8F;
        backSpines.yRot = Mth.sin(age + (animPos / 3) * 0.1F + Mth.PI) * animSpeed * 0.8F;

		sleepingAnimation.apply(state.sleepingAnimationState, age);
    }
}