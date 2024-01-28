package me.lucko.helper.shadows.nbt2;

import me.lucko.helper.nbt2.ShadowTagType;
import me.lucko.helper.nbt2.ShadowTags;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.ShadowingStrategy;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings("unused")
@NmsClassTarget("nbt.NBTList")
@DefaultQualifier(NonNull.class)
public interface CollectionShadowTag<T extends ShadowTag> extends Shadow, ShadowTag {

    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_20_R3),
    })
    T set(int i, T tag); // the NMS explicitly overrides java.util.AbstractList

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3),
    })
    void add(int i, T tag); // the NMS explicitly overrides java.util.AbstractList

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3),
    })
    T remove(int i); // the NMS explicitly overrides java.util.AbstractList

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3),
    })
    boolean setTag(int index, ShadowTag element);

    @ObfuscatedTarget({
            @Mapping(value = "b", version = PackageVersion.v1_20_R3),
    })
    boolean addTag(int index, ShadowTag element);

    @ObfuscatedTarget({
            @Mapping(value = "f", version = PackageVersion.v1_20_R3),
    })
    byte elementTypeId();

    default ShadowTagType elementType() {
        return ShadowTagType.of(elementTypeId());
    }

    // TODO create FULL proxies for java.util.AbstractList

    ////// We implicitly override some basic methods of java.util.AbstractList

    boolean add(T e);

    @ShadowingStrategy(wrapper = ForShadowNbt0.class) // must explicitly shadow otherwise we can't cast T to subclasses of ShadowTag
    T get(int index);

    boolean contains(T e);

    boolean remove(T e);

    enum ForShadowNbt0 implements ShadowingStrategy.Wrapper {
        INSTANCE;

        @Override public Object wrap(@Nullable final Object unwrapped, @NonNull final Class<?> expectedType, @NonNull final ShadowFactory shadowFactory) {
            return ShadowTags.shadow(unwrapped);
        }
    }

    //////

}
