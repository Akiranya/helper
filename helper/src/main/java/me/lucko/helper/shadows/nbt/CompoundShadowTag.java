package me.lucko.helper.shadows.nbt;

import me.lucko.helper.nbt.ShadowTagType;
import me.lucko.shadow.Field;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowingStrategy;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
@NmsClassTarget("nbt.NBTTagCompound")
@DefaultQualifier(NonNull.class)
public interface CompoundShadowTag extends Shadow, ShadowTag {

    static CompoundShadowTag create() {
        return BukkitShadowFactory.global().constructShadow(CompoundShadowTag.class);
    }

    @Field
    @ShadowingStrategy(wrapper = NbtShadowingStrategy.UnsafeMutableMapWrapper.class)
    @ObfuscatedTarget({
            @Mapping(value = "x", version = PackageVersion.v1_20_R3)
    })
    Map<Object, Object> tags();

    @ObfuscatedTarget({
            @Mapping(value = "e", version = PackageVersion.v1_20_R3)
    })
    Set<String> keySet();

    @ObfuscatedTarget({
            @Mapping(value = "f", version = PackageVersion.v1_20_R3)
    })
    int size();

    @ObfuscatedTarget({
            @Mapping(value = "g", version = PackageVersion.v1_20_R3)
    })
    boolean isEmpty();

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    @Nullable ShadowTag put(String key, ShadowTag value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putByte(String key, byte value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putShort(String key, short value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putInt(String key, int value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putLong(String key, long value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putUUID(String key, UUID value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putFloat(String key, float value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putDouble(String key, double value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putString(String key, String value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putByteArray(String key, byte[] value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putIntArray(String key, int[] value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putLongArray(String key, long[] value);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void putBoolean(String key, boolean value);

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3)
    })
    @ShadowingStrategy(wrapper = NbtShadowingStrategy.SingleWrapper.class)
    @Nullable ShadowTag get(String key);

    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_20_R3)
    })
    byte getTagType(String key);

    @ObfuscatedTarget({
            @Mapping(value = "e", version = PackageVersion.v1_20_R3)
    })
    boolean contains(String key);

    @ObfuscatedTarget({
            @Mapping(value = "b", version = PackageVersion.v1_20_R3)
    })
    boolean contains(String key, int type);

    default boolean contains(String key, ShadowTagType type) {
        return contains(key, type.number() ? ShadowTagType.ANY_NUMERIC.id() : type.id());
    }

    @ObfuscatedTarget({
            @Mapping(value = "f", version = PackageVersion.v1_20_R3)
    })
    byte getByte(String key);

    @ObfuscatedTarget({
            @Mapping(value = "g", version = PackageVersion.v1_20_R3)
    })
    short getShort(String key);

    @ObfuscatedTarget({
            @Mapping(value = "h", version = PackageVersion.v1_20_R3)
    })
    int getInt(String key);

    @ObfuscatedTarget({
            @Mapping(value = "i", version = PackageVersion.v1_20_R3)
    })
    long getLong(String key);

    /**
     * You must use {@link #hasUUID(String)} before or else it <b>will</b> throw an NPE.
     */
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    UUID getUUID(String key);

    @ObfuscatedTarget({
            @Mapping(value = "b", version = PackageVersion.v1_20_R3)
    })
    boolean hasUUID(String key);

    @ObfuscatedTarget({
            @Mapping(value = "j", version = PackageVersion.v1_20_R3)
    })
    float getFloat(String key);

    @ObfuscatedTarget({
            @Mapping(value = "k", version = PackageVersion.v1_20_R3)
    })
    double getDouble(String key);

    @ObfuscatedTarget({
            @Mapping(value = "l", version = PackageVersion.v1_20_R3)
    })
    String getString(String key);

    @ObfuscatedTarget({
            @Mapping(value = "m", version = PackageVersion.v1_20_R3)
    })
    byte[] getByteArray(String key);

    @ObfuscatedTarget({
            @Mapping(value = "n", version = PackageVersion.v1_20_R3)
    })
    int[] getIntArray(String key);

    @ObfuscatedTarget({
            @Mapping(value = "o", version = PackageVersion.v1_20_R3)
    })
    long[] getLongArray(String key);

    @ObfuscatedTarget({
            @Mapping(value = "p", version = PackageVersion.v1_20_R3)
    })
    CompoundShadowTag getCompound(String key);

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3)
    })
    ListShadowTag getList(String key, int type);

    default ListShadowTag getList(String key, ShadowTagType type) {
        return getList(key, type.id());
    }

    @ObfuscatedTarget({
            @Mapping(value = "q", version = PackageVersion.v1_20_R3)
    })
    boolean getBoolean(String key);

    @ObfuscatedTarget({
            @Mapping(value = "r", version = PackageVersion.v1_20_R3)
    })
    void remove(String key);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3)
    })
    void merge(CompoundShadowTag other);

}
