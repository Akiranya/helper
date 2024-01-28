package me.lucko.helper.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.NBTTagShort")
@DefaultQualifier(NonNull.class)
public interface ShortShadowTag extends Shadow, ShadowTag, NumberShadowTag {

    static ShortShadowTag valueOf(short value) {
        return BukkitShadowFactory.global().staticShadow(ShortShadowTag.class).shortValueOf(value);
    }

    @Static
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    ShortShadowTag shortValueOf(short value);

}
