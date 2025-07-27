package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.factorytools.api.virtualentity.emuvanilla.PolyModelInstance;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.EntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.ModelPart;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.TexturedModelData;
import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.entity.rubblemite.RubblemiteVariant;
import net.bunten.enderscape.registry.EnderscapeEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
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

    IdentityHashMap<EntityType<?>, PolyModelInstance<?>> BY_TYPE = Util.make(() -> {
        var m = new IdentityHashMap<EntityType<?>, PolyModelInstance<?>>();
        m.put(EnderscapeEntities.DRIFTER, EntityModels.DRIFTER);
        m.put(EnderscapeEntities.DRIFTLET, EntityModels.DRIFTLET);
        m.put(EnderscapeEntities.RUBBLEMITE, EntityModels.RUBBLEMITE.get(RubblemiteVariant.END_STONE));
        m.put(EnderscapeEntities.RUSTLE, EntityModels.RUSTLE);
        return m;
    });

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
