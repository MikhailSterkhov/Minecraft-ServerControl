package org.stonlexx.servercontrol.api;

import org.apache.logging.log4j.Logger;
import org.stonlexx.servercontrol.api.utility.Instances;

public interface MinecraftServerControlApi {

    static MinecraftServerControlApi getInstance() {
        return Instances.getInstance(MinecraftServerControlApi.class);
    }

    static void setInstance(MinecraftServerControlApi minecraftServerControlApi) {
        Instances.addInstance(MinecraftServerControlApi.class, minecraftServerControlApi);
    }

    boolean isWindows();

    boolean isRunning();

    Logger getLogger();


    ServiceManager getServiceManager();

    void setServiceManager(ServiceManager serviceManger);


    void onStart();

    void onShutdown();
}
