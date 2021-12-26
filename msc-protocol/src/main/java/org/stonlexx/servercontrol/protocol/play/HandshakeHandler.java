package org.stonlexx.servercontrol.protocol.play;

import org.stonlexx.servercontrol.protocol.PacketHandler;
import org.stonlexx.servercontrol.protocol.play.client.CHandshakePacket;
import org.stonlexx.servercontrol.protocol.play.server.SHandshakePacket;

public interface HandshakeHandler extends PacketHandler {

    default void handle(CHandshakePacket handshakePacket) {
    }

    default void handle(SHandshakePacket handshakePacket) {
    }

}
