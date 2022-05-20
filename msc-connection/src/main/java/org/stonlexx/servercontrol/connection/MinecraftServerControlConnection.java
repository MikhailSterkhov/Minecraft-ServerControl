package org.stonlexx.servercontrol.connection;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.server.OSExecutionServer;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.api.utility.Instances;

public interface MinecraftServerControlConnection {

    static MinecraftServerControlConnection getInstance() {
        return Instances.getInstance(MinecraftServerControlConnection.class);
    }

    static void setInstance(MinecraftServerControlConnection minecraftServerControlConnection) {
        Instances.addInstance(MinecraftServerControlConnection.class, minecraftServerControlConnection);
    }

    void createDirectories();

    void processHandle(@NonNull String logMessage, @NonNull Runnable process);

    void resetRunningServers();

    void initMinecraftCommands();

    void initMinecraftServers();

    void initTicker(long millis);

    long getRunningMillis();

    OSExecutionServer getServerExecution(@NonNull ConnectedMinecraftServer server);
}
