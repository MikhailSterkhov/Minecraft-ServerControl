package org.stonlexx.servercontrol.api.command;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.chat.ChatMessageType;
import org.stonlexx.servercontrol.api.chat.component.BaseComponent;
import org.stonlexx.servercontrol.api.chat.component.TextComponent;

public interface CommandSender {

    /**
     * Получить имя или ник отправителя команды.
     */
    String getName();

    /**
     * Получить выводимое имя отправителя команды.
     * <p>
     * Здесь могут быть префиксы к нику, суффиксы,
     * или другие фишки.
     */
    String getDisplayName();

    /**
     * Отправить сообщение
     *
     * @param chatMessageType - тип сообщения
     * @param baseComponents  - текст сообщения
     */
    void sendMessage(@NonNull ChatMessageType chatMessageType, @NonNull BaseComponent[] baseComponents);

    /**
     * Отправить сообщение
     *
     * @param chatMessageType - тип сообщения
     * @param message         - текст сообщения
     */
    default void sendMessage(@NonNull ChatMessageType chatMessageType, @NonNull String message) {
        sendMessage(chatMessageType, TextComponent.fromLegacyText(message));
    }

    /**
     * Отправить сообщение
     *
     * @param baseComponents - текст сообщения
     */
    default void sendMessage(@NonNull BaseComponent[] baseComponents) {
        sendMessage(ChatMessageType.CHAT, baseComponents);
    }

    /**
     * Отправить сообщение
     *
     * @param message - текст сообщения
     */
    default void sendMessage(@NonNull String message) {
        sendMessage(TextComponent.fromLegacyText(message));
    }
}
