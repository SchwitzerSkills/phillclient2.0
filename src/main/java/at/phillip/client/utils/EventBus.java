package at.phillip.client.utils;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class EventBus {
    private EventBus() {}

    @FunctionalInterface public interface JoinListener { void onJoin(MinecraftClient client); }
    @FunctionalInterface public interface DisconnectListener { void onDisconnect(MinecraftClient client); }
    @FunctionalInterface public interface StartupListener { void onClientStarted(MinecraftClient client); }

    private static final Set<JoinListener> onJoin = new CopyOnWriteArraySet<>();
    private static final Set<DisconnectListener> onDisconnect = new CopyOnWriteArraySet<>();
    private static final Set<StartupListener> onStarted = new CopyOnWriteArraySet<>();

    private static volatile boolean bound = false;

    public static void bindOnce() {
        if (bound) return;

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            for (JoinListener l : onJoin) safe(() -> l.onJoin(client));
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            for (DisconnectListener l : onDisconnect) safe(() -> l.onDisconnect(client));
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            for (StartupListener l : onStarted) safe(() -> l.onClientStarted(client));
        });

        bound = true;
    }

    public static void addOnJoin(JoinListener l) { if (l != null) onJoin.add(l); }
    public static void addOnDisconnect(DisconnectListener l) { if (l != null) onDisconnect.add(l); }
    public static void addOnStarted(StartupListener l) { if (l != null) onStarted.add(l); }

    public static void removeOnJoin(JoinListener l) { if (l != null) onJoin.remove(l); }
    public static void removeOnDisconnect(DisconnectListener l) { if (l != null) onDisconnect.remove(l); }
    public static void removeOnStarted(StartupListener l) { if (l != null) onStarted.remove(l); }

    public static void clearAll() {
        onJoin.clear();
        onDisconnect.clear();
        onStarted.clear();
    }

    private static void safe(Runnable r) {
        try { r.run(); } catch (Throwable t) { t.printStackTrace(); }
    }
}
