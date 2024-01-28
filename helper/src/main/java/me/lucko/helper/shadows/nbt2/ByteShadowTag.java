package me.lucko.helper.shadows.nbt2;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.NBTTagByte")
@DefaultQualifier(NonNull.class)
public interface ByteShadowTag extends Shadow, ShadowTag, NumberShadowTag {

    static ByteShadowTag valueOf(byte value) {
        return BukkitShadowFactory.global().staticShadow(ByteShadowTag.class).byteValueOf(value);
    }

    static ByteShadowTag valueOf(boolean value) {
        return BukkitShadowFactory.global().staticShadow(ByteShadowTag.class).byteValueOf(value);
    }

    @Static
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    ByteShadowTag byteValueOf(byte value);

    @Static
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    ByteShadowTag byteValueOf(boolean value);

}
