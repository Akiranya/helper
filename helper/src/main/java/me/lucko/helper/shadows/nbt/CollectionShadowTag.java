package me.lucko.helper.shadows.nbt;

import me.lucko.helper.nbt.ShadowTagType;
import me.lucko.helper.nbt.ShadowTags;
import me.lucko.shadow.Field;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowingStrategy;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@NmsClassTarget("nbt.NBTList")
@DefaultQualifier(NonNull.class)
public interface CollectionShadowTag<T extends ShadowTag> extends Shadow, ShadowTag, Iterable<T> {

    // the NMS explicitly overrides java.util.AbstractList
    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_20_R3),
    })
    T set(int i, T tag);

    // the NMS explicitly overrides java.util.AbstractList
    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3),
    })
    void add(int i, T tag);

    // the NMS explicitly overrides java.util.AbstractList
    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3),
    })
    T remove(int i);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_20_R3),
    })
    boolean setTag(int index, ShadowTag element);

    @ObfuscatedTarget({
            @Mapping(value = "b", version = PackageVersion.v1_20_R3),
    })
    boolean addTag(int index, ShadowTag element);

    @ObfuscatedTarget({
            @Mapping(value = "f", version = PackageVersion.v1_20_R3),
    })
    byte elementTypeId();

    default ShadowTagType elementType() {
        return ShadowTagType.of(elementTypeId());
    }

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_20_R3)
    })
    @ShadowingStrategy(
            wrapper = NbtShadowingStrategy.ImmutableListTagWrapper.class
    )
    @Field
    List<T> backingList();

    //<editor-fold desc="AbstractList Proxies">
    boolean add(T e);

    // We must explicitly specify shadow strategy for this method,
    // otherwise it's always effectively shadowed as plain ShadowTag.
    // Consequently, we can't cast T to any subclasses of ShadowTag.
    @ShadowingStrategy(
            wrapper = NbtShadowingStrategy.SingleWrapper.class
    )
    T get(int index);

    boolean contains(T e);

    boolean remove(T e);

    int size();
    //</editor-fold>

    //<editor-fold desc="Iterator Proxies">
    @SuppressWarnings("unchecked")
    @Override default Iterator<T> iterator() {
        final Object tag = Objects.requireNonNull(getShadowTarget());
        final Iterable<Object> iterable = (Iterable<Object>) tag;
        final Iterator<Object> iterator = iterable.iterator();
        return new Iterator<>() {
            @Override public boolean hasNext() {
                return iterator.hasNext();
            }
            @Override public T next() {
                return (T) ShadowTags.shadow(iterator.next());
            }
            @Override public void remove() {
                iterator.remove();
            }
        };
    }

    @Override default void forEach(Consumer<? super T> action) {
        iterator().forEachRemaining(action);
    }

    @Override default Spliterator<T> spliterator() {
        return Spliterators.spliterator(backingList(), Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }
    //</editor-fold>

}
