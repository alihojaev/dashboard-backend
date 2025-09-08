package com.parser.core.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Base64Util {

    public static String encode(byte[] raw) {
        return Base64Utils.encodeToString(raw);
    }

    public static byte[] decode(String raw) {
        return Base64Utils.decodeFromString(raw);
    }
}
