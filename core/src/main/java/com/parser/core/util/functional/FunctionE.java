package com.parser.core.util.functional;

@FunctionalInterface
public interface FunctionE<T, R, E extends Exception> {

    R apply(T t) throws E;
}
