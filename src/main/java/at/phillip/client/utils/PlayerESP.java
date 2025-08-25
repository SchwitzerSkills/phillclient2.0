package at.phillip.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class PlayerESP {

    private static boolean enabled = false;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggle() {
        setEnabled(!enabled);
    }

    private static void setEnabled(boolean on) {
        if (on && !enabled) {
            enabled = true;
            send("§7[PlayerESP] §aenabled");
        } else if (!on && enabled) {
            enabled = false;
            send("§7[PlayerESP] §cdisabled");
            flushLocalGlow();
        }
    }

    private static void send(String s) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.sendMessage(Text.of(s), false);
    }

    public static boolean shouldGlow(Entity e) {
        if (!enabled || !(e instanceof PlayerEntity)) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc != null && mc.player != null && e != mc.player;
    }

    private static void flushLocalGlow() {
        var mc = MinecraftClient.getInstance();
        if (mc != null && mc.world != null) {
            for (var p : mc.world.getPlayers()) {
                if (p == mc.player) continue;
                if (!at.phillip.client.utils.MurderScanner.isMarked(p.getUuid())) {
                    p.setGlowing(false);
                }
            }
        }
    }
}
