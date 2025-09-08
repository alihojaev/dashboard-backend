package com.parser.server.controllers;

import com.parser.core.exceptions.BaseException;
import com.parser.core.util.dao.DaoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@Slf4j
@ControllerAdvice
@RestControllerAdvice
public class ExceptionHandler {

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(
            BaseException e
    ) {
        log.error("handleBaseException()", e);
        return ResponseEntity.status(e.getStatus().value())
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(DaoException.class)
    public Map<String, Object> handleDaoException(
            RuntimeException e
    ) {
        log.error("daoException()", e);
        return Collections.singletonMap("message", e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler({HttpMessageNotReadableException.class, NumberFormatException.class})
    public Map<String, String> badRequest(Exception e) {
        log.error("badRequest()", e);
        return Collections.singletonMap("message", e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public Map<String, String> runtime(Exception e) {
        log.error("", e);
        return Collections.singletonMap("message", e.getMessage());
    }
}
