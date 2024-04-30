package me.lucko.helper.shadows.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.shadow.Field;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.Target;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@NmsClassTarget("nbt.EndTag")
@DefaultQualifier(NonNull.class)
public interface EndShadowTag extends Shadow, ShadowTag {

    static EndShadowTag instance() {
        return BukkitShadowFactory.global().staticShadow(EndShadowTag.class).INSTANCE();
    }

    @ObfuscatedTarget({
            @Mapping(value = "INSTANCE", version = NmsVersion.v1_20_R4),
            @Mapping(value = "b", version = NmsVersion.v1_20_R3)
    })
    @Static
    @Field
    @Target("INSTANCE")
    EndShadowTag INSTANCE();

}
