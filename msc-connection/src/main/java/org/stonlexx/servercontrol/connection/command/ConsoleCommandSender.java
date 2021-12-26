package org.stonlexx.servercontrol.connection.command;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.chat.ChatMessageType;
import org.stonlexx.servercontrol.api.chat.component.BaseComponent;
import org.stonlexx.servercontrol.api.chat.component.TextComponent;
import org.stonlexx.servercontrol.api.command.CommandSender;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ConsoleCommandSender implements CommandSender {

    public static final ConsoleCommandSender INSTANCE = new ConsoleCommandSender();


    @Override
    public String getName() {
        return "MSC";
    }

    @Override
    public String getDisplayName() {
        return (ChatColor.RED + getName());
    }


    @Override
    public void sendMessage(@NonNull ChatMessageType chatMessageType, @NonNull BaseComponent[] baseComponents) {
        log.info(TextComponent.toLegacyText(baseComponents));
    }

}
