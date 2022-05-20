package org.stonlexx.servercontrol.api.server;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MinecraftServerType {

    CRAFTBUKKIT(10, "https://cdn.getbukkit.org/craftbukkit/craftbukkit-%version%.jar", true),
    SPIGOT(20, "https://cdn.getbukkit.org/spigot/spigot-%version%.jar", true),
    PAPER(30, "https://papermc.io/api/v2/projects/paper/versions/%version%/builds/%last-build%/downloads/paper-%version%-%last-build%.jar", true),
    SPONGE_FORGE(40, "https://repo.spongepowered.org/maven/org/spongepowered/spongeforge/1.12.2-2838-7.3.1-RC4093/spongeforge-1.12.2-2838-7.3.1-RC4093.jar", false),
    SPONGE(40, "https://repo.spongepowered.org/maven/org/spongepowered/spongevanilla/1.12.2-7.3.1-RC395/spongevanilla-1.12.2-7.3.1-RC395.jar", false),
    BUNGEE(50, "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", false),
    WATERFALL(60, "https://papermc.io/api/v1/waterfall/%version%/%last-build%/download", true),
    ;

    public static final MinecraftServerType[] MINECRAFT_SERVER_VALUES = values();

    private final int serverLevel;
    private final String downloadUrl;

    private final boolean moreVersion;


    public static MinecraftServerType getTypeByLevel(int serverLevel) {
        for (MinecraftServerType minecraftServerType : MINECRAFT_SERVER_VALUES) {

            if (minecraftServerType.serverLevel == serverLevel) {
                return minecraftServerType;
            }
        }

        return null;
    }

    public static MinecraftServerType getTypeByName(@NonNull String enumName) {
        for (MinecraftServerType minecraftServerType : MINECRAFT_SERVER_VALUES) {

            if (minecraftServerType.name().equalsIgnoreCase(enumName)) {
                return minecraftServerType;
            }
        }

        return null;
    }

}
