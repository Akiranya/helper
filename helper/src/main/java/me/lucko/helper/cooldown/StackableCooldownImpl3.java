package me.lucko.helper.cooldown;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

public class StackableCooldownImpl3<T> implements StackableCooldown {

    private final Cooldown base;
    private final T key;
    private final Function<T, Long> stacks;

    StackableCooldownImpl3(@NotNull Cooldown base, @NotNull T key, @NotNull Function<T, Long> stacks) {
        this.base = base.copy();
        this.key = key;
        this.stacks = stacks;
    }

    @Override public Cooldown getBase() {
        return base;
    }

    @Override public long getStacks() {
        return stacks.apply(key);
    }

}