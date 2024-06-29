package me.lucko.helper.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.ByteTag")
@DefaultQualifier(NonNull.class)
public interface ByteTag extends Shadow, NumberTag {

    static ByteTag valueOf(byte value) {
        return BukkitShadowFactory.global().staticShadow(ByteTag.class).byteValueOf(value);
    }

    static ByteTag valueOf(boolean value) {
        return BukkitShadowFactory.global().staticShadow(ByteTag.class).byteValueOf(value);
    }

    @ObfuscatedTarget({
            @Mapping(value = "valueOf", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    @Static
    ByteTag byteValueOf(byte value);

    @ObfuscatedTarget({
            @Mapping(value = "valueOf", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    @Static
    ByteTag byteValueOf(boolean value);

}
