package me.lucko.helper.shadows.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.LongTag")
@DefaultQualifier(NonNull.class)
public interface LongShadowTag extends Shadow, NumberShadowTag {

    static LongShadowTag valueOf(long value) {
        return BukkitShadowFactory.global().staticShadow(LongShadowTag.class).longValueOf(value);
    }

    @ObfuscatedTarget({
            @Mapping(value = "valueOf", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    @Static
    LongShadowTag longValueOf(long value);

}
