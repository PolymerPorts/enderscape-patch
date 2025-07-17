package eu.pb4.enderscapepatch.impl;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;

import java.util.IdentityHashMap;

public interface DataTrackerHack {
    IdentityHashMap<TrackedData<Object>, DataTracker.Entry<Object>> enderscapepatch$getFakes();
}
