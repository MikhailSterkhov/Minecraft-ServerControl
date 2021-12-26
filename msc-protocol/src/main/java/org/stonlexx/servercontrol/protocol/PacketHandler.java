package org.stonlexx.servercontrol.protocol;

public interface PacketHandler {

    default void addHandler(PacketHandler handler) {
        // add handler to channel
    }

    default void removeHandler(PacketHandler handler) {
        // remove handler from channel
    }

    default void handle(MinecraftPacket<PacketHandler> msg) throws Exception {
        msg.handle(this);

        // handle packet
    }

    default void channelActive(ChannelWrapper wrapper) {
        // active server or client channel
    }

    default void channelInactive() {
        // inactive server or client channel
    }

    default void handle(Throwable t) {
        // handle exception fucking error
    }

}
