package com.parser.core.util.triplet;

public class Triplet<A, B, C> {

    public final A a;
    public final B b;
    public final C c;

    public Triplet(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getFirst() {
        return this.a;
    }

    public B getSecond() {
        return this.b;
    }

    public C getThird() {
        return this.c;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ")";
    }
}
