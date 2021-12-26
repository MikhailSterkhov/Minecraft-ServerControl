package org.stonlexx.servercontrol.api.event.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.stonlexx.servercontrol.api.event.Event;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;

@RequiredArgsConstructor
@Getter
public class ServerConnectEvent extends Event {

    private final ConnectedMinecraftServer server;
    private final long connectTimeMillis;
}
