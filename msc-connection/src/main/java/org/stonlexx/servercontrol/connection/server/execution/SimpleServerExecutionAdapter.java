package org.stonlexx.servercontrol.connection.server.execution;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.scheduler.SchedulerManager;
import org.stonlexx.servercontrol.api.server.OSExecutionServer;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.api.utility.*;
import org.stonlexx.servercontrol.protocol.ChannelWrapper;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Getter
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class SimpleServerExecutionAdapter implements OSExecutionServer {

    String scriptCode;
    String scriptName;

    ConnectedMinecraftServer minecraftServer;

    @NonFinal Process process;
    @NonFinal Path serverRunFolder;

    protected abstract List<String> getProcessCommands();


    @SneakyThrows
    @Override
    public synchronized void runServer() {
        if (serverRunFolder == null) {
            serverRunFolder = MinecraftServerControlApi.getInstance().getServiceManager().getRunningDirectory().resolve(minecraftServer.getName());
        }

        // Creating the server run folder
        if (!Files.exists(serverRunFolder)) {
            Files.createDirectories(serverRunFolder);
        }

        // Start the server
        File jarFile = Arrays.stream(Objects.requireNonNull(minecraftServer.getTemplateDirectory().toFile().listFiles()))
                .filter(file -> file.getName().endsWith(".jar"))
                .findFirst()
                .orElse(null);

        if (jarFile == null) {
            log.error(ChatColor.RED + "[!] Server .jar is`nt exists!");

            return;
        }

        Directories.copyDirectory(minecraftServer.getTemplateDirectory(), minecraftServer.getRunningDirectory());
        Files.deleteIfExists(minecraftServer.getRunningDirectory().resolve("mccontrol.properties"));

        execute(jarFile.getName());
    }

    @Override
    public synchronized void shutdownServer() {
        if (process != null) {

            process.destroy();
            process = null;
        }
    }

    @SneakyThrows
    @Override
    public synchronized void execute(@NonNull String jarFile) {
        if ((process != null && process.isAlive()) || minecraftServer.isRunning()) {
            return;
        }

        // create a file
        Path bashFile = minecraftServer.getRunningDirectory().resolve(getScriptName());

        if (!Files.exists(bashFile)) {
            Files.createFile(bashFile);
        }

        // build batch commands
        FileUtil.write(bashFile.toFile(), fileWriter -> {

            String script = Placeholders.parseText(getScriptCode())
                    .addLocalPlaceholder("%jar_name%", jarFile)
                    .addLocalPlaceholder("%server_name%", minecraftServer.getName())
                    .addLocalPlaceholder("%server_memory%", minecraftServer.getTotalMemory())
                    .asString(minecraftServer);

            fileWriter.write(script);
        });

        // start the process
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(getProcessCommands());

        processBuilder.directory(minecraftServer.getRunningDirectory().toFile());
        ProcessExecutionUtil.addProcessTask(process = processBuilder.start());


        // create server.properties
        Properties serverProperties = new Properties();
        File serverPropertiesFile = minecraftServer.getRunningDirectory().resolve("server.properties").toFile();

        serverPropertiesFile.createNewFile();

        FileUtil.read(serverPropertiesFile, serverProperties::load);
        int startPort = minecraftServer.getTemplate().getStartPort();

        if (startPort > 0) {
            serverProperties.setProperty("server-port", String.valueOf(startPort + Integer.parseInt(minecraftServer.getTemplateDirectory().toFile().getName())));

        } else {

            serverProperties.setProperty("server-port", String.valueOf(NumberUtil.randomInt(1000, 45000)));
        }

        serverProperties.setProperty("server-ip", "127.0.0.1");
        serverProperties.setProperty("server-name", minecraftServer.getName());

        FileUtil.write(serverPropertiesFile, handler -> serverProperties.store(handler, null));


        // Если серв самостоятельно или вручную будет остановлен, чекаем это
        checkShutdown(serverProperties);
    }


    private Bootstrap createInboundBootstrap(@NonNull Properties serverProperties) {
        return new Bootstrap()
                .remoteAddress(new InetSocketAddress("localhost", Integer.parseInt(serverProperties.getProperty("server-port"))))

                .channel(NioSocketChannel.class)

                .group(new NioEventLoopGroup(2))
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);

                        channel.pipeline().addLast("shutdown-handler", new ShutdownChannelHandler(serverProperties));
                    }
                });
    }

    private void checkShutdown(@NonNull Properties serverProperties) {
        Bootstrap bootstrap = createInboundBootstrap(serverProperties);

        SchedulerManager schedulerManager = MinecraftServerControlApi.getInstance().getServiceManager().getSchedulerManager();
        String schedulerId = ("checkShutdown" + minecraftServer.getName());

        schedulerManager.runTimer(schedulerId,
                new Runnable() {

                    private long timeoutCounter = 0;
                    private long connectionCounter = 0;

                    @Override
                    public void run() {
                        if (connectionCounter <= 0) {
                            timeoutCounter++;

                            if (timeoutCounter >= TimeUnit.MINUTES.toSeconds(1)) {
                                schedulerManager.cancelScheduler(schedulerId);

                                minecraftServer.onShutdown();
                                shutdownServer();
                                return;
                            }
                        }

                        bootstrap.connect().addListener((ChannelFutureListener) (future) -> {
                            timeoutCounter = 0;

                            if (minecraftServer.isRunning()) {
                                return;
                            }

                            if (future.isSuccess()) {
                                minecraftServer.setChannel(new ChannelWrapper(future.channel()));
                                minecraftServer.onStart();

                                log.info(ChatColor.GREEN + "MSC :: Server '" + minecraftServer.getName() + "' was success connected!");

                                schedulerManager.cancelScheduler(schedulerId);

                            } else {
                                connectionCounter++;

                                if (connectionCounter >= 30) {
                                    schedulerManager.cancelScheduler(schedulerId);

                                    minecraftServer.onShutdown();
                                    shutdownServer();

                                    log.info(ChatColor.RED + "MSC :: No response server '" + minecraftServer.getName() + "' connection: Timeout");
                                }
                            }
                        });
                    }

                }, 10, 1, TimeUnit.SECONDS);
    }

    @RequiredArgsConstructor
    private final class ShutdownChannelHandler extends ChannelInboundHandlerAdapter {

        private final Properties serverProperties;

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error(minecraftServer.getName() + " <-> " + cause.getMessage(), cause);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            if (!minecraftServer.isRunning()) {
                return;
            }

            // Вдруг он отключился из-за какого-то таймаута или неактивности
            createInboundBootstrap(serverProperties).connect().addListener(future -> {

                if (!future.isSuccess()) {
                    log.info(ChatColor.RED + "MSC :: Server '" + minecraftServer.getName() + "' was disconnected!");

                    minecraftServer.onShutdown();
                }
            });
        }
    }
}
