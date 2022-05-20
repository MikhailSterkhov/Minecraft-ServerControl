package org.stonlexx.servercontrol.api.player;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.command.CommandSender;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.protocol.UnsafeConnection;

import java.net.InetSocketAddress;
import java.util.UUID;

public interface BasePlayer extends CommandSender, UnsafeConnection {

    String getName();

    UUID getUniqueId();

    InetSocketAddress getInetSocketAddress();

    ConnectedMinecraftServer getConnectedServer();

    ConnectedMinecraftServer getConnectedProxy();

    void connect(@NonNull ConnectedMinecraftServer connectedMinecraftServer);

    void connect(@NonNull String serverName);
}
