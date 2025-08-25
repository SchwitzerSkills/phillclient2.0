package at.phillip.client.commands;

import at.phillip.client.utils.FullBright;
import at.phillip.client.utils.ICommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class FullBrightCommand implements ICommand {
    @Override
    public String getName() { return "fullbright"; }

    @Override
    public boolean handle(String message) {
        if(!message.toLowerCase().startsWith("#" + getName())) return false;
        FullBright.toggle();
        send(FullBright.isEnabled() ? "§7[FullBright] §aenabled" : "§7[FullBright] §cdisabled");
        return false;
    }

    public void send(String s){
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player != null) mc.player.sendMessage(Text.of(s), false);
    }
}
