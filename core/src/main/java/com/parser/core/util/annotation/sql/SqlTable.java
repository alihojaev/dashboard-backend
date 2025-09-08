package com.parser.core.util.annotation.sql;

import org.intellij.lang.annotations.Language;

@Language(value = "sql", prefix = "SELECT * FROM ")
public @interface SqlTable {
}
