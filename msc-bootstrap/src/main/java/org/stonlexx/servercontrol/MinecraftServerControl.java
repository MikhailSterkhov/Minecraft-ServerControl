package org.stonlexx.servercontrol;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.ServiceManager;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.server.OSExecutionServer;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.connection.MinecraftServerControlConnection;
import org.stonlexx.servercontrol.connection.SimpleMinecraftServerControlConnection;
import org.stonlexx.servercontrol.connection.SimpleServiceManager;
import org.stonlexx.servercontrol.log.LogTerminal;

@Getter
@Setter
@Log4j2
public final class MinecraftServerControl
        implements MinecraftServerControlApi {

    public final Logger logger = log;

    private ServiceManager serviceManager;
    private boolean running;

    @Override
    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    @Override
    public void onStart() {

        // Start system running...
        log.info(ChatColor.YELLOW + "[>] Running MinecraftServerControl system:");

        // Initialize system services
        setRunning(true);
        initServices();

        // Create & initialize API realises
        MinecraftServerControlConnection connection = MinecraftServerControlConnection.getInstance();

        connection.processHandle("Creating all directories", connection::createDirectories);
        connection.processHandle("Loading of running servers", connection::resetRunningServers);
        connection.processHandle("Registering basic commands", connection::initMinecraftCommands);

        connection.initTicker(500);

        log.info(ChatColor.WHITE + "[>] Type \"/msc-help\" to print available commands list.\n");
    }

    @Override
    public void onShutdown() {

        // Reset & delete running servers
        for (ConnectedMinecraftServer connectedMinecraftServer : serviceManager.getServerManager().getMinecraftServers()) {
            OSExecutionServer executionServer = MinecraftServerControlConnection.getInstance().getServerExecution(connectedMinecraftServer);

            if (connectedMinecraftServer.isRunning()) {
                executionServer.shutdownServer();

                connectedMinecraftServer.onShutdown();
            }
        }

        // Stop the system.
        setRunning(false);

        System.exit(0);
    }


    private void initServices() {

        // service manager
        SimpleServiceManager simpleServiceManager = new SimpleServiceManager();
        simpleServiceManager.setTerminal(new LogTerminal(this));

        setServiceManager(simpleServiceManager);

        // instances
        MinecraftServerControlApi.setInstance(this);
        MinecraftServerControlConnection.setInstance(new SimpleMinecraftServerControlConnection(serviceManager, System.currentTimeMillis()));
    }

}
