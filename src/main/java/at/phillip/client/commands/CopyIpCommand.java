package at.phillip.client.commands;

import at.phillip.client.utils.ICommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.net.InetSocketAddress;

public class CopyIpCommand implements ICommand {
    @Override public String getName() { return "copyIp"; }

    @Override public boolean handle(String message) {
        if (!message.equalsIgnoreCase("#" + getName())) return false;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getCurrentServerEntry() == null || mc.getNetworkHandler() == null) {
            mc.player.sendMessage(Text.of("§cYou are not on any server!"), false);
            return true;
        }
        InetSocketAddress addr = (InetSocketAddress) mc.getNetworkHandler().getConnection().getAddress();
        String ip = addr.getAddress().getHostAddress() + ":" + addr.getPort();

        mc.keyboard.setClipboard(ip);
        mc.player.sendMessage(Text.of("§aIp copied: " + ip), false);
        return true;
    }
}
