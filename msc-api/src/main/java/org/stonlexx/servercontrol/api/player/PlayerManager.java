package org.stonlexx.servercontrol.api.player;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.utility.query.ResponseHandler;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.UUID;

public interface PlayerManager {

    BasePlayer getPlayer(@NonNull String playerName);

    BasePlayer getPlayer(@NonNull InetSocketAddress inetSocketAddress);

    BasePlayer getPlayer(@NonNull UUID uniqueId);


    Collection<BasePlayer> getOnlinePlayers(ResponseHandler<Boolean, BasePlayer> responseHandler);

    Collection<BasePlayer> getOnlinePlayers();


    void addPlayer(@NonNull BasePlayer basePlayer);

    void removePlayer(@NonNull BasePlayer basePlayer);

}
