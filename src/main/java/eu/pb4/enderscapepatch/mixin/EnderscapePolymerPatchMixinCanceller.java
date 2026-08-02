package eu.pb4.enderscapepatch.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class EnderscapePolymerPatchMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return mixinClassName.equals("net.penumbra.enderscape.mixin.block.NoteBlockInstrumentMixin")
                || mixinClassName.equals("net.penumbra.enderscape.client.mixin.MusicManagerMixin")
                ;
    }
}