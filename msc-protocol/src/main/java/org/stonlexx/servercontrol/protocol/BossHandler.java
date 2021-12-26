package org.stonlexx.servercontrol.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@ChannelHandler.Sharable
public class BossHandler extends SimpleChannelInboundHandler<MinecraftPacket<PacketHandler>> {

    @Getter
    @Setter
    private PacketHandler handler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MinecraftPacket<PacketHandler> msg) throws Exception {
        handler.handle(msg);
    }

    @Override
    public void channelActive(@NonNull ChannelHandlerContext ctx) {
        handler.channelActive(new ChannelWrapper(ctx.channel()));
    }

    @Override
    public void channelInactive(@NonNull ChannelHandlerContext ctx) {
        handler.channelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        handler.handle(cause);
    }
}
