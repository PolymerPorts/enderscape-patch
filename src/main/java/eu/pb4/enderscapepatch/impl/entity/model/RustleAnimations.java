package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.animation.*;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.animation.AnimationDefinition.Builder;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.animation.Transformation.*;

public class RustleAnimations {
    public static final AnimationDefinition SLEEPING;

    public RustleAnimations() {
    }

    static {
        SLEEPING = Builder.create(3.8333F).looping().addBoneAnimation("head", new Transformation(Targets.ROTATE, new Keyframe[]{new Keyframe(0.0F, AnimationHelper.createRotationalVector(30.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(1.5F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(2.5F, AnimationHelper.createRotationalVector(35.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(3.8333F, AnimationHelper.createRotationalVector(30.0F, 0.0F, 0.0F), Interpolations.LINEAR)})).addBoneAnimation("rightAntenna", new Transformation(Targets.ROTATE, new Keyframe[]{new Keyframe(0.0F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(0.875F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(1.4583F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(2.4583F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(3.8333F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Interpolations.CUBIC)})).addBoneAnimation("leftAntenna", new Transformation(Targets.ROTATE, new Keyframe[]{new Keyframe(0.0F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(0.875F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(1.4583F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(2.4583F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Interpolations.CUBIC), new Keyframe(3.8333F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Interpolations.CUBIC)})).addBoneAnimation("body", new Transformation(Targets.SCALE, new Keyframe[]{new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Interpolations.CUBIC), new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Interpolations.CUBIC), new Keyframe(1.5F, AnimationHelper.createScalingVector(1.024999976158142, 1.024999976158142, 1.024999976158142), Interpolations.CUBIC), new Keyframe(2.5F, AnimationHelper.createScalingVector(1.0, 1.0, 1.0), Interpolations.CUBIC)})).build();
    }
}