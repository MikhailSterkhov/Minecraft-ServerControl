package org.stonlexx.servercontrol.api.utility;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class FileUtil {

    @SneakyThrows
    public void read(@NonNull File file,
                     @NonNull ThrowableFileHandler<FileReader> fileHandler) {

        FileReader fileReader = new FileReader(file);
        fileHandler.handle(fileReader);

        fileReader.close();
    }

    @SneakyThrows
    public void write(@NonNull File file,
                      @NonNull ThrowableFileHandler<FileWriter> fileHandler) {

        FileWriter fileWriter = new FileWriter(file);
        fileHandler.handle(fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }

    @SneakyThrows
    public void input(@NonNull File file,
                      @NonNull ThrowableFileHandler<FileInputStream> fileHandler) {

        FileInputStream fileInputStream = new FileInputStream(file);
        fileHandler.handle(fileInputStream);

        fileInputStream.close();
    }

    @SneakyThrows
    public void output(@NonNull File file,
                       @NonNull ThrowableFileHandler<FileOutputStream> fileHandler) {

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileHandler.handle(fileOutputStream);

        fileOutputStream.flush();
        fileOutputStream.close();
    }

    @SneakyThrows
    public void createAndWrite(@NonNull File file,
                               @NonNull ThrowableFileHandler<FileWriter> fileHandler) {

        if (!Files.exists(file.toPath())) {
            Files.createFile(file.toPath());
        }

        write(file, fileHandler);
    }

    @SneakyThrows
    public void createAndOutput(@NonNull File file,
                                @NonNull ThrowableFileHandler<FileOutputStream> fileHandler) {

        if (!Files.exists(file.toPath())) {
            Files.createFile(file.toPath());
        }

        output(file, fileHandler);
    }

    @SneakyThrows
    public void deleteAndRead(@NonNull File file,
                              @NonNull ThrowableFileHandler<FileReader> fileHandler) {

        read(file, fileHandler);
        Files.deleteIfExists(file.toPath());
    }

    @SneakyThrows
    public void downloadFile(@NonNull String downloadUrl,
                             @NonNull Path toFilePath,
                             @NonNull ThrowableFileHandler<HttpURLConnection> preDownloadHandler) {

        if (Files.exists(toFilePath)) {
            Files.delete(toFilePath);
        }

        URLConnection urlConnection = new URL(downloadUrl).openConnection();
        HttpURLConnection httpURLConnection = ((HttpURLConnection) urlConnection);

        preDownloadHandler.handle(httpURLConnection);

        Files.copy(httpURLConnection.getInputStream(), toFilePath);

        System.gc();
        httpURLConnection.disconnect();
    }

    @SneakyThrows
    public byte[] toByteArray(File file) {
        FileInputStream inputStream = new FileInputStream(file);

        byte[] arr = new byte[(int) file.length()];

        inputStream.read(arr);
        inputStream.close();

        return arr;
    }

    public interface ThrowableFileHandler<H> {

        void handle(H handler) throws Exception;
    }

}
