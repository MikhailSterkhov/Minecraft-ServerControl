package org.stonlexx.servercontrol.api.server;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.api.server.type.TemplateMinecraftServer;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface ServerManager {

    ConnectedMinecraftServer getConnectedServer(@NonNull String serverName);

    TemplateMinecraftServer getTemplateServer(@NonNull String serverName);


    void addConnectedServer(@NonNull ConnectedMinecraftServer connectedMinecraftServer);

    void addTemplateServer(@NonNull TemplateMinecraftServer templateMinecraftServer);


    Collection<ConnectedMinecraftServer> getMinecraftServers();

    Collection<TemplateMinecraftServer> getTemplateServers();


    TemplateMinecraftServer createTemplateServer(@NonNull String serverIndex,
                                                 @NonNull MinecraftServerType minecraftServerType);

    ConnectedMinecraftServer createConnectedServer(@NonNull TemplateMinecraftServer templateMinecraftServer,
                                                   @NonNull String serverIndex,

                                                   String serverVersion,
                                                   boolean downloadJar);
}
