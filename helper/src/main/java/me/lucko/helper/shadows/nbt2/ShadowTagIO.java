package me.lucko.helper.shadows.nbt2;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@SuppressWarnings("unused")
@NmsClassTarget("nbt.NBTCompressedStreamTools")
public interface ShadowTagIO extends Shadow {

    @Static
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3),
    })
    CompoundShadowTag read(DataInput input) throws IOException;

    @Static
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3),
    })
    void write(CompoundShadowTag nbt, DataOutput output) throws IOException;

}
