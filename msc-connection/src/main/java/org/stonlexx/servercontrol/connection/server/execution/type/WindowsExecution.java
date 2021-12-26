package org.stonlexx.servercontrol.connection.server.execution.type;

import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.connection.server.execution.SimpleServerExecutionAdapter;

import java.util.Arrays;
import java.util.List;

public final class WindowsExecution extends SimpleServerExecutionAdapter {

    public static final String BATCH_SCRIPT
            = "@ECHO OFF \ntitle %server_name% \n\njava -server -Xmx%server_memory% -Dfile.encoding=UTF-8 -jar %jar_name%";

    public WindowsExecution(ConnectedMinecraftServer minecraftServer) {
        super(BATCH_SCRIPT, "start.bat", minecraftServer);
    }

    @Override
    protected List<String> getProcessCommands() {
        return Arrays.asList("cmd.exe", "/c", "start", "start.bat");
    }

}
