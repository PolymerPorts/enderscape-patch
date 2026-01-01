package eu.pb4.enderscapepatch.impl;

import java.util.IdentityHashMap;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

public interface DataTrackerHack {
    IdentityHashMap<EntityDataAccessor<Object>, SynchedEntityData.DataItem<Object>> enderscapepatch$getFakes();
}
