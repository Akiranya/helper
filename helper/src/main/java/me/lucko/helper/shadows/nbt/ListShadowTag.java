package me.lucko.helper.shadows.nbt;

import cc.mewcraft.version.NmsVersion;
import me.lucko.helper.nbt.ShadowTagType;
import me.lucko.helper.nbt.ShadowTags;
import me.lucko.shadow.Field;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowingStrategy;
import me.lucko.shadow.bukkit.BukkitShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

@NmsClassTarget("nbt.ListTag")
@DefaultQualifier(NonNull.class)
public interface ListShadowTag extends Shadow, CollectionShadowTag<ShadowTag>, Iterable<ShadowTag> {

    static ListShadowTag create() {
        return BukkitShadowFactory.global().constructShadow(ListShadowTag.class);
    }

    static ListShadowTag create(List<ShadowTag> list, ShadowTagType type) {
        List<Object> unwrap = list.stream().map(Objects.requireNonNull(Shadow::getShadowTarget)).toList(); // unwrap
        return BukkitShadowFactory.global().constructShadow(ListShadowTag.class, unwrap, type.id());
    }

    @ObfuscatedTarget({
            @Mapping(value = "getCompound", version = NmsVersion.v1_20_R4),
            @Mapping(value = "a", version = NmsVersion.v1_20_R3)
    })
    CompoundShadowTag getCompound(int index);

    @ObfuscatedTarget({
            @Mapping(value = "getList", version = NmsVersion.v1_20_R4),
            @Mapping(value = "b", version = NmsVersion.v1_20_R3)
    })
    ListShadowTag getList(int index);

    @ObfuscatedTarget({
            @Mapping(value = "getShort", version = NmsVersion.v1_20_R4),
            @Mapping(value = "d", version = NmsVersion.v1_20_R3)
    })
    short getShort(int index);

    @ObfuscatedTarget({
            @Mapping(value = "getInt", version = NmsVersion.v1_20_R4),
            @Mapping(value = "e", version = NmsVersion.v1_20_R3)
    })
    int getInt(int index);

    @ObfuscatedTarget({
            @Mapping(value = "getIntArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "f", version = NmsVersion.v1_20_R3)
    })
    int[] getIntArray(int index);

    @ObfuscatedTarget({
            @Mapping(value = "getLongArray", version = NmsVersion.v1_20_R4),
            @Mapping(value = "g", version = NmsVersion.v1_20_R3)
    })
    long[] getLongArray(int index);

    @ObfuscatedTarget({
            @Mapping(value = "getDouble", version = NmsVersion.v1_20_R4),
            @Mapping(value = "h", version = NmsVersion.v1_20_R3)
    })
    double getDouble(int index);

    @ObfuscatedTarget({
            @Mapping(value = "getFloat", version = NmsVersion.v1_20_R4),
            @Mapping(value = "i", version = NmsVersion.v1_20_R3)
    })
    float getFloat(int index);

    @ObfuscatedTarget({
            @Mapping(value = "getString", version = NmsVersion.v1_20_R4),
            @Mapping(value = "j", version = NmsVersion.v1_20_R3)
    })
    String getString(int index);

    @ObfuscatedTarget({
            @Mapping(value = "list", version = NmsVersion.v1_20_R4),
            @Mapping(value = "c", version = NmsVersion.v1_20_R3)
    })
    @ShadowingStrategy(
            wrapper = NbtShadowingStrategy.ForImmutableListTags.class
    )
    @Field
    List<ShadowTag> list();

    @SuppressWarnings("unchecked")
    @Override default Iterator<ShadowTag> iterator() {
        final Object tag = Objects.requireNonNull(getShadowTarget());
        final Iterable<Object> iterable = (Iterable<Object>) tag;
        final Iterator<Object> iterator = iterable.iterator();
        return new Iterator<>() {
            @Override public boolean hasNext() {
                return iterator.hasNext();
            }
            @Override public ShadowTag next() {
                return ShadowTags.shadow(iterator.next());
            }
            @Override public void remove() {
                iterator.remove();
            }
        };
    }

    @Override default void forEach(Consumer<? super ShadowTag> action) {
        iterator().forEachRemaining(action);
    }

    @Override default Spliterator<ShadowTag> spliterator() {
        return Spliterators.spliterator(list(), Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

}
