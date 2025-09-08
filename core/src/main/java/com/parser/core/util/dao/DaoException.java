package com.parser.core.util.dao;

public class DaoException extends RuntimeException {

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message) {
        super(message);
    }
}
