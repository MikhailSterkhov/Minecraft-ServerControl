package org.stonlexx.servercontrol.connection.server.execution.type;

import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.connection.server.execution.SimpleServerExecutionAdapter;

import java.util.Arrays;
import java.util.List;

public final class LinuxExecution extends SimpleServerExecutionAdapter {

    public static final String SHELL_SCRIPT
            = "screen -h 5000 -dmS %server_name% java -server -Xmx%server_memory% -Dfile.encoding=UTF-8 -jar %jar_name%";

    public LinuxExecution(ConnectedMinecraftServer minecraftServer) {
        super(SHELL_SCRIPT, "start.sh", minecraftServer);
    }

    @Override
    protected List<String> getProcessCommands() {
        return Arrays.asList("sh", "start.sh");
    }

}
