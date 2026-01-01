package eu.pb4.enderscapepatch.mixin.mod;

import net.bunten.enderscape.registry.EnderscapeNoteBlockInstruments;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnderscapeNoteBlockInstruments.class)
public class EnderscapeNoteBlockInstrumentsMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public NoteBlockInstrument get() {
        return switch ((EnderscapeNoteBlockInstruments) (Object) this) {
            case SYNTH_BASS -> NoteBlockInstrument.BASS;
            case SYNTH_BELL -> NoteBlockInstrument.BELL;
        };
    }
}
