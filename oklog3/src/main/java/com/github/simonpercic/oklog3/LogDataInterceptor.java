package com.github.simonpercic.oklog3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.simonpercic.oklog.core.BaseLogDataInterceptor;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor.Chain;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Log data interceptor util. Inspired by: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
class LogDataInterceptor extends BaseLogDataInterceptor<Chain, Request, Response, Headers, MediaType> {

    @NonNull private final HasResponseBodyManager hasResponseBodyManager;

    LogDataInterceptor() {
        hasResponseBodyManager = HasResponseBodyManager.create();
    }

    @Override protected Request request(Chain chain) {
        return chain.request();
    }

    @Override protected String protocol(Chain chain) {
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        return protocol.toString();
    }

    @Override protected String requestMethod(Request request) {
        return request.method();
    }

    @Override protected String requestUrl(Request request) {
        return request.url().toString();
    }

    @Override protected String requestUrlPath(Request request) {
        return request.url().encodedPath();
    }

    @Override protected String responseUrl(Response response) {
        return response.request().url().toString();
    }

    @Override protected Headers requestHeaders(Request request) {
        return request.headers();
    }

    @Override protected Headers responseHeaders(Response response) {
        return response.headers();
    }

    @Override protected int headersCount(Headers headers) {
        return headers.size();
    }

    @Override protected String headerName(Headers headers, int index) {
        return headers.name(index);
    }

    @Override protected String headerValue(Headers headers, int index) {
        return headers.value(index);
    }

    @Override protected String headerValue(Headers headers, String name) {
        return headers.get(name);
    }

    @Override protected boolean hasRequestBody(Request request) {
        return request.body() != null;
    }

    @Override protected boolean hasResponseBody(Response response) {
        return hasResponseBodyManager.hasBody(response);
    }

    @Override protected int responseCode(Response response) {
        return response.code();
    }

    @Override protected String responseMessage(Response response) {
        return response.message();
    }

    @Override protected long requestContentLength(Request request) throws IOException {
        //noinspection ConstantConditions
        return request.body().contentLength();
    }

    @Override protected long responseContentLength(Response response) throws IOException {
        //noinspection ConstantConditions
        return response.body().contentLength();
    }

    @Override protected MediaType requestContentType(Request request) {
        //noinspection ConstantConditions
        return request.body().contentType();
    }

    @Override protected MediaType responseContentType(Response response) {
        //noinspection ConstantConditions
        return response.body().contentType();
    }

    @Override protected String contentTypeString(MediaType mediaType) {
        return mediaType.toString();
    }

    @Override protected Charset contentTypeCharset(MediaType mediaType, Charset charset) {
        return mediaType.charset(charset);
    }

    @Nullable @Override protected Charset responseContentTypeCharset(MediaType contentType, Charset charset) {
        return contentTypeCharset(contentType, charset);
    }

    @Override protected void writeRequestBody(Request request, Buffer buffer) throws IOException {
        //noinspection ConstantConditions
        request.body().writeTo(buffer);
    }

    @Override protected BufferedSource responseBodySource(Response response) throws IOException {
        //noinspection ConstantConditions
        return response.body().source();
    }
}
