package org.stonlexx.servercontrol.protocol;

public enum Protocol {

    HANDSHAKE, PLAY;

    public final PacketMapper TO_CLIENT = new PacketMapper();
    public final PacketMapper TO_SERVER = new PacketMapper();
}
