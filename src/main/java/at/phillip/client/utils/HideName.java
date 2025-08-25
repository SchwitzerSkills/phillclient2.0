package at.phillip.client.utils;

import net.minecraft.client.MinecraftClient;

public final class HideName {
    private static boolean enabled = false;
    public static boolean isEnabled() { return enabled; }
    public static void toggle() { enabled = !enabled; }
    public static String myName() {
        var mc = MinecraftClient.getInstance();
        return (mc != null && mc.getSession() != null) ? mc.getSession().getUsername() : null;
    }
}
