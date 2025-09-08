package com.parser.core.util.functional;

@FunctionalInterface
public interface BiConsumerE<T, U, E extends Exception> {

    void accept(T t, U u) throws E;

}
