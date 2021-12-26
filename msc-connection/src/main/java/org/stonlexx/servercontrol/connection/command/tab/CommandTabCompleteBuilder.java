package org.stonlexx.servercontrol.connection.command.tab;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.stonlexx.servercontrol.connection.command.MinecraftCommand;

import java.util.Arrays;

@RequiredArgsConstructor
public class CommandTabCompleteBuilder {

    private final MinecraftCommand minecraftCommand;
    private final Multimap<String, String> tabCompletableMap = HashMultimap.create();

    public CommandTabCompleteBuilder addTabComplete(@NonNull String commandArgument,
                                                    @NonNull String... tabComplete) {

        tabCompletableMap.putAll(commandArgument.toLowerCase(), Arrays.asList(tabComplete));
        return this;
    }

    public void create() {
        minecraftCommand.getTabCompletable().clear();
        minecraftCommand.getTabCompletable().putAll(tabCompletableMap);
    }
}
