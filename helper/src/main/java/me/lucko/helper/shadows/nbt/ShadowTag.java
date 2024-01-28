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

@SuppressWarnings("unused")
@NmsClassTarget("nbt.NBTBase")
@DefaultQualifier(NonNull.class)
public interface ShadowTag extends Shadow {

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void write(DataOutput dataOutput) throws IOException;

    @ObfuscatedTarget({
            @Mapping(value = "b", version = PackageVersion.v1_20_R3)
    })
    byte getId();

    default ShadowTagType getType() {
        return ShadowTagType.of(getId());
    }

    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_20_R3)
    })
    ShadowTag copy();

    @ObfuscatedTarget({
            @Mapping(value = "t_", version = PackageVersion.v1_20_R3)
    })
    String asString();

}
