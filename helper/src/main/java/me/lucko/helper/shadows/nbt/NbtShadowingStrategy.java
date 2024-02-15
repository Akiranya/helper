package me.lucko.helper.shadows.nbt;

import me.lucko.helper.nbt.ShadowTags;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.ShadowingStrategy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Objects;

interface NbtShadowingStrategy {
    enum SingleWrapper implements ShadowingStrategy.Wrapper {
        INSTANCE;

        @Override public Object wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            return ShadowTags.shadow(unwrapped);
        }
    }

    enum ListWrapper implements ShadowingStrategy.Wrapper {
        INSTANCE;

        @SuppressWarnings("unchecked")
        @Override public Object wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            Objects.requireNonNull(unwrapped);
            List<Object> listTag = (List<Object>) unwrapped;
            return listTag.stream().map(ShadowTags::shadow).toList();
        }
    }
}
