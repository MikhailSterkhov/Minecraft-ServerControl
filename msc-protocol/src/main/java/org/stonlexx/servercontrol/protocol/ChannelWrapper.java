package org.stonlexx.servercontrol.protocol;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import org.stonlexx.servercontrol.protocol.codec.PacketDecoder;
import org.stonlexx.servercontrol.protocol.codec.PacketEncoder;

import java.net.SocketAddress;

public class ChannelWrapper {

    @Getter
    private final Channel channel;

    @Getter
    private final SocketAddress address;

    public ChannelWrapper(Channel channel) {
        this.channel = channel;
        this.address = channel.remoteAddress() == null ? channel.localAddress() : channel.remoteAddress();
    }

    public void handleConnect(PacketHandler handler, PacketHandler remove) {
        addHandler(handler);
        removeHandler(remove);

        setProtocol(Protocol.PLAY);
        handler.channelActive(this);
    }

    public void changeHandler(PacketHandler add, PacketHandler remove) {
        addHandler(add);
        removeHandler(remove);
    }

    public void addHandler(PacketHandler handler) {
        channel.pipeline().get(BossHandler.class).getHandler().addHandler(handler);
    }

    public void removeHandler(PacketHandler handler) {
        channel.pipeline().get(BossHandler.class).getHandler().removeHandler(handler);
    }

    public void write(MinecraftPacket<?> minecraftPacket) {
        channel.writeAndFlush(minecraftPacket);
    }

    public void setProtocol(Protocol protocol) {
        channel.pipeline().get(PacketEncoder.class).setProtocol(protocol);
        channel.pipeline().get(PacketDecoder.class).setProtocol(protocol);
    }

    public void close(MinecraftPacket<?> minecraftPacket) {
        if (channel.isActive() && minecraftPacket != null) {
            channel.writeAndFlush(minecraftPacket).addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, ChannelFutureListener.CLOSE);
            return;
        }

        channel.flush();
        channel.close();
    }

    public void addBefore(String before, String after, ChannelHandler handler) {
        Preconditions.checkState(channel.eventLoop().inEventLoop(), "cannot add handler outside of event loop");

        channel.pipeline().flush();
        channel.pipeline().addBefore(before, after, handler);
    }

    public void setCompression(int threshold) {
      // if (channel.pipeline().get(PacketCompressor.class) == null && threshold != -1) {
      //     addBefore(PipelineUtil.PACKET_ENCODER, "compress", new PacketCompressor());
      // }

      // if (threshold != -1) {
      //     channel.pipeline().get(PacketCompressor.class).setThreshold(threshold);
      // } else {
      //     channel.pipeline().remove("compress");
      // }

      // if (channel.pipeline().get(PacketDecompressor.class) == null && threshold != -1) {
      //     addBefore(PipelineUtil.PACKET_DECODER, "decompress", new PacketDecompressor());
      // }
      // if (threshold == -1) {
      //     channel.pipeline().remove("decompress");
      // }
    }
}
