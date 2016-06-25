package com.github.simonpercic.oklog3;

import com.github.simonpercic.oklog.core.LogDataBuilder;
import com.github.simonpercic.oklog.core.LogDataBuilder.HeaderData;
import com.github.simonpercic.oklog.core.LogManager;
import com.github.simonpercic.oklog.core.StringUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.ByteString;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

/**
 * LogDataInterceptor unit test. Inspired by: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class LogDataInterceptorUnitTest {

    private static final MediaType PLAIN = MediaType.parse("text/plain; charset=utf-8");
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String GET = "GET";
    private static final String MESSAGE_OK = "OK";
    private static final int RESPONSE_CODE_OK = 200;

    @Mock LogManager applicationLogManager;
    @Mock LogManager networkLogManager;

    @Rule public final MockWebServer server = new MockWebServer();

    private OkHttpClient client;
    private String host;
    private HttpUrl url;

    @Before public void setUp() {
        OkLogInterceptor applicationInterceptor = new OkLogInterceptor(applicationLogManager);
        OkLogInterceptor networkInterceptor = new OkLogInterceptor(networkLogManager);

        client = new OkHttpClient.Builder()
                .addInterceptor(applicationInterceptor)
                .addNetworkInterceptor(networkInterceptor)
                .build();

        host = String.format(Locale.US, "%s:%d", server.getHostName(), server.getPort());
        url = server.url("/");
    }

    @Test public void bodyGet() throws IOException {
        server.enqueue(new MockResponse());
        Response response = client.newCall(request().build()).execute();
        response.body().close();

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK);

        LogDataBuilder appValue = getLogData(applicationLogManager);

        assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());

        LogDataBuilder networkValue = getLogData(networkLogManager);
        assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
    }

    @Test public void bodyGet204() throws IOException {
        bodyGetNoBody(204);
    }

    @Test public void bodyGet205() throws IOException {
        bodyGetNoBody(205);
    }

    private void bodyGetNoBody(int code) throws IOException {
        server.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 " + code + " No Content"));
        Response response = client.newCall(request().build()).execute();
        response.body().close();

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(code)
                .responseMessage("No Content");

        LogDataBuilder appValue = getLogData(applicationLogManager);
        assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());

        LogDataBuilder networkValue = getLogData(networkLogManager);
        assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
    }

    @Test public void bodyPost() throws IOException {
        server.enqueue(new MockResponse());
        Request request = request().post(RequestBody.create(PLAIN, "Hi?")).build();
        Response response = client.newCall(request).execute();
        response.body().close();

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod("POST")
                .requestContentType(PLAIN.toString())
                .requestContentLength(3)
                .requestBody("Hi?")
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK);

        LogDataBuilder appValue = getLogData(applicationLogManager);
        assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());

        LogDataBuilder networkValue = getLogData(networkLogManager);
        assertData(expectedValue, appValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
    }

    @Test public void bodyResponseBody() throws IOException {
        server.enqueue(new MockResponse()
                .setBody("Hello!")
                .setHeader(CONTENT_TYPE, PLAIN));
        Response response = client.newCall(request().build()).execute();
        response.body().close();

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(6)
                .responseContentLength(6)
                .responseBody("Hello!");

        LogDataBuilder appValue = getLogData(applicationLogManager);
        assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN.toString()));

        LogDataBuilder networkValue = getLogData(networkLogManager);
        assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN.toString()));
    }

    @Test public void bodyResponseBodyChunked() throws IOException {
        server.enqueue(new MockResponse()
                .setChunkedBody("Hello!", 2)
                .setHeader(CONTENT_TYPE, PLAIN));
        Response response = client.newCall(request().build()).execute();
        response.body().close();

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(6)
                .responseContentLength(-1)
                .responseBody("Hello!");

        LogDataBuilder appValue = getLogData(applicationLogManager);
        assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), "Transfer-encoding", new PredicateEquals<>("chunked"));
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN.toString()));

        LogDataBuilder networkValue = getLogData(networkLogManager);
        assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(networkValue.getResponseHeaders(), "Transfer-encoding", new PredicateEquals<>("chunked"));
        assertHeaderData(networkValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN.toString()));
    }

    @Test public void bodyResponseNotIdentityEncoded() throws IOException {
        server.enqueue(new MockResponse()
                .setHeader("Content-Encoding", "gzip")
                .setHeader(CONTENT_TYPE, PLAIN)
                .setBody(new Buffer().write(ByteString.decodeBase64(
                        "H4sIAAAAAAAAAPNIzcnJ11HwQKIAdyO+9hMAAAA="))));
        Response response = client.newCall(request().build()).execute();
        response.body().close();

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(19)
                .responseContentLength(-1)
                .responseBody("Hello, Hello, Hello");

        LogDataBuilder appValue = getLogData(applicationLogManager);
        assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN.toString()));

        expectedValue
                .responseBodySize(0)
                .responseContentLength(29)
                .responseBodyState(LogDataBuilder.ENCODED_BODY)
                .responseBody(null);

        LogDataBuilder networkValue = getLogData(networkLogManager);
        assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(networkValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN.toString()));
        assertHeaderData(networkValue.getResponseHeaders(), "Content-Encoding", new PredicateEquals<>("gzip"));
    }

    @Test public void bodyGetMalformedCharset() throws IOException {
        String contentType = "text/html; charset=0";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, contentType)
                .setBody("Ignore This"));
        Response response = client.newCall(request().build()).execute();
        response.body().close();

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(0)
                .responseContentLength(11)
                .responseBodyState(LogDataBuilder.CHARSET_MALFORMED);

        LogDataBuilder appValue = getLogData(applicationLogManager);
        assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(contentType));

        LogDataBuilder networkValue = getLogData(networkLogManager);
        assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(networkValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(contentType));
    }

    @Test public void responseBodyIsBinary() throws IOException {
        Buffer buffer = new Buffer();
        buffer.writeUtf8CodePoint(0x89);
        buffer.writeUtf8CodePoint(0x50);
        buffer.writeUtf8CodePoint(0x4e);
        buffer.writeUtf8CodePoint(0x47);
        buffer.writeUtf8CodePoint(0x0d);
        buffer.writeUtf8CodePoint(0x0a);
        buffer.writeUtf8CodePoint(0x1a);
        buffer.writeUtf8CodePoint(0x0a);

        String contentType = "image/png; charset=utf-8";
        server.enqueue(new MockResponse()
                .setBody(buffer)
                .setHeader(CONTENT_TYPE, contentType));
        Response response = client.newCall(request().build()).execute();
        response.body().close();

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(9)
                .responseContentLength(9)
                .responseBodyState(LogDataBuilder.BINARY_BODY);

        LogDataBuilder appValue = getLogData(applicationLogManager);
        assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(contentType));

        LogDataBuilder networkValue = getLogData(networkLogManager);
        assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(networkValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(contentType));
    }

    @Test public void isPlaintext() throws IOException {
        assertTrue(LogDataInterceptor.isPlaintext(new Buffer()));
        assertTrue(LogDataInterceptor.isPlaintext(new Buffer().writeUtf8("abc")));
        assertTrue(LogDataInterceptor.isPlaintext(new Buffer().writeUtf8("new\r\nlines")));
        assertTrue(LogDataInterceptor.isPlaintext(new Buffer().writeUtf8("white\t space")));
        assertTrue(LogDataInterceptor.isPlaintext(new Buffer().writeByte(0x80)));
        assertFalse(LogDataInterceptor.isPlaintext(new Buffer().writeByte(0x00)));
        assertFalse(LogDataInterceptor.isPlaintext(new Buffer().writeByte(0xc0)));
    }

    private Request.Builder request() {
        return new Request.Builder().url(url);
    }

    private static void assertData(LogDataBuilder expected, LogDataBuilder value) {
        assertEquals(expected.getRequestMethod(), value.getRequestMethod());
        assertEquals(expected.getRequestUrl(), value.getRequestUrl());
        assertEquals(expected.getRequestContentType(), value.getRequestContentType());
        assertEquals(expected.getRequestContentLength(), value.getRequestContentLength());
        assertEquals(expected.getRequestBody(), value.getRequestBody());
        assertEquals(expected.getRequestBodyState(), value.getRequestBodyState());
        assertEquals(expected.isRequestFailed(), value.isRequestFailed());
        assertEquals(expected.getResponseCode(), value.getResponseCode());
        assertEquals(expected.getResponseMessage(), value.getResponseMessage());
        assertEquals(expected.getResponseUrl(), value.getResponseUrl());
        assertEquals(expected.getResponseContentLength(), value.getResponseContentLength());
        assertEquals(expected.getResponseBodyState(), value.getResponseBodyState());
        assertEquals(expected.getResponseBodySize(), value.getResponseBodySize());
        assertEquals(expected.getResponseBody(), value.getResponseBody());
    }

    private LogDataBuilder expectedLogData() {
        return new LogDataBuilder()
                .requestUrl(url.toString())
                .responseUrl(url.toString());
    }

    private static LogDataBuilder getLogData(LogManager logManager) {
        ArgumentCaptor<LogDataBuilder> appCaptor = ArgumentCaptor.forClass(LogDataBuilder.class);
        verify(logManager).log(appCaptor.capture());
        return appCaptor.getValue();
    }

    private void assertRequestHeaders(List<HeaderData> requestHeaders) {
        assertHeaderData(requestHeaders, "Host", new PredicateEquals<>(host));
        assertHeaderData(requestHeaders, "User-Agent", new PredicateStartsWith("okhttp"));
    }

    private void assertHeaderData(List<HeaderData> headers, String name, Predicate<String> assertPredicate) {
        if (headers == null || headers.size() == 0) {
            fail("Headers are empty");
        }

        if (StringUtils.isEmpty(name)) {
            fail("Header name is empty");
        }

        for (HeaderData header : headers) {
            if (name.equalsIgnoreCase(header.getName())) {
                assertPredicate.assertValue(header.getValue());
                return;
            }
        }

        fail(String.format("No header with name: %s", name));
    }

    class PredicateStartsWith implements Predicate<String> {

        final String expected;

        PredicateStartsWith(String expected) {
            this.expected = expected.toLowerCase(Locale.US);
        }

        @Override public void assertValue(String value) {
            assertTrue(value.toLowerCase(Locale.US).startsWith(expected));
        }
    }

    class PredicateEquals<T> implements Predicate<T> {

        final T expected;

        PredicateEquals(T expected) {
            this.expected = expected;
        }

        @Override public void assertValue(T value) {
            assertEquals(expected, value);
        }
    }

    interface Predicate<T> {
        void assertValue(T value);
    }
}
