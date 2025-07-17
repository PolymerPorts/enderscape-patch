package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.enderscapepatch.impl.entity.model.DrifterModel;
import eu.pb4.enderscapepatch.impl.entity.model.DriftletModel;
import eu.pb4.enderscapepatch.impl.entity.model.RubblemiteModel;
import eu.pb4.enderscapepatch.impl.entity.model.RustleModel;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.PolyModelInstance;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.EntityModel;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.ModelPart;
import eu.pb4.enderscapepatch.impl.entity.model.emuvanilla.model.TexturedModelData;
import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.entity.rubblemite.RubblemiteVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public interface EntityModels {
    List<PolyModelInstance<?>> ALL = new ArrayList<>();
    PolyModelInstance<DrifterModel> DRIFTER = create(DrifterModel::new, DrifterModel.createLayer(), Enderscape.id("entity/drifter/drifter"));
    PolyModelInstance<DrifterModel> DRIFTER_WITH_JELLY = withTexture(DRIFTER, Enderscape.id("entity/drifter/drifter_with_jelly"));
    PolyModelInstance<DriftletModel> DRIFTLET = create(DriftletModel::new, DriftletModel.createLayer(), Enderscape.id("entity/drifter/driftlet"));

    EnumMap<RubblemiteVariant, PolyModelInstance<RubblemiteModel>> RUBBLEMITE = Util.make(new EnumMap<>(RubblemiteVariant.class), m -> {
        var instance = create(RubblemiteModel::new, RubblemiteModel.createLayer(), Enderscape.id("entity/rubblemite/" + RubblemiteVariant.END_STONE.asString()));
        m.put(RubblemiteVariant.END_STONE, instance);
        for (var variant : RubblemiteVariant.values()) {
            if (variant == RubblemiteVariant.END_STONE) continue;
            m.put(variant, withTexture(instance, Enderscape.id("entity/rubblemite/" + variant.asString())));
        }
    });
    PolyModelInstance<RustleModel> RUSTLE = create(RustleModel::new, RustleModel.createLayer(), Enderscape.id("entity/rustle/rustle"));

    static <T extends EntityModel<?>> PolyModelInstance<T> create(Function<ModelPart, T> modelCreator, TexturedModelData data, Identifier texture) {
        var instance = PolyModelInstance.create(modelCreator, data, texture);
        ALL.add(instance);
        return instance;
    }

    static <T extends EntityModel<?>> PolyModelInstance<T> withTexture(PolyModelInstance<T> original, Identifier texture) {
        var instance = original.withTexture(texture);
        ALL.add(instance);
        return instance;
    }
}
