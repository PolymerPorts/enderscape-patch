package eu.pb4.enderscapepatch.impl.entity.model;

import eu.pb4.enderscapepatch.impl.entity.model.rubblemite.RubblemiteModel;
import eu.pb4.enderscapepatch.impl.entity.model.rustle.BabyRustleModel;
import eu.pb4.enderscapepatch.impl.entity.model.rustle.RustleModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.PolyModelInstance;

import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.EntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.LayerDefinition;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.ModelPart;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.entity.rubblemite.RubblemiteVariant;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.penumbra.enderscape.registry.entity.EnderscapeEntities;
import net.penumbra.enderscape.registry.entity.EnderscapeRubblemiteVariants;

import java.util.*;
import java.util.function.Function;

public interface EntityModels {
    List<PolyModelInstance<?>> ALL = new ArrayList<>();
    PolyModelInstance<DrifterModel> DRIFTER = create(DrifterModel::new, DrifterModel.createDrifterLayer(), Enderscape.id("entity/drifter/drifter"));
    PolyModelInstance<DrifterModel> DRIFTER_WITH_JELLY = withTexture(DRIFTER, Enderscape.id("entity/drifter/drifter_with_jelly"));
    PolyModelInstance<DrifterModel> DRIFTLET = create(DrifterModel::new, DrifterModel.createDriftletLayer(), Enderscape.id("entity/drifter/driftlet"));

    Map<ResourceKey<RubblemiteVariant>, PolyModelInstance<RubblemiteModel>> RUBBLEMITE = Util.make(new HashMap<>(), m -> {
        var instance = create(RubblemiteModel::new, RubblemiteModel.createLayer(), EnderscapeRubblemiteVariants.DEFAULT.identifier().withPrefix("entity/rubblemite/"));
        m.put(EnderscapeRubblemiteVariants.DEFAULT, instance);
        for (var variant : EnderscapeRubblemiteVariants.RUBBLEMITE_VARIANTS) {
            m.put(variant, withTexture(instance, variant.identifier().withPrefix("entity/rubblemite/rubblemite_")));
        }
    });
    PolyModelInstance<RustleModel> RUSTLE = create(RustleModel::new, RustleModel.createLayer(), Enderscape.id("entity/rustle/rustle"));
    PolyModelInstance<BabyRustleModel> BABY_RUSTLE = create(BabyRustleModel::new, BabyRustleModel.createLayer(), Enderscape.id("entity/rustle/rustle_baby"));

    IdentityHashMap<EntityType<?>, PolyModelInstance<?>> BY_TYPE = Util.make(() -> {
        var m = new IdentityHashMap<EntityType<?>, PolyModelInstance<?>>();
        m.put(EnderscapeEntities.DRIFTER, EntityModels.DRIFTER);
        m.put(EnderscapeEntities.RUBBLEMITE, EntityModels.RUBBLEMITE.get(EnderscapeRubblemiteVariants.DEFAULT));
        m.put(EnderscapeEntities.RUSTLE, EntityModels.RUSTLE);
        return m;
    });

    static <T extends EntityModel<?>> PolyModelInstance<T> create(Function<ModelPart, T> modelCreator, LayerDefinition data, Identifier texture) {
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
