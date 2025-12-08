package CapableSimulator;

@FunctionalInterface
public interface EventListener<T> {

    void onEvent(T event);
}
