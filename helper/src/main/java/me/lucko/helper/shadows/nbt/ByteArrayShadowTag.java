package me.lucko.helper.shadows.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@NmsClassTarget("nbt.ByteArrayTag")
@DefaultQualifier(NonNull.class)
public interface ByteArrayShadowTag extends Shadow, CollectionShadowTag<ByteShadowTag> {

    static ByteArrayShadowTag create(byte[] data) {
        return BukkitShadowFactory.global().constructShadow(ByteArrayShadowTag.class, (Object) data);
    }

    static ByteArrayShadowTag create(List<Byte> data) {
        return BukkitShadowFactory.global().constructShadow(ByteArrayShadowTag.class, (Object) data);
    }

    @ObfuscatedTarget({
            @Mapping(value = "getAsByteArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "e", version = NmsVersion.v1_20_R3),
    })
    byte[] value();

}
