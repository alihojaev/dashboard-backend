package com.parser.core.util;

import com.parser.core.util.functional.SupplierE;

import java.util.Optional;

public class OptionalUtil {

    public static <T, E extends Exception> T orElseE(Optional<T> optional, SupplierE<T, E> orElse) throws E {
        return optional.isPresent() ? optional.get() : orElse.get();
    }
}
