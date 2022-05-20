package org.stonlexx.servercontrol.connection.player;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.chat.ChatMessageType;
import org.stonlexx.servercontrol.api.chat.component.BaseComponent;
import org.stonlexx.servercontrol.api.player.BasePlayer;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.protocol.ChannelWrapper;
import org.stonlexx.servercontrol.protocol.MinecraftPacket;
import org.stonlexx.servercontrol.protocol.UnsafeConnection;

import java.net.InetSocketAddress;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SimpleBasePlayer implements BasePlayer, UnsafeConnection {

    String name;
    UUID uniqueId;

    @NonFinal String displayName;

    @NonFinal ChannelWrapper channel;
    InetSocketAddress inetSocketAddress;

    @NonFinal ConnectedMinecraftServer connectedServer;
    ConnectedMinecraftServer connectedProxy;


    @Override
    public void sendMessage(@NonNull ChatMessageType chatMessageType, @NonNull BaseComponent[] baseComponents) {
        //todo: send ChatMessagePacket to the player
    }

    @Override
    public void connect(@NonNull ConnectedMinecraftServer connectedMinecraftServer) {
        if (connectedServer.equals(connectedMinecraftServer)) {
            return;
        }

        this.connectedServer = connectedMinecraftServer;
        //todo: send PlayerRedirectPacket to the proxy server
    }

    @Override
    public void connect(@NonNull String serverName) {
        ConnectedMinecraftServer minecraftServer = MinecraftServerControlApi.getInstance().getServiceManager()
                .getServerManager().getConnectedServer(serverName);

        if (minecraftServer == null) {
            return;
        }

        connect(minecraftServer);
    }

    @Override
    public void sendPacket(MinecraftPacket<?> nettyPacket) {
        // ...
    }

}
