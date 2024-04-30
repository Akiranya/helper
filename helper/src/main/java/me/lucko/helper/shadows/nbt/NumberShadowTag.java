package me.lucko.helper.shadows.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.NumericTag")
@DefaultQualifier(NonNull.class)
public interface NumberShadowTag extends ShadowTag {

    @ObfuscatedTarget({
            @Mapping(value = "getAsLong", version = NmsVersion.v1_20_R4),
            @Mapping(value = "f", version = NmsVersion.v1_20_R3)
    })
    long longValue();

    @ObfuscatedTarget({
            @Mapping(value = "getAsInt", version = NmsVersion.v1_20_R4),
            @Mapping(value = "g", version = NmsVersion.v1_20_R3)
    })
    int intValue();

    @ObfuscatedTarget({
            @Mapping(value = "getAsShort", version = NmsVersion.v1_20_R4),
            @Mapping(value = "h", version = NmsVersion.v1_20_R3)
    })
    short shortValue();

    @ObfuscatedTarget({
            @Mapping(value = "getAsByte", version = NmsVersion.v1_20_R4),
            @Mapping(value = "i", version = NmsVersion.v1_20_R3)
    })
    byte byteValue();

    @ObfuscatedTarget({
            @Mapping(value = "getAsDouble", version = NmsVersion.v1_20_R4),
            @Mapping(value = "j", version = NmsVersion.v1_20_R3)
    })
    double doubleValue();

    @ObfuscatedTarget({
            @Mapping(value = "getAsFloat", version = NmsVersion.v1_20_R4),
            @Mapping(value = "k", version = NmsVersion.v1_20_R3)
    })
    float floatValue();

}
