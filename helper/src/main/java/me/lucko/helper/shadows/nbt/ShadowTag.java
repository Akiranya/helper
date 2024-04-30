package me.lucko.helper.shadows.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.helper.nbt.ShadowTagType;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.DataOutput;
import java.io.IOException;

@NmsClassTarget("nbt.Tag")
@DefaultQualifier(NonNull.class)
public interface ShadowTag extends Shadow {

    @ObfuscatedTarget({
            @Mapping(value = "write", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void write(DataOutput dataOutput) throws IOException;

    @ObfuscatedTarget({
            @Mapping(value = "getId", version = NmsVersion.v1_20_R4),
            @Mapping(value = "b", version = NmsVersion.v1_20_R3)
    })
    byte getTypeId();

    default ShadowTagType getType() {
        return ShadowTagType.of(getTypeId());
    }

    @ObfuscatedTarget({
            @Mapping(value = "copy", version = NmsVersion.v1_20_R4),
            @Mapping(value = "d", version = NmsVersion.v1_20_R3)
    })
    ShadowTag copy();

    @ObfuscatedTarget({
            @Mapping(value = "getAsString", version = NmsVersion.v1_20_R4),
            @Mapping(value = "t_", version = NmsVersion.v1_20_R3)
    })
    String asString();

}
