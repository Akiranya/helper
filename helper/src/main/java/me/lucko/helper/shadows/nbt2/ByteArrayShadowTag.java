package me.lucko.helper.shadows.nbt2;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@NmsClassTarget("nbt.NBTTagByteArray")
@DefaultQualifier(NonNull.class)
public interface ByteArrayShadowTag extends Shadow, CollectionShadowTag<ByteShadowTag> {

    @SuppressWarnings("RedundantCast")
    static ByteArrayShadowTag create(byte[] data) {
        return BukkitShadowFactory.global().constructShadow(ByteArrayShadowTag.class, (Object) data);
    }

    @SuppressWarnings("RedundantCast")
    static ByteArrayShadowTag create(List<Byte> data) {
        return BukkitShadowFactory.global().constructShadow(ByteArrayShadowTag.class, (Object) data);
    }

    @ObfuscatedTarget({
            @Mapping(value = "e", version = PackageVersion.v1_20_R3),
    })
    byte[] value();

}
