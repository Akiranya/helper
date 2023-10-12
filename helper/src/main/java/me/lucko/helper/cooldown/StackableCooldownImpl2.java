package me.lucko.helper.cooldown;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

public class StackableCooldownImpl2 implements StackableCooldown {

    private final Cooldown base;
    private final Supplier<Long> stacks;

    StackableCooldownImpl2(@NotNull Cooldown base, @NotNull Supplier<Long> stacks) {
        this.base = base.copy();
        this.stacks = stacks;
    }

    @Override public Cooldown getBase() {
        return base;
    }

    @Override public long getStacks() {
        return stacks.get();
    }

}
