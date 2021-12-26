package org.stonlexx.servercontrol.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import lombok.Setter;
import org.stonlexx.servercontrol.protocol.MinecraftPacket;
import org.stonlexx.servercontrol.protocol.BufferedQuery;
import org.stonlexx.servercontrol.protocol.Protocol;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Setter
    private Protocol protocol = Protocol.HANDSHAKE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            int id = BufferedQuery.readVarInt(in);

            if (id < 0) {
                throw new IllegalStateException(String.format("Bad packet id %s", id));
            }

            MinecraftPacket<?> minecraftPacket = protocol.TO_CLIENT.getPacket(id);

            if (minecraftPacket == null) {
                throw new DecoderException(String.format("Unable to decode, packet id %s not found packet", id));
            }

            try {
                minecraftPacket.readPacket(in);
            } catch (Exception e) {
                throw new DecoderException(String.format("Unable to decode packet %s", minecraftPacket), e);
            }

            out.add(minecraftPacket);

        } finally {
            in.skipBytes(in.readableBytes());
        }
    }
}
