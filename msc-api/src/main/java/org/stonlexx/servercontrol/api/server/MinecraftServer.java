package org.stonlexx.servercontrol.api.server;

import java.nio.file.Path;
import java.util.regex.Pattern;

public interface MinecraftServer {

    Pattern MULTI_INDEX_PATTERN         = Pattern.compile("%([0-9]+)-([0-9]+)%");
    Pattern MINECRAFT_VERSION_PATTERN   = Pattern.compile("1\\.([1-9]{2}|[1-9])(\\.[1-9])?");


    String getName();

    Path getTemplateDirectory();

    MinecraftServerType getServerType();
}
