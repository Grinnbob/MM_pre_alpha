package com.mycompany.grifon.mm_pre_alpha.engine.events;

/**
 * Created by Vlad on 05.11.2017.
 */

public abstract class EventWithSingleArg<T> {
    public T getArg() {
        return arg;
    }

    final T arg;

    public EventWithSingleArg(T arg) {
        this.arg = arg;
    }
}
