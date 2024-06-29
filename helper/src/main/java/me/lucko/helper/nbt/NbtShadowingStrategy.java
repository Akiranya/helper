package me.lucko.helper.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.ShadowingStrategy;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;

interface NbtShadowingStrategy {
    /**
     * A wrapper/unwrapper for a single Tag object.
     */
    enum ForShadowTags implements ShadowingStrategy.Wrapper, ShadowingStrategy.Unwrapper {
        INSTANCE;

        @Override public Tag wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            if (unwrapped == null) {
                return null;
            }

            return ShadowTags.shadow(unwrapped);
        }

        @Override public @Nullable Object unwrap(@Nullable final Object wrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            if (wrapped instanceof final Shadow shadow) {
                return shadow.getShadowTarget();
            }

            return wrapped;
        }

        @Override public @NonNull Class<?> unwrap(final Class<?> wrappedClass, @NonNull final ShadowFactory shadowFactory) {
            return BukkitShadowFactory.global().targetClass(wrappedClass);
        }
    }

    /**
     * A wrapper/unwrapper for a List of Tag objects.
     * <p>
     * The shadow List is immutable. The shadow Tag in the List, however, is not.
     */
    @SuppressWarnings("unchecked")
    enum ForImmutableListTags implements ShadowingStrategy.Wrapper, ShadowingStrategy.Unwrapper {
        INSTANCE;

        @Override public List<Tag> wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            if (unwrapped == null) {
                return null;
            }

            List<Object> nmsListTag = (List<Object>) unwrapped;
            List<Tag> shadowListTag = nmsListTag.stream().map(ShadowTags::shadow).toList();
            return shadowListTag;
        }

        @Override public @Nullable Object unwrap(@Nullable final Object wrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            throw new UnsupportedOperationException();
        }

        @Override public @NonNull Class<?> unwrap(final Class<?> wrappedClass, @NonNull final ShadowFactory shadowFactory) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * A wrapper/unwrapper for a Map of Tag objects.
     * <p>
     * The shadow Map is a view of its shadow target - any changes on the shadow Map
     * will reflect on its shadow target.
     * <p>
     * This shadowing strategy is only intended to expose the typeless methods of the shadow map,
     * such as {@link Map#clear()} and {@link Map#isEmpty()}.
     */
    @SuppressWarnings("unchecked")
    enum ForTypelessMaps implements ShadowingStrategy.Wrapper, ShadowingStrategy.Unwrapper {
        INSTANCE;

        @Override public Map<Object, Object> wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            if (unwrapped == null) {
                return null;
            }

            return (Map<Object, Object>) unwrapped;
        }

        @Override public @Nullable Object unwrap(@Nullable final Object wrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            throw new UnsupportedOperationException();
        }

        @Override public @NonNull Class<?> unwrap(final Class<?> wrappedClass, @NonNull final ShadowFactory shadowFactory) {
            throw new UnsupportedOperationException();
        }
    }
}
