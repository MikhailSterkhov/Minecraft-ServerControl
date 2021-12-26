package org.stonlexx.servercontrol.protocol.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.experimental.UtilityClass;
import org.stonlexx.servercontrol.protocol.AbstractPacketHandler;
import org.stonlexx.servercontrol.protocol.BossHandler;
import org.stonlexx.servercontrol.protocol.codec.varint.VarIntPacketDecoder;
import org.stonlexx.servercontrol.protocol.codec.varint.VarIntPacketEncoder;

@UtilityClass
public class PipelineUtil {

    private final int LOW_MARK = SystemPropertyUtil.getInt("ru.tynixcloud.core.low_mark", 2 << 18);
    private final int HIGH_MARK = SystemPropertyUtil.getInt("ru.tynixcloud.core.high_mark", 2 << 20);

    public final WriteBufferWaterMark MARK = new WriteBufferWaterMark(LOW_MARK, HIGH_MARK);

    public final String FRAME_DECODER = "frame-decoder";
    public final String FRAME_ENCODER = "frame-encoder";
    public final String PACKET_DECODER = "packet-decoder";
    public final String PACKET_ENCODER = "packet-encoder";
    public final String PACKET_HANDLER = "boss-handler";

    public final String TIMEOUT_HANDLER = "timeout-handler";

    public void initPipeline(Channel ch) {
        ch.pipeline().addLast(FRAME_DECODER, new VarIntPacketDecoder());
        ch.pipeline().addLast(FRAME_ENCODER, new VarIntPacketEncoder());

        BossHandler bossHandler = new BossHandler();
        bossHandler.setHandler(new AbstractPacketHandler());

        ch.pipeline().addLast(PACKET_HANDLER, bossHandler);
    }

    public EventLoopGroup getEventLoopGroup(int threads) {
        return Epoll.isAvailable() ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
    }

    public ChannelFactory<ServerSocketChannel> getChannelFactory() {
        return Epoll.isAvailable() ? EpollServerSocketChannel::new : NioServerSocketChannel::new;
    }

    public Class<? extends Channel> getClientChannel() {
        return Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
    }
}
