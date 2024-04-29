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

@NmsClassTarget("nbt.LongTag")
@DefaultQualifier(NonNull.class)
public interface LongShadowTag extends Shadow, NumberShadowTag {

    static LongShadowTag valueOf(long value) {
        return BukkitShadowFactory.global().staticShadow(LongShadowTag.class).longValueOf(value);
    }

    @ObfuscatedTarget({
            @Mapping(value = "valueOf", version = PackageVersion.NONE),
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    @Static
    LongShadowTag longValueOf(long value);

}
