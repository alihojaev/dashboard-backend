package com.parser.core.util.dao;

import com.parser.core.util.functional.FunctionE;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Closer implements AutoCloseable {

    Deque<AutoCloseable> closeableStack;

    public Closer() {
        closeableStack = new ConcurrentLinkedDeque<>();
    }

    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    log.error("close()", e);
                }
            }
        }
    }

    public <T extends AutoCloseable> T reg(T closable) {
        closeableStack.push(closable);
        return closable;
    }

    public <R extends AutoCloseable, T extends AutoCloseable, E extends Exception> R reg(T closable, FunctionE<T, R, E> second) throws E {
        closeableStack.push(closable);
        R r = second.apply(closable);
        closeableStack.push(r);
        return r;
    }

    public <R1 extends AutoCloseable, R2 extends AutoCloseable, T extends AutoCloseable, E extends Exception>
    R2 reg(T closable, FunctionE<T, R1, E> second, FunctionE<R1, R2, E> third) throws E {
        closeableStack.push(closable);
        R1 r1 = second.apply(closable);
        closeableStack.push(r1);
        R2 r2 = third.apply(r1);
        closeableStack.push(r2);
        return r2;
    }

    @Override
    public void close() {
        AutoCloseable closeable;
        while ((closeable = closeableStack.poll()) != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
}
