package org.stonlexx.servercontrol.api.utility;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.command.CommandManager;
import org.stonlexx.servercontrol.api.scheduler.TaskScheduler;
import org.stonlexx.servercontrol.api.server.MinecraftServerType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Log4j2
@UtilityClass
public class HttpDownloadUtil {

    private final Map<String, byte[]> cachedUrlsInput = new ConcurrentHashMap<>();

    @SneakyThrows
    public boolean downloadMinecraftServer(@NonNull Path directoryTo,
                                           @NonNull MinecraftServerType minecraftServerType,

                                           String minecraftVersion) {

        String downloadUrl = minecraftServerType.getDownloadUrl().replace("%version%", minecraftServerType.isMoreVersion() ? minecraftVersion : "");

        // Check the build of Paper project
        if (downloadUrl.contains("%last-build%")) {

            PaperMcUtil.GetPaperBuildsProject paperBuildsProject = PaperMcUtil.getPaperBuilds(minecraftServerType.name().toLowerCase(), minecraftVersion);

            if (paperBuildsProject != null) {
                downloadUrl = downloadUrl.replace("%last-build%", String.valueOf(paperBuildsProject.builds[paperBuildsProject.builds.length - 1]));
            }
        }

        // downloading jar file
        log.info(ChatColor.YELLOW + "[>] Downloading " + minecraftServerType.name() + " v" + minecraftVersion + " from " + downloadUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(downloadUrl).openConnection();

        if (urlConnection.getContentLength() <= 0) {
            log.info(ChatColor.RED + "[!] " + minecraftServerType.name() + " v" + minecraftVersion + " is`nt found or not exists!");

            return false;
        }

        boolean isDownloaded = downloadFromUrl(urlConnection, directoryTo);

        if (!isDownloaded) {
            log.info(ChatColor.RED + "[!] " + minecraftServerType.name() + " v" + minecraftVersion + " is`nt found or not exists!");
        }

        return isDownloaded;
    }

    private void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 : (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" : String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        int percent = (int) Math.round(PercentUtil.getPercent(current, total));

        String stringBuilder = "\r" +
                String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")) +

                String.format(" %d%% [", percent) +

                String.join("", Collections.nCopies(percent, "=")) + '>' +
                String.join("", Collections.nCopies(100 - percent, " ")) + ']' +
                String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")) +

                String.format(" (%s/%s KB) [Time left: %s]", NumberUtil.spaced((int) current), NumberUtil.spaced((int) total), etaHms);

        System.out.print(stringBuilder);
    }

    @SneakyThrows
    public boolean downloadFromUrl(@NonNull HttpURLConnection urlConnection,
                                   @NonNull Path directoryTo) {

        String urlString = urlConnection.getURL().toString();

        if (cachedUrlsInput.containsKey(urlString)) {
            FileUtil.createAndOutput(directoryTo.toFile(), handler -> handler.write(cachedUrlsInput.get(urlString)));
            return true;
        }

        try (InputStream inputStream = urlConnection.getInputStream()) {
            TaskScheduler progressTask = new TaskScheduler() {

                private final long startTime = System.currentTimeMillis();
                private long last;

                private final File file = directoryTo.toFile();

                @Override
                public void run() {
                    if (file.exists()) {

                        long current = (file.length() / 1024);
                        long max = (urlConnection.getContentLengthLong() / 1024);

                        if (current != last) {

                            printProgress(startTime, max, current);
                            last = current;
                        }
                    }
                }
            };

            CommandManager commandManager = MinecraftServerControlApi.getInstance().getServiceManager().getCommandManager();
            commandManager.openResponseSession(null);

            progressTask.runTimer(0, 1, TimeUnit.SECONDS);

            Files.copy(inputStream, directoryTo);
            cachedUrlsInput.put(urlString, FileUtil.toByteArray(directoryTo.toFile()));

            String length = NumberUtil.spaced((int) (urlConnection.getContentLengthLong() / 1024));
            System.out.println("\r[>] Success downloaded [" + length + "/" + length + " KB] 100%!");

            progressTask.cancel();
            commandManager.getCurrentResponseSession().closeSession();
            return true;
        }

        catch (Exception ignored) {
            return false;
        }

        finally {
            urlConnection.disconnect();
            System.gc();
        }
    }
}
