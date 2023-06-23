package me.lucko.helper.gson.typeadapters.immutable.immutable;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class ImmutableMapDeserializer extends BaseMapDeserializer<ImmutableMap<?, ?>> {

    @Override
    protected ImmutableMap<?, ?> buildFrom(Map<?, ?> map) {
        return ImmutableMap.copyOf(map);
    }

}
