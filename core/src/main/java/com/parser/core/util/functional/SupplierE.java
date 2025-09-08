package com.parser.core.util.functional;

@FunctionalInterface
public interface SupplierE<T, E extends Exception> {

    T get() throws E;
}
