package com.parser.core.util.rest;

import java.util.concurrent.TimeUnit;

public class Rest {

    private static final RestUtil restUtil = new RestUtilImpl(
            (int) TimeUnit.SECONDS.toMillis(30),
            (int) TimeUnit.MINUTES.toMillis(2)
    );

    public static <T extends BaseRestDto> ResponseResult<String> post(String url, T writeObject, String token) {
        return restUtil.post(url, writeObject, token);
    }

    public static <T extends BaseRestDto> ResponseResult<String> post(String url, T writeObject) {
        return restUtil.post(url, writeObject, null);
    }

    public static ResponseResult<String> post(String url, String body, String token) {
        return restUtil.post(url, body, token);
    }

    public static ResponseResult<String> post(String url, String body, String token, String tokenHeader) {
        return restUtil.post(url, body, token, tokenHeader);
    }

    public static ResponseResult<String> post(String url, String body) {
        return restUtil.post(url, body, null);
    }

    public static ResponseResult<String> get(String url) {
        return restUtil.get(url);
    }

    public static ResponseResult<String> get(String url, String token) {
        return restUtil.get(url, token);
    }

    public static ResponseResult<String> get(String url, String token, String tokenHeader) {
        return restUtil.get(url, token, tokenHeader);
    }
}
