package me.lucko.helper.gson.typeadapters.immutable.immutable;

import com.google.common.collect.ImmutableSortedSet;

import java.util.Collection;

public class ImmutableSortedSetDeserializer extends BaseCollectionDeserializer<ImmutableSortedSet<?>> {

    @Override
    protected ImmutableSortedSet<?> buildFrom(Collection<?> collection) {
        return ImmutableSortedSet.copyOf(collection);
    }

}
