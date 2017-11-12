package com.mycompany.grifon.mm_pre_alpha.events;

public abstract class EventWithSingleArg<T> {
    public T getArg() {
        return arg;
    }

    final T arg;

    public EventWithSingleArg(T arg) {
        this.arg = arg;
    }
}
