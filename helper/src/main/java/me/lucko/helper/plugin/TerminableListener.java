package me.lucko.helper.plugin;

import me.lucko.helper.terminable.Terminable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class TerminableListener<T extends Listener> implements Terminable {
    private final T listener;

    TerminableListener(final T listener) {
        this.listener = listener;
    }

    /**
     * Gets the wrapped listener itself.
     *
     * @return the wrapped listener itself
     */
    public T listener() {
        return listener;
    }

    @Override public void close() throws Exception {
        HandlerList.unregisterAll(listener);
    }
}
