package CapableSimulator.EventHandeling;

@FunctionalInterface
public interface EventListener<T> {

    void onEvent(T event);
}
