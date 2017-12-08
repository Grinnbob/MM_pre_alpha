package com.mycompany.mm_pre_alpha.engine.events;

public abstract class EventWithSingleArg<T> {
    public T getArg() {
        return arg;
    }

    final T arg;

    public EventWithSingleArg(T arg) {
        this.arg = arg;
    }
}
