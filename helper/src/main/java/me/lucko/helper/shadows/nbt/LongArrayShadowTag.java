package me.lucko.helper.shadows.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@NmsClassTarget("nbt.LongArrayTag")
@DefaultQualifier(NonNull.class)
public interface LongArrayShadowTag extends CollectionShadowTag<LongShadowTag> {


    static LongArrayShadowTag create(long[] data) {
        return BukkitShadowFactory.global().constructShadow(LongArrayShadowTag.class, (Object) data);
    }

    static LongArrayShadowTag create(LongSet data) {
        return BukkitShadowFactory.global().constructShadow(LongArrayShadowTag.class, (Object) data);
    }

    static LongArrayShadowTag create(List<Long> data) {
        return BukkitShadowFactory.global().constructShadow(LongArrayShadowTag.class, (Object) data);
    }

    @ObfuscatedTarget({
            @Mapping(value = "getAsLongArray", version = PackageVersion.NONE),
            @Mapping(value = "g", version = PackageVersion.v1_20_R3)
    })
    long[] value();

}
