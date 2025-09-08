package com.parser.core.util.functional;

@FunctionalInterface
public interface ConsumerE<T, E extends Exception> {

    void accept(T t) throws E;
}
