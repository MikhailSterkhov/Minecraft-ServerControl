package org.stonlexx.servercontrol.api.utility.multimap.cache;

import org.stonlexx.servercontrol.api.utility.multimap.MultikeyMap;

public interface MultikeyCacheMap<I> extends MultikeyMap<I> {

    void cleanUp();
}
