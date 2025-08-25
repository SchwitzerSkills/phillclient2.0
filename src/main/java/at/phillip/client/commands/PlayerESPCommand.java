package at.phillip.client.commands;

import at.phillip.client.utils.ICommand;
import at.phillip.client.utils.PlayerESP;

public class PlayerESPCommand implements ICommand {
    @Override
    public String getName() {
        return "playeresp";
    }

    @Override
    public boolean handle(String message) {
        if(!message.toLowerCase().startsWith("#" + getName())) return false;
        PlayerESP.toggle();
        return false;
    }
}
