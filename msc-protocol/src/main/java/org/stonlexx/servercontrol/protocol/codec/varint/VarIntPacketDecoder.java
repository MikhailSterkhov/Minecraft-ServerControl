package org.stonlexx.servercontrol.protocol.codec.varint;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.stonlexx.servercontrol.protocol.BufferedQuery;

import java.util.List;

public class VarIntPacketDecoder extends ByteToMessageDecoder {

    private static boolean DIRECT_WARNING;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!ctx.channel().isActive()) {
            in.skipBytes(in.readableBytes());
            return;
        }

        in.markReaderIndex();

        final byte[] buf = new byte[3];

        for (int i = 0; i < buf.length; i++) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            buf[i] = in.readByte();

            if (buf[i] >= 0) {
                int length = BufferedQuery.readVarInt(Unpooled.wrappedBuffer(buf));
                if (length == 0) {
                    throw new CorruptedFrameException("Empty Packet!");
                }

                if (in.readableBytes() < length) {
                    in.resetReaderIndex();
                } else {
                    if (in.hasMemoryAddress()) {
                        out.add(in.slice(in.readerIndex(), length).retain());
                        in.skipBytes(length);
                    } else {
                        if (!DIRECT_WARNING) {
                            DIRECT_WARNING = true;
                            System.out.println("Netty is not using direct IO buffers.");
                        }

                        ByteBuf dst = ctx.alloc().directBuffer(length);

                        in.readBytes(dst);
                        out.add(dst);
                    }
                }
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}