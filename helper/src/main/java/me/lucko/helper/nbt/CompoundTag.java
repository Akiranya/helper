package me.lucko.helper.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.shadow.Field;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowingStrategy;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@NmsClassTarget("nbt.CompoundTag")
@DefaultQualifier(NonNull.class)
public interface CompoundTag extends Shadow, Tag {

    static CompoundTag create() {
        return BukkitShadowFactory.global().constructShadow(CompoundTag.class);
    }

    @ObfuscatedTarget({
            @Mapping(value = "tags", version = NmsVersion.v1_20_R4),
            @Mapping(value = "x", version = NmsVersion.v1_20_R3)
    })
    @ShadowingStrategy(
            wrapper = NbtShadowingStrategy.ForTypelessMaps.class
    )
    @Field
    Map<Object, Object> tags();

    @ObfuscatedTarget({
            @Mapping(value = "getAllKeys", version = NmsVersion.v1_20_R4),
            @Mapping(value = "e", version = NmsVersion.v1_20_R3)
    })
    Set<String> keySet();

    @ObfuscatedTarget({
            @Mapping(value = "size", version = NmsVersion.v1_20_R4),
            @Mapping(value = "f", version = NmsVersion.v1_20_R3)
    })
    int size();

    @ObfuscatedTarget({
            @Mapping(value = "isEmpty", version = NmsVersion.v1_20_R4),
            @Mapping(value = "g", version = NmsVersion.v1_20_R3)
    })
    boolean isEmpty();

    @ObfuscatedTarget({
            @Mapping(value = "put", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    @Nullable Tag put(String key, Tag value);

    @ObfuscatedTarget({
            @Mapping(value = "putByte", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putByte(String key, byte value);

    @ObfuscatedTarget({
            @Mapping(value = "putShort", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putShort(String key, short value);

    @ObfuscatedTarget({
            @Mapping(value = "putInt", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putInt(String key, int value);

    @ObfuscatedTarget({
            @Mapping(value = "putLong", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putLong(String key, long value);

    @ObfuscatedTarget({
            @Mapping(value = "putUUID", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putUUID(String key, UUID value);

    @ObfuscatedTarget({
            @Mapping(value = "putFloat", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putFloat(String key, float value);

    @ObfuscatedTarget({
            @Mapping(value = "putDouble", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putDouble(String key, double value);

    @ObfuscatedTarget({
            @Mapping(value = "putString", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putString(String key, String value);

    @ObfuscatedTarget({
            @Mapping(value = "putByteArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putByteArray(String key, byte[] value);

    @ObfuscatedTarget({
            @Mapping(value = "putIntArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putIntArray(String key, int[] value);

    @ObfuscatedTarget({
            @Mapping(value = "putLongArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putLongArray(String key, long[] value);

    @ObfuscatedTarget({
            @Mapping(value = "putBoolean", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void putBoolean(String key, boolean value);

    @ObfuscatedTarget({
            @Mapping(value = "get", version = NmsVersion.v1_20_R4),
            @Mapping(value = "c", version = NmsVersion.v1_20_R3)
    })
    @ShadowingStrategy(
            wrapper = NbtShadowingStrategy.ForShadowTags.class
    )
    @Nullable Tag get(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getTagType", version = NmsVersion.v1_20_R4),
            @Mapping(value = "d", version = NmsVersion.v1_20_R3)
    })
    byte getTagTypeId(String key);

    default ShadowTagType getTagType(String key) {
        return ShadowTagType.of(getTypeId());
    }

    @ObfuscatedTarget({
            @Mapping(value = "contains", version = NmsVersion.v1_20_R4),
            @Mapping(value = "e", version = NmsVersion.v1_20_R3)
    })
    boolean contains(String key);

    @ObfuscatedTarget({
            @Mapping(value = "contains", version = NmsVersion.v1_20_R4),
            @Mapping(value = "b", version = NmsVersion.v1_20_R3)
    })
    boolean contains(String key, int type);

    default boolean contains(String key, ShadowTagType type) {
        return contains(key, type.number() ? ShadowTagType.ANY_NUMERIC.id() : type.id());
    }

    @ObfuscatedTarget({
            @Mapping(value = "getByte", version = NmsVersion.v1_20_R4),
            @Mapping(value = "f", version = NmsVersion.v1_20_R3)
    })
    byte getByte(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getShort", version = NmsVersion.v1_20_R4),
            @Mapping(value = "g", version = NmsVersion.v1_20_R3)
    })
    short getShort(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getInt", version = NmsVersion.v1_20_R4),
            @Mapping(value = "h", version = NmsVersion.v1_20_R3)
    })
    int getInt(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getLong", version = NmsVersion.v1_20_R4),
            @Mapping(value = "i", version = NmsVersion.v1_20_R3)
    })
    long getLong(String key);

    /**
     * You must use {@link #hasUUID(String)} before or else it <b>will</b> throw an NPE.
     */
    @ObfuscatedTarget({
            @Mapping(value = "getUUID", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    UUID getUUID(String key);

    @ObfuscatedTarget({
            @Mapping(value = "hasUUID", version = NmsVersion.v1_20_R4),
            @Mapping(value = "b", version = NmsVersion.v1_20_R3)
    })
    boolean hasUUID(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getFloat", version = NmsVersion.v1_20_R4),
            @Mapping(value = "j", version = NmsVersion.v1_20_R3)
    })
    float getFloat(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getDouble", version = NmsVersion.v1_20_R4),
            @Mapping(value = "k", version = NmsVersion.v1_20_R3)
    })
    double getDouble(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getString", version = NmsVersion.v1_20_R4),
            @Mapping(value = "l", version = NmsVersion.v1_20_R3)
    })
    String getString(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getByteArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "m", version = NmsVersion.v1_20_R3)
    })
    byte[] getByteArray(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getIntArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "n", version = NmsVersion.v1_20_R3)
    })
    int[] getIntArray(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getLongArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "o", version = NmsVersion.v1_20_R3)
    })
    long[] getLongArray(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getCompound", version = NmsVersion.v1_20_R4),
            @Mapping(value = "p", version = NmsVersion.v1_20_R3)
    })
    CompoundTag getCompound(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getList", version = NmsVersion.v1_20_R4),
            @Mapping(value = "c", version = NmsVersion.v1_20_R3)
    })
    ListShadowTag getList(String key, int type);

    default ListShadowTag getList(String key, ShadowTagType type) {
        return getList(key, type.id());
    }

    @ObfuscatedTarget({
            @Mapping(value = "getBoolean", version = NmsVersion.v1_20_R4),
            @Mapping(value = "q", version = NmsVersion.v1_20_R3)
    })
    boolean getBoolean(String key);

    @ObfuscatedTarget({
            @Mapping(value = "remove", version = NmsVersion.v1_20_R4),
            @Mapping(value = "r", version = NmsVersion.v1_20_R3)
    })
    void remove(String key);

    @ObfuscatedTarget({
            @Mapping(value = "merge", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    void merge(CompoundTag other);

}
