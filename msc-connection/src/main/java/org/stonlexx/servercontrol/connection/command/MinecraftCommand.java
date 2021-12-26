package org.stonlexx.servercontrol.connection.command;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.command.BaseCommand;
import org.stonlexx.servercontrol.api.command.CommandResponseSession;
import org.stonlexx.servercontrol.connection.command.tab.CommandTabCompleteBuilder;

import java.util.function.BiConsumer;

@Getter
public abstract class MinecraftCommand
        implements BaseCommand {

    private final Multimap<String, String> tabCompletable = HashMultimap.create();

    private final String commandName;
    private final String[] commandAliases;

    public MinecraftCommand(@NonNull String commandName, @NonNull String... commandAliases) {
        this.commandName = commandName;
        this.commandAliases = commandAliases;
    }

    @Setter
    protected boolean onlyConsole = false; // This command only for the console


    protected CommandTabCompleteBuilder newTabCompleteBuilder() {
        return new CommandTabCompleteBuilder(this);
    }

    protected void openResponseSession(@NonNull BiConsumer<CommandResponseSession, String> responseConsumer) {
        MinecraftServerControlApi.getInstance().getServiceManager().getCommandManager()
                .openResponseSession(responseConsumer);
    }

}
