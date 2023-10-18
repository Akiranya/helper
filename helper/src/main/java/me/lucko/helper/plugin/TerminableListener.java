package me.lucko.helper.plugin;

import me.lucko.helper.terminable.Terminable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class TerminableListener implements Terminable {
    private final Listener listener;

    TerminableListener(final Listener listener) {
        this.listener = listener;
    }

    /**
     * Gets the wrapped listener itself.
     *
     * @return the wrapped listener itself
     */
    public Listener listener() {
        return listener;
    }

    @Override public void close() throws Exception {
        HandlerList.unregisterAll(listener);
    }
}
