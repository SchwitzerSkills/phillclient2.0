package at.phillip.client.commands;

import at.phillip.client.utils.ICommand;
import at.phillip.client.utils.MurderScanner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MurderCommand implements ICommand {
    @Override public String getName() { return "murder"; }

    @Override
    public boolean handle(String message) {
        if (!message.toLowerCase().startsWith("#" + getName())) return false;

        String[] args = message.trim().split("\\s+");
        if (args.length < 2) {
            send("§cUsage: #murder start [amount] | #murder stop | #murder near [radius]");
            return true;
        }

        String sub = args[1].toLowerCase();
        switch (sub) {
            case "start" -> {
                if (MurderScanner.isActive()) { send("§eAlready active. Stop first with §6#murder stop§e."); return true; }
                int amount = 1;
                if (args.length >= 3) {
                    try {
                        amount = Integer.parseInt(args[2]);
                        if (amount < 1) { send("§cAmount must be ≥ 1."); return true; }
                    } catch (NumberFormatException e) { send("§cInvalid number: " + args[2]); return true; }
                }
                MurderScanner.start(amount);
                send("§aMurder scan started for §2" + amount + "§a murderers.");
                return true;
            }
            case "stop" -> {
                if (!MurderScanner.isActive()) { send("§eNot active. Start with §6#murder start§e."); return true; }
                MurderScanner.stop();
                send("§eMurder scan stopped. Glow removed and list cleared.");
                return true;
            }
            case "near" -> {
                if (args.length < 3) {
                    send("§cUsage: #murder near <radius>");
                    return true;
                }
                double r;
                try {
                    r = Double.parseDouble(args[2]);
                    if (r < 1.0 || r > 64.0) {
                        send("§cRadius must be between 1 and 64.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    send("§cInvalid radius: " + args[2]);
                    return true;
                }
                MurderScanner.nearToggle(r);
                send(MurderScanner.isNearEnabled() ? "§7[Near] §aenabled §7(radius " + r + "m, only marked murderers)." : "§7[Near] §cdisabled");
                return true;
            }
            default -> {
                send("§cUnknown subcommand: " + sub);
                send("§7Usage: #murder start [amount] | #murder stop");
                return true;
            }
        }
    }

    private void send(String msg) {
        var mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.sendMessage(Text.of(msg), false);
    }
}
