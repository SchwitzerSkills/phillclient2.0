package at.phillip.client.utils;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

import java.util.ArrayList;
import java.util.List;

public final class CommandBus {
    private CommandBus() {}

    private static volatile List<ICommand> commands = new ArrayList<>();
    private static boolean registered = false;

    public static void registerOnce() {
        if (registered) return;
        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
            if (!message.startsWith("#")) return true;
            for (ICommand c : commands) {
                try {
                    if (c.handle(message)) return false;
                } catch (Throwable t) { t.printStackTrace(); }
            }
            return false;
        });
        registered = true;
    }

    public static void setCommands(List<ICommand> newCommands) {
        commands = new ArrayList<>(newCommands);
    }
}
