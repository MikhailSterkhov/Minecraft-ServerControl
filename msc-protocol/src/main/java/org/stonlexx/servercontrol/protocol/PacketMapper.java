package org.stonlexx.servercontrol.protocol;

import gnu.trove.impl.Constants;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class PacketMapper {

    private final TObjectIntMap<Class<? extends MinecraftPacket<?>>> idToPackets =
            new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);

    private final TIntObjectMap<Supplier<MinecraftPacket<?>>> toIdPackets = new TIntObjectHashMap<>();

    public void registerPacket(int id, Class<? extends MinecraftPacket<?>> cls, Supplier<MinecraftPacket<?>> supplier) {
        if (id < 0) {
            return;
        }

        if (idToPackets.containsValue(id) || toIdPackets.containsKey(id)) {
            throw new PacketRegisteredException(String.format("Packet %s is already registered", id));
        }

        idToPackets.put(cls, id);
        toIdPackets.put(id, supplier);
    }

    public int getPacketId(MinecraftPacket<?> minecraftPacket) {
        return idToPackets.get(minecraftPacket.getClass());
    }

    public MinecraftPacket<?> getPacket(int id) {
        return toIdPackets.get(id).get();
    }
}
