package me.lucko.helper.shadows.nbt;

import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@NmsClassTarget("nbt.IntArrayTag")
@DefaultQualifier(NonNull.class)
public interface IntArrayShadowTag extends CollectionShadowTag<IntShadowTag> {

    @SuppressWarnings("RedundantCast")
    static IntArrayShadowTag create(int[] data) {
        return BukkitShadowFactory.global().constructShadow(IntArrayShadowTag.class, (Object) data);
    }

    @SuppressWarnings("RedundantCast")
    static IntArrayShadowTag create(List<Integer> data) {
        return BukkitShadowFactory.global().constructShadow(IntArrayShadowTag.class, (Object) data);
    }

    @ObfuscatedTarget({
            @Mapping(value = "getAsIntArray", version = PackageVersion.NONE),
            @Mapping(value = "g", version = PackageVersion.v1_20_R3)
    })
    int[] value();

}
