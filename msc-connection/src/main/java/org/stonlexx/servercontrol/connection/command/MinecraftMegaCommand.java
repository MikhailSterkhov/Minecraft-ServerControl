package org.stonlexx.servercontrol.connection.command;

import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.stonlexx.servercontrol.api.command.BaseCommand;
import org.stonlexx.servercontrol.api.command.CommandSender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public abstract class MinecraftMegaCommand
        extends MinecraftCommand
        implements BaseCommand {

    private final Map<String, Method> commandArguments = new HashMap<>();


// ================================================================================================================ //

    protected final int minimalArgsCount;

    @Setter
    protected Consumer<CommandSender> noArgumentMessage;

// ================================================================================================================ //

    public MinecraftMegaCommand(int minimalArgsCount,
                                @NonNull String commandName, @NonNull String... commandAliases) {

        super(commandName, commandAliases);

        this.minimalArgsCount = minimalArgsCount;

        Arrays.stream(getClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(CommandArgument.class) != null)
                .filter(method -> method.getParameterCount() == 2 && (Arrays.equals(method.getParameterTypes(), new Class<?>[]{CommandSender.class, String[].class})))

                .forEach(method -> {
                    method.setAccessible(true);

                    commandArguments.put(method.getName().toLowerCase(Locale.ROOT), method);

                    for (String alias : method.getDeclaredAnnotation(CommandArgument.class).aliases()) {
                        commandArguments.put(alias.toLowerCase(Locale.ROOT), method);
                    }
                });

        setNoArgumentMessage(this::onUsage);
    }

    protected abstract void onUsage(@NonNull CommandSender commandSender);


    @Override
    @SneakyThrows
    public void onExecute(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        if (commandArgs.length == minimalArgsCount) {
            onUsage(commandSender);
            return;
        }

        Method argumentMethod = commandArguments.get(commandArgs[minimalArgsCount].toLowerCase());

        if (argumentMethod != null) {
            argumentMethod.invoke(this, commandSender, Arrays.copyOfRange(commandArgs, minimalArgsCount + 1, commandArgs.length));

        } else {

            noArgumentMessage.accept(commandSender);
        }
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface CommandArgument {

        String[] aliases() default {};
    }
}
