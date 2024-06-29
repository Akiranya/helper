package me.lucko.helper.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@NmsClassTarget("nbt.NbtIo")
@DefaultQualifier(NonNull.class)
public interface NbtIO extends Shadow {

    @ObfuscatedTarget({
            @Mapping(value = "read", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3),
    })
    @Static
    CompoundTag read(DataInput input) throws IOException;

    @ObfuscatedTarget({
            @Mapping(value = "write", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3),
    })
    @Static
    void write(CompoundTag nbt, DataOutput output) throws IOException;

}
