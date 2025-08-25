package at.phillip.client.commands;

import at.phillip.client.utils.ClientReloader;
import at.phillip.client.utils.ICommand;

public class ReloadCommand implements ICommand {
    @Override public String getName() { return "reload"; }
    @Override public boolean handle(String message) {
        if (!message.equalsIgnoreCase("#" + getName())) return false;
        try { ClientReloader.loadClient(); }
        catch (Throwable t) { t.printStackTrace(); }
        return true;
    }
}
