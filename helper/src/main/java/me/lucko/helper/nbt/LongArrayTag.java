package me.lucko.helper.nbt;

import cc.mewcraft.version.NmsVersion;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@NmsClassTarget("nbt.LongArrayTag")
@DefaultQualifier(NonNull.class)
public interface LongArrayTag extends CollectionTag<LongTag> {


    static LongArrayTag create(long[] data) {
        return BukkitShadowFactory.global().constructShadow(LongArrayTag.class, (Object) data);
    }

    static LongArrayTag create(LongSet data) {
        return BukkitShadowFactory.global().constructShadow(LongArrayTag.class, (Object) data);
    }

    static LongArrayTag create(List<Long> data) {
        return BukkitShadowFactory.global().constructShadow(LongArrayTag.class, (Object) data);
    }

    @ObfuscatedTarget({
            @Mapping(value = "getAsLongArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "g", version = NmsVersion.v1_20_R3)
    })
    long[] value();

}
