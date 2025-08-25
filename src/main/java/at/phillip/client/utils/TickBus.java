package at.phillip.client.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class TickBus {
    private static final Set<Runnable> listeners = new CopyOnWriteArraySet<>();
    private static boolean bound = false;

    private TickBus() {}

    public static void bindOnce() {
        if (bound) return;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Runnable r : listeners) {
                try { r.run(); } catch (Throwable t) { t.printStackTrace(); }
            }
        });
        bound = true;
    }

    public static void add(Runnable r)   { if (r != null) listeners.add(r); }
    public static void remove(Runnable r){ if (r != null) listeners.remove(r); }
    public static void clear()           { listeners.clear(); }
}
