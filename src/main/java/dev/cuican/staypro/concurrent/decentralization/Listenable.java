package dev.cuican.staypro.concurrent.decentralization;

import dev.cuican.staypro.concurrent.task.EventTask;

import java.util.concurrent.ConcurrentHashMap;

public interface Listenable {

    ConcurrentHashMap<DecentralizedEvent<? extends EventData>, EventTask<? extends EventData>> listenerMap();

    void subscribe();

    void unsubscribe();

}
