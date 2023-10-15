package me.lucko.helper.plugin;

import me.lucko.helper.terminable.Terminable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

class AutoCloseableListenerWrapper implements Terminable {
    private final Listener listener;

    AutoCloseableListenerWrapper(final Listener listener) {
        this.listener = listener;
    }

    @Override public void close() throws Exception {
        HandlerList.unregisterAll(listener);
    }
}
