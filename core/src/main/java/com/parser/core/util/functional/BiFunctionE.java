package com.parser.core.util.functional;

@FunctionalInterface
public interface BiFunctionE<T, U, R, E extends Exception> {
    R apply(T t, U u) throws E;
}
