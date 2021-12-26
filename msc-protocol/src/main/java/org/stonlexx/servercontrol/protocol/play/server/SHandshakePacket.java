package org.stonlexx.servercontrol.protocol.play.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.stonlexx.servercontrol.protocol.MinecraftPacket;
import org.stonlexx.servercontrol.protocol.BufferedQuery;
import org.stonlexx.servercontrol.protocol.play.HandshakeHandler;

import java.net.InetSocketAddress;

@AllArgsConstructor
@NoArgsConstructor
public class SHandshakePacket extends MinecraftPacket<HandshakeHandler> {

    private String name;
    private String motd;

    private InetSocketAddress inetSocketAddress;

    private int versionId;

    private boolean bungee;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(name, byteBuf);
        BufferedQuery.writeString(motd, byteBuf);

        BufferedQuery.writeString(inetSocketAddress.getHostString(), byteBuf);
        BufferedQuery.writeVarInt(inetSocketAddress.getPort(), byteBuf);

        BufferedQuery.writeVarInt(versionId, byteBuf);

        BufferedQuery.writeBoolean(bungee, byteBuf);
    }

}
