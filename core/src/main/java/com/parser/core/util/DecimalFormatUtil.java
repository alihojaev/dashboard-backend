package com.parser.core.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DecimalFormatUtil {

    public static String format(BigDecimal val) {
        return new DecimalFormat("###0.00000000##", DecimalFormatSymbols.getInstance(Locale.US)).format(val);
    }
}
