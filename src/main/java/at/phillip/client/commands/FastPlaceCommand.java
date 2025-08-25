package at.phillip.client.commands;

import at.phillip.client.utils.FastPlace;
import at.phillip.client.utils.ICommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class FastPlaceCommand implements ICommand {
    @Override public String getName() { return "fastplace"; }

    @Override
    public boolean handle(String message) {
        if (!message.toLowerCase().startsWith("#" + getName())) return false;

        String[] a = message.trim().split("\\s+");
        if (a.length == 1) {
            FastPlace.toggle();
            send(FastPlace.isActive() ? "§aFastPlace: ON" : "§eFastPlace: OFF");
            return true;
        }
        send("§7Usage: #fastplace");
        return true;
    }

    private void send(String s) {
        var mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.sendMessage(Text.of(s), false);
    }
}
