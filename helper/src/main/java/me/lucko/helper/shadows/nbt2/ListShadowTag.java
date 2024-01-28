package me.lucko.helper.shadows.nbt2;

import me.lucko.helper.nbt2.ShadowTagType;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@SuppressWarnings("unused")
@NmsClassTarget("nbt.NBTTagList")
@DefaultQualifier(NonNull.class)
public interface ListShadowTag extends Shadow, CollectionShadowTag<ShadowTag> {

    static ListShadowTag create() {
        return BukkitShadowFactory.global().constructShadow(ListShadowTag.class);
    }

    static ListShadowTag create(List<ShadowTag> list, ShadowTagType type) {
        return BukkitShadowFactory.global().constructShadow(ListShadowTag.class, list, type.id());
    }

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    CompoundShadowTag getCompound(int index);

    @ObfuscatedTarget({
            @Mapping(value = "b", version = PackageVersion.v1_20_R3)
    })
    ListShadowTag getList(int index);

    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_20_R3)
    })
    short getShort(int index);

    @ObfuscatedTarget({
            @Mapping(value = "e", version = PackageVersion.v1_20_R3)
    })
    int getInt(int index);

    @ObfuscatedTarget({
            @Mapping(value = "f", version = PackageVersion.v1_20_R3)
    })
    int[] getIntArray(int index);

    @ObfuscatedTarget({
            @Mapping(value = "g", version = PackageVersion.v1_20_R3)
    })
    long[] getLongArray(int index);

    @ObfuscatedTarget({
            @Mapping(value = "h", version = PackageVersion.v1_20_R3)
    })
    double getDouble(int index);

    @ObfuscatedTarget({
            @Mapping(value = "i", version = PackageVersion.v1_20_R3)
    })
    float getFloat(int index);

    @ObfuscatedTarget({
            @Mapping(value = "j", version = PackageVersion.v1_20_R3)
    })
    String getString(int index);

}
