package org.stonlexx.servercontrol.protocol;

public interface UnsafeConnection {

    ChannelWrapper getChannel();

    void setChannel(ChannelWrapper channel);

    void sendPacket(MinecraftPacket<?> nettyPacket);

}
