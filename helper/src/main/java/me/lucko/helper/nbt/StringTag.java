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

@NmsClassTarget("nbt.StringTag")
@DefaultQualifier(NonNull.class)
public interface StringTag extends Shadow, Tag {

    static StringTag valueOf(String value) {
        return BukkitShadowFactory.global().staticShadow(StringTag.class).stringValueOf(value);
    }

    @ObfuscatedTarget({
            @Mapping(value = "valueOf", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    @Static
    StringTag stringValueOf(String value);

    @ObfuscatedTarget({
            @Mapping(value = "getAsString", version = NmsVersion.v1_20_R4),
            @Mapping(value = "t_", version = NmsVersion.v1_20_R3)
    })
    String value();

}
