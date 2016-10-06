package com.github.simonpercic.oklog.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okio.Buffer;
import okio.ByteString;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseLogDataInterceptorUnitTest<MockResponse, Request> {

    protected static final String PLAIN_STRING = "text/plain; charset=utf-8";
    protected static final String CONTENT_TYPE = "Content-Type";
    private static final String GET = "GET";
    private static final String MESSAGE_OK = "OK";
    private static final int RESPONSE_CODE_OK = 200;

    @Mock LogManager applicationLogManager;
    @Mock LogManager networkLogManager;

    private String host;

    // region Abstract methods

    protected abstract String createClient(LogManager applicationLogManager, LogManager networkLogManager);

    protected abstract void newCall(MockResponse mockResponse) throws IOException;

    protected abstract void newCall(Request request, MockResponse mockResponse) throws IOException;

    protected abstract String getUrlString();

    protected abstract MockResponse createMockResponse();

    protected abstract MockResponse setMockResponseStatus(MockResponse mockResponse, String status);

    protected abstract MockResponse setMockResponseBody(MockResponse mockResponse, String body);

    protected abstract MockResponse setMockResponseBody(MockResponse mockResponse, Buffer body);

    protected abstract MockResponse setMockResponsePlainContentTypeHeader(MockResponse mockResponse);

    protected abstract MockResponse setMockResponseHeader(MockResponse mockResponse, String name, Object value);

    protected abstract MockResponse setMockResponseChunked(MockResponse mockResponse, String body, int maxChunkSize);

    protected abstract Request createPlainPostRequest(String body);

    // endregion Abstract methods

    @Before public void setUp() {
        host = createClient(applicationLogManager, networkLogManager);
    }

    @Test public void bodyGet() throws IOException {
        newCall(createMockResponse());

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK);

        LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);

        TestUtils.assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());

        LogDataBuilder networkValue = TestUtils.getLogData(networkLogManager);
        TestUtils.assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
    }

    @Test public void bodyGet204() throws IOException {
        bodyGetNoBody(204);
    }

    @Test public void bodyGet205() throws IOException {
        bodyGetNoBody(205);
    }

    private void bodyGetNoBody(int code) throws IOException {
        MockResponse mockResponse = createMockResponse();
        mockResponse = setMockResponseStatus(mockResponse, "HTTP/1.1 " + code + " No Content");

        newCall(mockResponse);

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(code)
                .responseMessage("No Content");

        LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);
        TestUtils.assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());

        LogDataBuilder networkValue = TestUtils.getLogData(networkLogManager);
        TestUtils.assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
    }

    @Test public void bodyPost() throws IOException {
        Request request = createPlainPostRequest("Hi?");
        newCall(request, createMockResponse());

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod("POST")
                .requestContentType(PLAIN_STRING)
                .requestContentLength(3)
                .requestBody("Hi?")
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK);

        LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);
        TestUtils.assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());

        LogDataBuilder networkValue = TestUtils.getLogData(networkLogManager);
        TestUtils.assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
    }

    @Test public void bodyResponseBody() throws IOException {
        MockResponse mockResponse = createMockResponse();
        mockResponse = setMockResponseBody(mockResponse, "Hello!");
        mockResponse = setMockResponsePlainContentTypeHeader(mockResponse);

        newCall(mockResponse);

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(6)
                .responseContentLength(6)
                .responseBody("Hello!");

        LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);
        TestUtils.assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN_STRING));

        LogDataBuilder networkValue = TestUtils.getLogData(networkLogManager);
        TestUtils.assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN_STRING));
    }

    @Test public void bodyResponseBodyChunked() throws IOException {
        MockResponse mockResponse = createMockResponse();
        mockResponse = setMockResponseChunked(mockResponse, "Hello!", 2);
        mockResponse = setMockResponsePlainContentTypeHeader(mockResponse);

        newCall(mockResponse);

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(6)
                .responseContentLength(-1)
                .responseBody("Hello!");

        LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);
        TestUtils.assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), "Transfer-encoding", new PredicateEquals<>("chunked"));
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN_STRING));

        LogDataBuilder networkValue = TestUtils.getLogData(networkLogManager);
        TestUtils.assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(networkValue.getResponseHeaders(), "Transfer-encoding", new PredicateEquals<>("chunked"));
        assertHeaderData(networkValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN_STRING));
    }

    @Test public void bodyResponseNotIdentityEncoded() throws IOException {
        MockResponse mockResponse = createMockResponse();
        mockResponse = setMockResponseHeader(mockResponse, "Content-Encoding", "gzip");
        mockResponse = setMockResponsePlainContentTypeHeader(mockResponse);
        mockResponse = setMockResponseBody(mockResponse, new Buffer().write(ByteString.decodeBase64(
                "H4sIAAAAAAAAAPNIzcnJ11HwQKIAdyO+9hMAAAA=")));

        newCall(mockResponse);

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(19)
                .responseContentLength(-1)
                .responseBody("Hello, Hello, Hello");

        LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);
        TestUtils.assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN_STRING));

        expectedValue
                .responseBodySize(0)
                .responseContentLength(29)
                .responseBodyState(LogDataBuilder.ENCODED_BODY)
                .responseBody(null);

        LogDataBuilder networkValue = TestUtils.getLogData(networkLogManager);
        TestUtils.assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(networkValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(PLAIN_STRING));
        assertHeaderData(networkValue.getResponseHeaders(), "Content-Encoding", new PredicateEquals<>("gzip"));
    }

    @Test public void bodyGetMalformedCharset() throws IOException {
        String contentType = "text/html; charset=0";

        MockResponse mockResponse = createMockResponse();
        mockResponse = setMockResponseHeader(mockResponse, CONTENT_TYPE, contentType);
        mockResponse = setMockResponseBody(mockResponse, "Ignore This");

        newCall(mockResponse);

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(0)
                .responseContentLength(11)
                .responseBodyState(LogDataBuilder.CHARSET_MALFORMED);

        LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);
        TestUtils.assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(contentType));

        LogDataBuilder networkValue = TestUtils.getLogData(networkLogManager);
        TestUtils.assertData(expectedValue, networkValue);
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

        MockResponse mockResponse = createMockResponse();
        mockResponse = setMockResponseBody(mockResponse, buffer);
        mockResponse = setMockResponseHeader(mockResponse, CONTENT_TYPE, contentType);

        newCall(mockResponse);

        LogDataBuilder expectedValue = expectedLogData()
                .requestMethod(GET)
                .requestBodyState(LogDataBuilder.NO_BODY)
                .responseCode(RESPONSE_CODE_OK)
                .responseMessage(MESSAGE_OK)
                .responseBodySize(9)
                .responseContentLength(9)
                .responseBodyState(LogDataBuilder.BINARY_BODY);

        LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);
        TestUtils.assertData(expectedValue, appValue);
        assertNull(appValue.getRequestHeaders());
        assertHeaderData(appValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(contentType));

        LogDataBuilder networkValue = TestUtils.getLogData(networkLogManager);
        TestUtils.assertData(expectedValue, networkValue);
        assertRequestHeaders(networkValue.getRequestHeaders());
        assertHeaderData(networkValue.getResponseHeaders(), CONTENT_TYPE, new PredicateEquals<>(contentType));
    }

    @Test public void isPlaintext() throws IOException {
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer()));
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer().writeUtf8("abc")));
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer().writeUtf8("new\r\nlines")));
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer().writeUtf8("white\t space")));
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer().writeByte(0x80)));
        assertFalse(BaseLogDataInterceptor.isPlaintext(new Buffer().writeByte(0x00)));
        assertFalse(BaseLogDataInterceptor.isPlaintext(new Buffer().writeByte(0xc0)));
    }

    private LogDataBuilder expectedLogData() {
        return new LogDataBuilder()
                .requestUrl(getUrlString())
                .responseUrl(getUrlString());
    }

    private void assertRequestHeaders(List<LogDataBuilder.HeaderDataBuilder> requestHeaders) {
        assertHeaderData(requestHeaders, "Host", new PredicateEquals<>(host));
        assertHeaderData(requestHeaders, "User-Agent", new PredicateStartsWith("okhttp"));
    }

    private void assertHeaderData(List<LogDataBuilder.HeaderDataBuilder> headers, String name,
            Predicate<String> assertPredicate) {
        if (headers == null || headers.size() == 0) {
            fail("Headers are empty");
        }

        if (StringUtils.isEmpty(name)) {
            fail("Header name is empty");
        }

        for (LogDataBuilder.HeaderDataBuilder header : headers) {
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
