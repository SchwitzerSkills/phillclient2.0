package at.phillip.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright {
    private static boolean enabled = false;
    private static double prevGamma = 1.0;

    public static boolean isEnabled() { return enabled; }

    public static void toggle() { setEnabled(!enabled); }

    public static void setEnabled(boolean on){
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc == null || mc.options == null) return;

        if(on && !enabled){
            prevGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(16.0D);
            mc.options.write();
            enabled = true;
        } else if(!on && enabled) {
            mc.options.getGamma().setValue(prevGamma);
            mc.options.write();
            enabled = false;

            if(mc.player != null){
                mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        }
    }

    public static void onClientTick(){
        if(!enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc == null || mc.player == null) return;

        StatusEffectInstance nv = new StatusEffectInstance(
                StatusEffects.NIGHT_VISION, 220, 0,
                true,
                false,
                false
        );
        mc.player.addStatusEffect(nv);
    }
}
