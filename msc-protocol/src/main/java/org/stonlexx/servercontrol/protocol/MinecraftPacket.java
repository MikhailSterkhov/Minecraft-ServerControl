package org.stonlexx.servercontrol.protocol;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import org.stonlexx.servercontrol.protocol.PacketHandler;

public abstract class MinecraftPacket<T extends PacketHandler> {

    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        //throw new UnsupportedOperationException();
    }

    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        //throw new UnsupportedOperationException();
    }

    public void handle(@NonNull T t) throws Exception {
        //throw new UnsupportedOperationException();
    }
}
