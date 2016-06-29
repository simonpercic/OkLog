package com.github.simonpercic.oklog;

import com.github.simonpercic.oklog.core.LogManager;
import com.github.simonpercic.oklog.coretest.BaseLogDataInterceptorUnitTest;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Locale;

import okio.Buffer;

/**
 * LogDataInterceptor unit test. Inspired by: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class LogDataInterceptorUnitTest extends BaseLogDataInterceptorUnitTest<MockResponse, Request> {

    private static final MediaType PLAIN = MediaType.parse(PLAIN_STRING);

    @Rule public final MockWebServer server = new MockWebServer();

    private OkHttpClient client;
    private HttpUrl url;

    @Override protected String createClient(LogManager applicationLogManager, LogManager networkLogManager) {
        OkLogInterceptor applicationInterceptor = new OkLogInterceptor(applicationLogManager);
        OkLogInterceptor networkInterceptor = new OkLogInterceptor(networkLogManager);

        client = new OkHttpClient();
        client.networkInterceptors().add(networkInterceptor);
        client.interceptors().add(applicationInterceptor);
        client.setConnectionPool(null);

        url = server.url("/");

        return String.format(Locale.US, "%s:%d", server.getHostName(), server.getPort());
    }

    @Override protected void newCall(MockResponse mockResponse) throws IOException {
        newCall(request().build(), mockResponse);
    }

    @Override protected void newCall(Request request, MockResponse mockResponse) throws IOException {
        server.enqueue(mockResponse);
        Response response = client.newCall(request).execute();
        response.body().close();
    }

    @Override protected String getUrlString() {
        return url.toString();
    }

    @Override protected MockResponse createMockResponse() {
        return new MockResponse();
    }

    @Override protected MockResponse setMockResponseStatus(MockResponse mockResponse, String status) {
        return mockResponse.setStatus(status);
    }

    @Override protected MockResponse setMockResponseBody(MockResponse mockResponse, String body) {
        return mockResponse.setBody(body);
    }

    @Override protected MockResponse setMockResponseBody(MockResponse mockResponse, Buffer body) {
        return mockResponse.setBody(body);
    }

    @Override protected MockResponse setMockResponsePlainContentTypeHeader(MockResponse mockResponse) {
        return mockResponse.setHeader(CONTENT_TYPE, PLAIN);
    }

    @Override protected MockResponse setMockResponseHeader(MockResponse mockResponse, String name, Object value) {
        return mockResponse.setHeader(name, value);
    }

    @Override protected MockResponse setMockResponseChunked(MockResponse mockResponse, String body, int maxChunkSize) {
        return mockResponse.setChunkedBody(body, maxChunkSize);
    }

    @Override protected Request createPlainPostRequest(String body) {
        return request().post(RequestBody.create(PLAIN, body)).build();
    }

    private Request.Builder request() {
        return new Request.Builder().url(url);
    }
}
