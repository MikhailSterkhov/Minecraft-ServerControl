package org.stonlexx.servercontrol.api.server.type;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.server.MinecraftServer;

import java.util.Collection;

public interface TemplateMinecraftServer extends MinecraftServer {

    int getStartPort();

    Collection<ConnectedMinecraftServer> getConnectedServers();
    Collection<ConnectedMinecraftServer> getActiveServers();

    ConnectedMinecraftServer getConnectedServer(@NonNull String serverName);

    void addConnectedServer(@NonNull ConnectedMinecraftServer connectedMinecraftServer);
}
