package me.lucko.helper.shadows.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.helper.nbt.ShadowTagType;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowingStrategy;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.CollectionTag")
@DefaultQualifier(NonNull.class)
public interface CollectionShadowTag<T extends ShadowTag> extends Shadow, ShadowTag {

    // the NMS explicitly overrides java.util.AbstractList
    @ObfuscatedTarget({
            @Mapping(value = "set", version = NmsVersion.v1_20_R4),
            @Mapping(value = "d", version = NmsVersion.v1_20_R3),
    })
    T set(int i, T tag);

    // the NMS explicitly overrides java.util.AbstractList
    @ObfuscatedTarget({
            @Mapping(value = "add", version = NmsVersion.v1_20_R4),
            @Mapping(value = "c", version = NmsVersion.v1_20_R3),
    })
    void add(int i, T tag);

    // the NMS explicitly overrides java.util.AbstractList
    @ObfuscatedTarget({
            @Mapping(value = "remove", version = NmsVersion.v1_20_R4),
            @Mapping(value = "c", version = NmsVersion.v1_20_R3),
    })
    T remove(int i);

    @ObfuscatedTarget({
            @Mapping(value = "setTag", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3),
    })
    boolean setTag(int index, ShadowTag element);

    @ObfuscatedTarget({
            @Mapping(value = "addTag", version = NmsVersion.v1_20_R4),
            @Mapping(value = "b", version = NmsVersion.v1_20_R3),
    })
    boolean addTag(int index, ShadowTag element);

    @ObfuscatedTarget({
            @Mapping(value = "getElementType", version = NmsVersion.v1_20_R4),
            @Mapping(value = "f", version = NmsVersion.v1_20_R3),
    })
    byte elementTypeId();

    default ShadowTagType elementType() {
        return ShadowTagType.of(elementTypeId());
    }

    //<editor-fold desc="AbstractList Proxies">
    boolean add(T e);

    // We must explicitly specify shadow strategy for this method,
    // otherwise it's always effectively shadowed as plain ShadowTag.
    // Consequently, we can't cast T to any subclasses of ShadowTag.
    @ShadowingStrategy(
            wrapper = NbtShadowingStrategy.ForShadowTags.class
    )
    T get(int index);

    boolean contains(T e);

    boolean remove(T e);

    int size();
    //</editor-fold>

}
