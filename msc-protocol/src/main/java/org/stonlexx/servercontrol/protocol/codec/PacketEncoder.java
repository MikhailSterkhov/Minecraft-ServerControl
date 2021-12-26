package org.stonlexx.servercontrol.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import org.stonlexx.servercontrol.protocol.MinecraftPacket;
import org.stonlexx.servercontrol.protocol.BufferedQuery;
import org.stonlexx.servercontrol.protocol.Protocol;

public class PacketEncoder extends MessageToByteEncoder<MinecraftPacket<?>> {

    @Setter
    private Protocol protocol = Protocol.HANDSHAKE;

    @Override
    protected void encode(ChannelHandlerContext ctx, MinecraftPacket<?> msg, ByteBuf out) throws Exception {
        int id = protocol.TO_SERVER.getPacketId(msg);

        if (id < 0) {
            throw new EncoderException(String.format("Unable to encode bad packet id %s", id));
        }

        BufferedQuery.writeVarInt(id, out);
        msg.writePacket(out);
    }
}