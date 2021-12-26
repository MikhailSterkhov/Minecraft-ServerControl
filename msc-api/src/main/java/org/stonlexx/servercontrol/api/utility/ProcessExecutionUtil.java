package org.stonlexx.servercontrol.api.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class ProcessExecutionUtil {

    private final List<Process> ACTIVE_PROCESSES = new ArrayList<>();


    public void addProcessTask(@NonNull Process process) {
        ACTIVE_PROCESSES.add(process);
    }

    public void removeProcess(@NonNull Process process) {
        ACTIVE_PROCESSES.remove(process);
    }

    public void handleAll(@NonNull Consumer<Process> processConsumer) {
        for (Process process : ACTIVE_PROCESSES)
            processConsumer.accept(process);
    }

    public void destroyAll() {
        handleAll(Process::destroy);
    }
}
