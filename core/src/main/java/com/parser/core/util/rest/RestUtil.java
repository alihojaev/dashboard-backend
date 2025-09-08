package com.parser.core.util.rest;

public interface RestUtil {
    <T extends BaseRestDto> ResponseResult<String> post(String url, T writeObject, String token);

    ResponseResult<String> post(String url, String body, String token);

    ResponseResult<String> post(String url, String body, String token, String tokenHeader);

    ResponseResult<String> get(String url);

    ResponseResult<String> get(String url, String token);

    ResponseResult<String> get(String url, String token, String tokenHeader);
}
