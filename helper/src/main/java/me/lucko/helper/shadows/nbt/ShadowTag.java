package me.lucko.helper.shadows.nbt;

import me.lucko.helper.nbt.ShadowTagType;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.DataOutput;
import java.io.IOException;

@NmsClassTarget("nbt.Tag")
@DefaultQualifier(NonNull.class)
public interface ShadowTag extends Shadow {

    @ObfuscatedTarget({
            @Mapping(value = "write", version = PackageVersion.NONE),
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void write(DataOutput dataOutput) throws IOException;

    @ObfuscatedTarget({
            @Mapping(value = "getId", version = PackageVersion.NONE),
            @Mapping(value = "b", version = PackageVersion.v1_20_R3)
    })
    byte getTypeId();

    default ShadowTagType getType() {
        return ShadowTagType.of(getTypeId());
    }

    @ObfuscatedTarget({
            @Mapping(value = "copy", version = PackageVersion.NONE),
            @Mapping(value = "d", version = PackageVersion.v1_20_R3)
    })
    ShadowTag copy();

    @ObfuscatedTarget({
            @Mapping(value = "getAsString", version = PackageVersion.NONE),
            @Mapping(value = "t_", version = PackageVersion.v1_20_R3)
    })
    String asString();

}
