package me.lucko.helper.shadows.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.NbtAccounter")
@DefaultQualifier(NonNull.class)
public interface ShadowTagAccounter extends Shadow {

    @ObfuscatedTarget({
            @Mapping(value = "create", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    @Static
    ShadowTagAccounter create(long max);

    @ObfuscatedTarget({
            @Mapping(value = "unlimitedHeap", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    @Static
    ShadowTagAccounter unlimitedHeap();

}
