package me.lucko.helper.shadows.nbt;

import me.lucko.helper.nbt.ShadowTags;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.ShadowingStrategy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

interface NbtShadowingStrategy {
    /**
     * A wrapper for a single NBT object.
     */
    enum SingleWrapper implements ShadowingStrategy.Wrapper {
        INSTANCE;

        @Override public ShadowTag wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            Objects.requireNonNull(unwrapped);
            return ShadowTags.shadow(unwrapped);
        }
    }

    /**
     * A wrapper for an <b>immutable</b> list of NBT object.
     */
    enum ImmutableListTagWrapper implements ShadowingStrategy.Wrapper {
        INSTANCE;

        @SuppressWarnings("unchecked")
        @Override public List<ShadowTag> wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            Objects.requireNonNull(unwrapped);
            List<Object> listTag = (List<Object>) unwrapped;
            return listTag.stream().map(ShadowTags::shadow).toList();
        }
    }

    /**
     * A wrapper for an unsafe <b>mutable</b> map.
     * <p>
     * This is only intended to expose the typeless methods of the shadow map,
     * such as {@link Map#clear()} and {@link Map#isEmpty()}.
     */
    enum UnsafeMutableMapWrapper implements ShadowingStrategy.Wrapper {
        INSTANCE;

        @SuppressWarnings("unchecked")
        @Override public Map<Object, Object> wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            Objects.requireNonNull(unwrapped);
            return (Map<Object, Object>) unwrapped;
        }
    }
}
