package org.stonlexx.servercontrol.connection.player;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.player.BasePlayer;
import org.stonlexx.servercontrol.api.player.PlayerManager;
import org.stonlexx.servercontrol.api.utility.multimap.MultikeyHashMap;
import org.stonlexx.servercontrol.api.utility.multimap.MultikeyMap;
import org.stonlexx.servercontrol.api.utility.query.ResponseHandler;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SimplePlayerManager
        implements PlayerManager {

    private final MultikeyMap<BasePlayer> connectedPlayers = new MultikeyHashMap<BasePlayer>()

            .register(InetSocketAddress.class, BasePlayer::getInetSocketAddress)
            .register(UUID.class, BasePlayer::getUniqueId)

            .register(String.class, basePlayer -> basePlayer.getName().toLowerCase(Locale.ROOT));


    @Override
    public BasePlayer getPlayer(@NonNull String playerName) {
        return connectedPlayers.get(String.class, playerName.toLowerCase(Locale.ROOT));
    }

    @Override
    public BasePlayer getPlayer(@NonNull InetSocketAddress inetSocketAddress) {
        return connectedPlayers.get(InetSocketAddress.class, inetSocketAddress);
    }

    @Override
    public BasePlayer getPlayer(@NonNull UUID uniqueId) {
        return connectedPlayers.get(UUID.class, uniqueId);
    }


    @Override
    public Collection<BasePlayer> getOnlinePlayers(ResponseHandler<Boolean, BasePlayer> responseHandler) {
        if (responseHandler == null) {
            return connectedPlayers.valueCollection();
        }

        return connectedPlayers.valueCollection().stream()
                .filter(responseHandler::handleResponse).collect(Collectors.toSet());
    }

    @Override
    public Collection<BasePlayer> getOnlinePlayers() {
        return getOnlinePlayers(null);
    }


    @Override
    public void addPlayer(@NonNull BasePlayer basePlayer) {
        connectedPlayers.put(basePlayer);
    }

    @Override
    public void removePlayer(@NonNull BasePlayer basePlayer) {
        connectedPlayers.delete(basePlayer);
    }

}
