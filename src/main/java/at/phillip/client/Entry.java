package at.phillip.client;

import at.phillip.client.commands.*;
import at.phillip.client.utils.*;
import net.fabricmc.api.ClientModInitializer;

import java.util.List;

public class Entry implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("[PhillClient] Fabric Client loaded!");
        CommandBus.registerOnce();
        CommandBus.setCommands(List.of(
                new CopyIpCommand(),
                new ReloadCommand(),
                new MurderCommand(),
                new FastPlaceCommand(),
                new FullBrightCommand(),
                new PlayerESPCommand(),
                new HideNameCommand()
        ));

        TickBus.bindOnce();
        TickBus.clear();
        TickBus.add(MurderScanner::onClientTick);
        TickBus.add(FastPlace::onClientTick);
        TickBus.add(FullBright::onClientTick);

        EventBus.bindOnce();
        EventBus.clearAll();
        EventBus.addOnJoin(client -> { MurderScanner.stop(); FastPlace.disable(); });
        EventBus.addOnDisconnect(client -> { MurderScanner.stop(); FastPlace.disable(); });

    }

    public void init() {
        System.out.println("PhillClient reloaded");
        CommandBus.setCommands(List.of(
                new CopyIpCommand(),
                new ReloadCommand(),
                new MurderCommand(),
                new FastPlaceCommand(),
                new FullBrightCommand(),
                new PlayerESPCommand(),
                new HideNameCommand()
        ));

        TickBus.clear();
        TickBus.add(MurderScanner::onClientTick);
        TickBus.add(FastPlace::onClientTick);
        TickBus.add(FullBright::onClientTick);

        EventBus.clearAll();
        EventBus.addOnJoin(client -> { MurderScanner.stop(); FastPlace.disable(); });
        EventBus.addOnDisconnect(client -> { MurderScanner.stop(); FastPlace.disable(); });
    }
}
