package com.parser.server.config;

import com.parser.core.exceptions.BaseException;
import com.parser.core.util.dao.DaoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@ControllerAdvice
@RestControllerAdvice
public class AdminExceptionHandler {

    @ResponseBody
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(
            BaseException e
    ) {
        log.error("handleBaseException()", e);
        return ResponseEntity.status(e.getStatus().value())
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(DaoException.class)
    public Map<String, Object> handleDaoException(
            RuntimeException e
    ) {
        log.error("daoException()", e);
        return Collections.singletonMap("message", e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class, NumberFormatException.class})
    public Map<String, String> badRequest(Exception e) {
        log.error("badRequest()", e);
        return Collections.singletonMap("message", e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> runtime(Exception e) {
        log.error("", e);
        return Collections.singletonMap("message", e.getMessage());
    }
}
