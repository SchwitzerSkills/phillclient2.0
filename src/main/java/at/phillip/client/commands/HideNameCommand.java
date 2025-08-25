package at.phillip.client.commands;

import at.phillip.client.utils.ICommand;
import at.phillip.client.utils.HideName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class HideNameCommand implements ICommand {
    @Override
    public String getName() {
        return "hidename";
    }

    @Override
    public boolean handle(String message) {
        if(!message.toLowerCase().startsWith("#" + getName())) return false;
        HideName.toggle();
        send(HideName.isEnabled() ? "§7[HideName] §aenabled" : "§7[HideName] §cdisabled");
        return false;
    }

    private void send(String s){
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player != null) mc.player.sendMessage(Text.of(s), false);
    }
}
