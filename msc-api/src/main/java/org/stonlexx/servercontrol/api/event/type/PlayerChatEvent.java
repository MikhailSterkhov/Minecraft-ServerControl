package org.stonlexx.servercontrol.api.event.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.stonlexx.servercontrol.api.chat.component.BaseComponent;
import org.stonlexx.servercontrol.api.event.Event;
import org.stonlexx.servercontrol.api.player.BasePlayer;

@RequiredArgsConstructor
@Getter
public class PlayerChatEvent extends Event {

    private final BasePlayer player;

    private final BaseComponent[] baseComponents;
    private final String textMessage;
}
