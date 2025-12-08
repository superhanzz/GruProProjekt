package CapableSimulator;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;

public class Dispacher<T> {
    private final List<EventListener<T>> listeners = new ArrayList<>();

    public void addEventListener(EventListener<T> listener) {
        listeners.add(listener);
    }

    public void removeEventListener(EventListener<T> listener) {
        listeners.remove(listener);
    }

    public void dispach(T event) {
        for (EventListener<T> listener : listeners) {
            listener.onEvent(event);
        }
    }
}
