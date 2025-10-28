package eu.pb4.enderscapepatch.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class EnderscapePolymerPatchMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return mixinClassName.equals("net.bunten.enderscape.mixin.LivingEntityMixin")
                || mixinClassName.equals("net.bunten.enderscape.mixin.NoteBlockInstrumentMixin")
                || mixinClassName.equals("net.bunten.enderscape.mixin.ItemEntityMixin")
                || mixinClassName.equals("net.bunten.enderscape.mixin.ExperienceOrbMixin")
                || mixinClassName.equals("net.bunten.enderscape.mixin.AbstractMinecartMixin")
                || mixinClassName.equals("net.bunten.enderscape.client.mixin.MusicManagerMixin")
                ;
    }
}