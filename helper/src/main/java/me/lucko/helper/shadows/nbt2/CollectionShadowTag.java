package me.lucko.helper.shadows.nbt2;

import me.lucko.helper.nbt2.ShadowTagType;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public interface CollectionShadowTag<T extends ShadowTag> extends Shadow, ShadowTag, Iterable<T>, Collection<T>, List<T> {

    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_20_R3),
    })
    T set(int i, T tag); // overrides by CollectionShadowTag

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3),
    })
    void add(int i, T tag); // overrides by CollectionShadowTag

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3),
    })
    T remove(int i); // overrides by CollectionShadowTag

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

}
