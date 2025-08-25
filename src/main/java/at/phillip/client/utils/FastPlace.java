package at.phillip.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.lang.reflect.Field;

public final class FastPlace {
    private static boolean active = false;
    private static int cps = 12;
    private static long lastNs = 15L;

    private FastPlace() {}

    public static boolean isActive() { return active; }

    public static void enable() {
        active = true;
        lastNs = 0L;
    }

    public static void disable() {
        active = false;
    }

    public static void toggle() {
        if (active) disable(); else enable();
    }

    public static void onClientTick() {
        if (!active) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.interactionManager == null) return;
        if (!mc.options.useKey.isPressed()) return;

        zeroUseCooldown(mc);

        long now = System.nanoTime();
        long interval = 1_000_000_000L / Math.max(1, cps);
        if (lastNs == 0L) lastNs = now - interval;

        int safety = 0;
        while (now - lastNs >= interval && safety++ < 8) {
            attemptPlace(mc);
            lastNs += interval;
        }
    }

    private static void attemptPlace(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        boolean placed = false;
        HitResult hr = mc.crosshairTarget;

        if (hr instanceof BlockHitResult bhr) {
            ActionResult r1 = mc.interactionManager.interactBlock(p, Hand.MAIN_HAND, bhr);
            if (isAccepted(r1)) { p.swingHand(Hand.MAIN_HAND); placed = true; }
            else {
                ActionResult r2 = mc.interactionManager.interactBlock(p, Hand.OFF_HAND, bhr);
                if (isAccepted(r2)) { p.swingHand(Hand.OFF_HAND); placed = true; }
            }
        }

        if (!placed) {
            ActionResult r1 = mc.interactionManager.interactItem(p, Hand.MAIN_HAND);
            if (isAccepted(r1)) { p.swingHand(Hand.MAIN_HAND); }
            else {
                ActionResult r2 = mc.interactionManager.interactItem(p, Hand.OFF_HAND);
                if (isAccepted(r2)) { p.swingHand(Hand.OFF_HAND); }
            }
        }
    }

    private static boolean isAccepted(ActionResult r) {
        if (r == null) return false;
        return r != ActionResult.PASS && r != ActionResult.FAIL;
    }

    private static void zeroUseCooldown(MinecraftClient mc) {
        try {
            Field f = MinecraftClient.class.getDeclaredField("itemUseCooldown");
            f.setAccessible(true);
            if (f.getInt(mc) != 0) f.setInt(mc, 0);
        } catch (Throwable ignored) {}
    }
}
