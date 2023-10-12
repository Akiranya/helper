package me.lucko.helper.persistent;

import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.StackableCooldown;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class StackableCooldownType implements PersistentDataType<byte[], StackableCooldown> {

    @Override public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override public @NotNull Class<StackableCooldown> getComplexType() {
        return StackableCooldown.class;
    }

    @Override public byte @NotNull [] toPrimitive(final @NotNull StackableCooldown complex, final @NotNull PersistentDataAdapterContext context) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeLong(complex.getBase().getLastTested().orElse(0L));
        out.writeLong(complex.getBase().getTimeout());
        out.writeLong(complex.getStacks());
        return out.toByteArray();
    }

    @Override public @NotNull StackableCooldown fromPrimitive(final byte @NotNull [] primitive, final @NotNull PersistentDataAdapterContext context) {
        ByteArrayDataInput in = ByteStreams.newDataInput(primitive);
        long lastTested = in.readLong();
        long timeout = in.readLong();
        long stacks = in.readLong();
        StackableCooldown cooldown = StackableCooldown.of(Cooldown.of(timeout, TimeUnit.MILLISECONDS), stacks);
        cooldown.setLastTested(lastTested);
        return cooldown;
    }

}
