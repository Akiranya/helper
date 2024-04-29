package me.lucko.helper.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@NmsClassTarget("nbt.NbtIo")
@DefaultQualifier(NonNull.class)
public interface ShadowTagIO extends Shadow {

    @ObfuscatedTarget({
            @Mapping(value = "read", version = PackageVersion.NONE),
            @Mapping(value = "a", version = PackageVersion.v1_20_R3),
    })
    @Static
    CompoundShadowTag read(DataInput input) throws IOException;

    @ObfuscatedTarget({
            @Mapping(value = "write", version = PackageVersion.NONE),
            @Mapping(value = "a", version = PackageVersion.v1_20_R3),
    })
    @Static
    void write(CompoundShadowTag nbt, DataOutput output) throws IOException;

}
