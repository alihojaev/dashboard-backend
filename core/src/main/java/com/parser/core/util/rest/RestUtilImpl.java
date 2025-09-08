package com.parser.core.util.rest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Supplier;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class RestUtilImpl implements RestUtil {

    private static final SimpleDateFormat format;

    static {
        format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    CloseableHttpClient httpClient;

    RestUtilImpl(
            int connectionTimeout,
            int socketTimeout
    ) {
        httpClient = buildClient(connectionTimeout, socketTimeout);
    }

    @Override
    public <T extends BaseRestDto> ResponseResult<String> post(String url, T writeObject, String token) {
        return post(url, writeObject.json(), token);
    }

    @Override
    public ResponseResult<String> post(String url, String body, String token) {
        StringEntity entity;
        try {
            entity = new StringEntity(body, "UTF-8");
        } catch (UnsupportedCharsetException e) {
            return new ResponseResult<>(0, "");
        }

        var httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        if (token != null) httpPost.setHeader("Authorization", token);
        httpPost.setEntity(entity);
        return getStringResponseResult(httpPost, () -> "POST '" + url + "', body: '" + body + "'");
    }

    @Override
    public ResponseResult<String> post(String url, String body, String token, String tokenHeader) {
        StringEntity entity;
        try {
            entity = new StringEntity(body, "UTF-8");
        } catch (UnsupportedCharsetException e) {
            return new ResponseResult<>(0, "");
        }

        var httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        if (token != null) httpPost.setHeader(tokenHeader, token);
        httpPost.setEntity(entity);
        return getStringResponseResult(httpPost, () -> "POST '" + url + "', body: '" + body + "'");
    }

    @Override
    public ResponseResult<String> get(String url) {
        return getStringResponseResult(new HttpGet(url), () -> "GET '" + url + "'");
    }

    @Override
    public ResponseResult<String> get(String url, String token) {
        var httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/json; charset=utf-8");
        if (token != null) httpGet.setHeader("Authorization", token);

        return getStringResponseResult(httpGet, () -> "GET '" + url + "'");
    }

    @Override
    public ResponseResult<String> get(String url, String token, String tokenHeader) {
        var httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/json; charset=utf-8");
        if (token != null) httpGet.setHeader(tokenHeader, token);

        return getStringResponseResult(httpGet, () -> "GET '" + url + "'");
    }

    private CloseableHttpClient buildClient(
            int connectionTimeout,
            int socketTimeout
    ) {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(socketTimeout)
                                .build()
                )
                .build();
    }

    private ResponseResult<String> getStringResponseResult(HttpRequestBase httpRequestBase, Supplier<String> infoSupplier) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpRequestBase);

            final var statusLine = response.getStatusLine();
            final var body = EntityUtils.toString(response.getEntity());

            return new ResponseResult<>(statusLine.getStatusCode(), body);
        } catch (UnknownHostException | ConnectTimeoutException | SSLHandshakeException | NoRouteToHostException |
                 SSLProtocolException e) {
            log.error("REST input: ({}), connection establish exception, exception: {}, message: {}", infoSupplier.get(), e.getClass().getCanonicalName(), e.getMessage());
            return new ResponseResult<>(504, e.getMessage());
        } catch (HttpHostConnectException | SocketTimeoutException e) {
            log.error("REST input: ({}), exception during making request to host, exception: {}, message: {}", infoSupplier.get(), e.getClass().getCanonicalName(), e.getMessage());
            return new ResponseResult<>(502, e.getMessage());
        } catch (IOException e) {
            if (log.isTraceEnabled()) log.error("REST input: (" + infoSupplier.get() + ")", e);
            else
                log.error("REST input: ({}), exception: {}, message: {}", infoSupplier.get(), e.getClass().getCanonicalName(), e.getMessage());
            return new ResponseResult<>(0, e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("REST input: (" + infoSupplier.get() + "), http client close problem", e);
                }
            }
        }
    }
}
