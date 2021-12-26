package org.stonlexx.servercontrol.protocol.play.client;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.stonlexx.servercontrol.protocol.MinecraftPacket;
import org.stonlexx.servercontrol.protocol.BufferedQuery;
import org.stonlexx.servercontrol.protocol.play.HandshakeHandler;

import java.net.InetSocketAddress;

@Getter
@NoArgsConstructor
public class CHandshakePacket extends MinecraftPacket<HandshakeHandler> {

    private String name;
    private String motd;

    private InetSocketAddress inetSocketAddress;

    private int versionId;

    private boolean bungee;


    @Override
    public void readPacket(ByteBuf byteBuf) {
        this.name = BufferedQuery.readString(byteBuf);
        this.motd = BufferedQuery.readString(byteBuf);

        this.inetSocketAddress = new InetSocketAddress(BufferedQuery.readString(byteBuf), BufferedQuery.readVarInt(byteBuf));

        this.versionId = BufferedQuery.readVarInt(byteBuf);

        this.bungee = BufferedQuery.readBoolean(byteBuf);
    }

    @Override
    public void handle(HandshakeHandler handshakeHandler) {
        handshakeHandler.handle(this);
    }
}
