package me.lucko.helper.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.NbtAccounter")
@DefaultQualifier(NonNull.class)
public interface ShadowTagAccounter extends Shadow {

    @ObfuscatedTarget({
            @Mapping(value = "create", version = PackageVersion.NONE),
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    @Static
    ShadowTagAccounter create(long max);

    @ObfuscatedTarget({
            @Mapping(value = "unlimitedHeap", version = PackageVersion.NONE),
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    @Static
    ShadowTagAccounter unlimitedHeap();

}
