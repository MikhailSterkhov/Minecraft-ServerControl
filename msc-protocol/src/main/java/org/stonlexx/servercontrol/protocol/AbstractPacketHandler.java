package org.stonlexx.servercontrol.protocol;

import java.util.HashSet;
import java.util.Set;

public class AbstractPacketHandler implements PacketHandler {

    private final Set<PacketHandler> handlers = new HashSet<>();

    @Override
    public void handle(MinecraftPacket<PacketHandler> msg) throws Exception {
        for (PacketHandler handler : handlers) {
            handler.handle(msg);
        }
    }

    @Override
    public void channelActive(ChannelWrapper wrapper) {
        for (PacketHandler handler : handlers) {
            handler.channelActive(wrapper);
        }
    }

    @Override
    public void channelInactive() {
        for (PacketHandler handler : handlers) {
            handler.channelInactive();
        }
    }

    @Override
    public void addHandler(PacketHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void removeHandler(PacketHandler handler) {
        handlers.remove(handler);
    }
}
