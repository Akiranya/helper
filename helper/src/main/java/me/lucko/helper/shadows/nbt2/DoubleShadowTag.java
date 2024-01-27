package me.lucko.helper.shadows.nbt2;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;

@NmsClassTarget("nbt.NBTTagDouble")
public interface DoubleShadowTag extends Shadow, ShadowTag, NumberShadowTag {

    static DoubleShadowTag valueOf(double value) {
        return BukkitShadowFactory.global().staticShadow(DoubleShadowTag.class).doubleValueOf(value);
    }

    @Static
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    DoubleShadowTag doubleValueOf(double value);

}
