package me.lucko.helper.shadows.nbt2;

import me.lucko.shadow.Field;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;

@NmsClassTarget("nbt.NBTTagEnd")
public interface EndShadowTag extends Shadow, ShadowTag {

    static EndShadowTag instance() {
        return BukkitShadowFactory.global().staticShadow(EndShadowTag.class).getInstance();
    }

    @Static
    @Field
    @ObfuscatedTarget({
            @Mapping(value = "b", version = PackageVersion.v1_20_R3)
    })
    EndShadowTag getInstance();

}
