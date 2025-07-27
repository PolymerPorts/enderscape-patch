package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.factorytools.api.virtualentity.emuvanilla.EntityValueExtraction;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.*;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.Dilation;
import net.bunten.enderscape.entity.rubblemite.Rubblemite;
import net.minecraft.util.math.MathHelper;

public class RubblemiteModel extends EntityModel<Rubblemite> {
    private final ModelPart shell;
    private final ModelPart head;

    public RubblemiteModel(ModelPart root) {
        super(root);
        this.shell = root.getChild("shell");
        this.head = this.shell.getChild("head");
    }

    public static TexturedModelData createLayer() {
        Dilation dilation = Dilation.NONE;
        ModelData data = new ModelData();
        ModelPartData rootData = data.getRoot();
        ModelPartData shellData = rootData.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, dilation), ModelTransform.origin(0.0F, 24.0F, 0.0F));
        shellData.addChild("head", ModelPartBuilder.create().uv(0, 14).cuboid(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, dilation), ModelTransform.origin(0.0F, -2.0F, -4.0F));
        return TexturedModelData.of(data, 32, 32);
    }

    @Override
    public void setAngles(Rubblemite state) {
        super.setAngles(state);

        this.getRootPart().traverse().forEach(ModelPart::resetTransform);
        float age = state.age;
        float strength = 0.05F;
        float speed = 0.3F;
        float speed2 = speed * 2.0F;
        if (state.isDashing()) {
            this.shell.yaw = age;
        } else {
            this.head.pitch = -MathHelper.sin(age * speed) * strength;
            this.head.roll = -MathHelper.sin(age * speed2 + 1.5707964F) * strength;
            this.shell.pitch = MathHelper.sin(age * speed + 1.5707964F) * strength;
            this.shell.roll = MathHelper.sin(age * speed2) * strength;
            ModelPart var10000 = this.shell;
            var10000.pitch += state.getPitch() * 0.017453292F / 2.0F;
            var10000 = this.shell;
            var10000.yaw += EntityValueExtraction.getRelativeHeadYaw(state) * 0.017453292F / 2.0F;
        }

        this.head.visible = !state.isInsideShell() && !state.isDashing();
    }
}