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

@NmsClassTarget("nbt.NBTTagString")
@DefaultQualifier(NonNull.class)
public interface StringShadowTag extends Shadow, ShadowTag {

    static StringShadowTag valueOf(String value) {
        return BukkitShadowFactory.global().staticShadow(StringShadowTag.class).stringValueOf(value);
    }

    @Static
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    StringShadowTag stringValueOf(String value);

    @ObfuscatedTarget({
            @Mapping(value = "t_", version = PackageVersion.v1_20_R3)
    })
    String value();

}
