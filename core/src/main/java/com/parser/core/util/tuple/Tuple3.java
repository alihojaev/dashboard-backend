package com.parser.core.util.tuple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Tuple3<T1, T2, T3> implements Tuple {
    T1 item1;
    T2 item2;
    T3 item3;
}
