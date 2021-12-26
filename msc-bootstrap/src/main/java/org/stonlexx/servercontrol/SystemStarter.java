package org.stonlexx.servercontrol;

import lombok.extern.log4j.Log4j2;

@Log4j2
public final class SystemStarter {

    public static void main(String[] args) {
        MinecraftServerControl minecraftServerControl = new MinecraftServerControl();

        minecraftServerControl.onStart();
        minecraftServerControl.getServiceManager().getTerminal().start();
    }

}
