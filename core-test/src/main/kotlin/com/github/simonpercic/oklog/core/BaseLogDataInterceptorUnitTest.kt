package com.github.simonpercic.oklog.core

import com.github.simonpercic.collectionhelper.CollectionHelper
import okio.Buffer
import okio.ByteString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.IOException
import java.util.Locale

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@RunWith(MockitoJUnitRunner::class)
abstract class BaseLogDataInterceptorUnitTest<MockResponse, Request> {

    private val applicationLogManager = Mockito.mock(LogManager::class.java)
    private val networkLogManager = Mockito.mock(LogManager::class.java)

    private lateinit var host: String

    // region Abstract methods

    protected abstract fun createClient(applicationLogManager: LogManager, networkLogManager: LogManager): String

    @Throws(IOException::class)
    protected abstract fun newCall(mockResponse: MockResponse)

    @Throws(IOException::class)
    protected abstract fun newCall(request: Request, mockResponse: MockResponse)

    protected abstract val urlString: String

    protected abstract val urlPath: String

    protected abstract fun createMockResponse(): MockResponse

    protected abstract fun setMockResponseStatus(mockResponse: MockResponse, status: String): MockResponse

    protected abstract fun setMockResponseBody(mockResponse: MockResponse, body: String): MockResponse

    protected abstract fun setMockResponseBody(mockResponse: MockResponse, body: Buffer): MockResponse

    protected abstract fun setMockResponsePlainContentTypeHeader(mockResponse: MockResponse): MockResponse

    protected abstract fun setMockResponseHeader(mockResponse: MockResponse, name: String, value: Any): MockResponse

    protected abstract fun setMockResponseChunked(mockResponse: MockResponse, body: String, maxChunkSize: Int): MockResponse

    protected abstract fun createPlainPostRequest(body: String): Request

    // endregion Abstract methods

    @Before fun setUp() {
        host = createClient(applicationLogManager, networkLogManager)
    }

    @Test @Throws(IOException::class)
    fun bodyGet() {
        newCall(createMockResponse())

        val expectedValue = expectedLogData()
            .requestMethod(GET)
            .requestBodyState(LogDataBuilder.BodyState.NO_BODY)
            .responseCode(RESPONSE_CODE_OK)
            .responseMessage(MESSAGE_OK)

        val appValue = TestUtils.getLogData(applicationLogManager!!)

        TestUtils.assertData(expectedValue, appValue)
        assertNull(appValue.requestHeaders)

        val networkValue = TestUtils.getLogData(networkLogManager!!)
        TestUtils.assertData(expectedValue, networkValue)
        assertRequestHeaders(networkValue.requestHeaders)
    }

    @Test @Throws(IOException::class)
    fun bodyGet204() {
        bodyGetNoBody(204)
    }

    @Test @Throws(IOException::class)
    fun bodyGet205() {
        bodyGetNoBody(205)
    }

    @Throws(IOException::class)
    private fun bodyGetNoBody(code: Int) {
        var mockResponse = createMockResponse()
        mockResponse = setMockResponseStatus(mockResponse, "HTTP/1.1 $code No Content")

        newCall(mockResponse)

        val expectedValue = expectedLogData()
            .requestMethod(GET)
            .requestBodyState(LogDataBuilder.BodyState.NO_BODY)
            .responseCode(code)
            .responseMessage("No Content")

        val appValue = TestUtils.getLogData(applicationLogManager!!)
        TestUtils.assertData(expectedValue, appValue)
        assertNull(appValue.requestHeaders)

        val networkValue = TestUtils.getLogData(networkLogManager!!)
        TestUtils.assertData(expectedValue, networkValue)
        assertRequestHeaders(networkValue.requestHeaders)
    }

    @Test @Throws(IOException::class)
    fun bodyPost() {
        val request = createPlainPostRequest("Hi?")
        newCall(request, createMockResponse())

        val expectedValue = expectedLogData()
            .requestMethod("POST")
            .requestContentType(PLAIN_STRING)
            .requestContentLength(3)
            .requestBody("Hi?")
            .responseCode(RESPONSE_CODE_OK)
            .responseMessage(MESSAGE_OK)

        val appValue = TestUtils.getLogData(applicationLogManager!!)
        TestUtils.assertData(expectedValue, appValue)
        assertNull(appValue.requestHeaders)

        val networkValue = TestUtils.getLogData(networkLogManager!!)
        TestUtils.assertData(expectedValue, networkValue)
        assertRequestHeaders(networkValue.requestHeaders)
    }

    @Test @Throws(IOException::class)
    fun bodyResponseBody() {
        var mockResponse = createMockResponse()
        mockResponse = setMockResponseBody(mockResponse, "Hello!")
        mockResponse = setMockResponsePlainContentTypeHeader(mockResponse)

        newCall(mockResponse)

        val expectedValue = expectedLogData()
            .requestMethod(GET)
            .requestBodyState(LogDataBuilder.BodyState.NO_BODY)
            .responseCode(RESPONSE_CODE_OK)
            .responseMessage(MESSAGE_OK)
            .responseBodySize(6)
            .responseContentLength(6)
            .responseBody("Hello!")

        val appValue = TestUtils.getLogData(applicationLogManager!!)
        TestUtils.assertData(expectedValue, appValue)
        assertNull(appValue.requestHeaders)
        assertHeaderData(appValue.responseHeaders, CONTENT_TYPE, PredicateEquals(PLAIN_STRING))

        val networkValue = TestUtils.getLogData(networkLogManager!!)
        TestUtils.assertData(expectedValue, networkValue)
        assertRequestHeaders(networkValue.requestHeaders)
        assertHeaderData(appValue.responseHeaders, CONTENT_TYPE, PredicateEquals(PLAIN_STRING))
    }

    @Test @Throws(IOException::class)
    fun bodyResponseBodyChunked() {
        var mockResponse = createMockResponse()
        mockResponse = setMockResponseChunked(mockResponse, "Hello!", 2)
        mockResponse = setMockResponsePlainContentTypeHeader(mockResponse)

        newCall(mockResponse)

        val expectedValue = expectedLogData()
            .requestMethod(GET)
            .requestBodyState(LogDataBuilder.BodyState.NO_BODY)
            .responseCode(RESPONSE_CODE_OK)
            .responseMessage(MESSAGE_OK)
            .responseBodySize(6)
            .responseContentLength(-1)
            .responseBody("Hello!")

        val appValue = TestUtils.getLogData(applicationLogManager!!)
        TestUtils.assertData(expectedValue, appValue)
        assertNull(appValue.requestHeaders)
        assertHeaderData(appValue.responseHeaders, "Transfer-encoding", PredicateEquals("chunked"))
        assertHeaderData(appValue.responseHeaders, CONTENT_TYPE, PredicateEquals(PLAIN_STRING))

        val networkValue = TestUtils.getLogData(networkLogManager!!)
        TestUtils.assertData(expectedValue, networkValue)
        assertRequestHeaders(networkValue.requestHeaders)
        assertHeaderData(networkValue.responseHeaders, "Transfer-encoding", PredicateEquals("chunked"))
        assertHeaderData(networkValue.responseHeaders, CONTENT_TYPE, PredicateEquals(PLAIN_STRING))
    }

    @Test @Throws(IOException::class)
    fun bodyResponseNotIdentityEncoded() {
        var mockResponse = createMockResponse()
        mockResponse = setMockResponseHeader(mockResponse, "Content-Encoding", "gzip")
        mockResponse = setMockResponsePlainContentTypeHeader(mockResponse)
        mockResponse = setMockResponseBody(mockResponse, Buffer().write(ByteString.decodeBase64(
            "H4sIAAAAAAAAAPNIzcnJ11HwQKIAdyO+9hMAAAA=")!!))

        newCall(mockResponse)

        val expectedValue = expectedLogData()
            .requestMethod(GET)
            .requestBodyState(LogDataBuilder.BodyState.NO_BODY)
            .responseCode(RESPONSE_CODE_OK)
            .responseMessage(MESSAGE_OK)
            .responseBodySize(19)
            .responseContentLength(-1)
            .responseBody("Hello, Hello, Hello")

        val appValue = TestUtils.getLogData(applicationLogManager!!)
        TestUtils.assertData(expectedValue, appValue)
        assertNull(appValue.requestHeaders)
        assertHeaderData(appValue.responseHeaders, CONTENT_TYPE, PredicateEquals(PLAIN_STRING))

        expectedValue
            .responseBodySize(0)
            .responseContentLength(29)
            .responseBodyState(LogDataBuilder.BodyState.ENCODED_BODY)
            .responseBody(null)

        val networkValue = TestUtils.getLogData(networkLogManager!!)
        TestUtils.assertData(expectedValue, networkValue)
        assertRequestHeaders(networkValue.requestHeaders)
        assertHeaderData(networkValue.responseHeaders, CONTENT_TYPE, PredicateEquals(PLAIN_STRING))
        assertHeaderData(networkValue.responseHeaders, "Content-Encoding", PredicateEquals("gzip"))
    }

    @Test @Throws(IOException::class)
    fun responseBodyIsBinary() {
        val buffer = Buffer()
        buffer.writeUtf8CodePoint(0x89)
        buffer.writeUtf8CodePoint(0x50)
        buffer.writeUtf8CodePoint(0x4e)
        buffer.writeUtf8CodePoint(0x47)
        buffer.writeUtf8CodePoint(0x0d)
        buffer.writeUtf8CodePoint(0x0a)
        buffer.writeUtf8CodePoint(0x1a)
        buffer.writeUtf8CodePoint(0x0a)

        val contentType = "image/png; charset=utf-8"

        var mockResponse = createMockResponse()
        mockResponse = setMockResponseBody(mockResponse, buffer)
        mockResponse = setMockResponseHeader(mockResponse, CONTENT_TYPE, contentType)

        newCall(mockResponse)

        val expectedValue = expectedLogData()
            .requestMethod(GET)
            .requestBodyState(LogDataBuilder.BodyState.NO_BODY)
            .responseCode(RESPONSE_CODE_OK)
            .responseMessage(MESSAGE_OK)
            .responseBodySize(9)
            .responseContentLength(9)
            .responseBodyState(LogDataBuilder.BodyState.BINARY_BODY)

        val appValue = TestUtils.getLogData(applicationLogManager!!)
        TestUtils.assertData(expectedValue, appValue)
        assertNull(appValue.requestHeaders)
        assertHeaderData(appValue.responseHeaders, CONTENT_TYPE, PredicateEquals(contentType))

        val networkValue = TestUtils.getLogData(networkLogManager!!)
        TestUtils.assertData(expectedValue, networkValue)
        assertRequestHeaders(networkValue.requestHeaders)
        assertHeaderData(networkValue.responseHeaders, CONTENT_TYPE, PredicateEquals(contentType))
    }

    @Test @Throws(IOException::class)
    fun isPlaintext() {
        assertTrue(BaseLogDataInterceptor.isPlaintext(Buffer()))
        assertTrue(BaseLogDataInterceptor.isPlaintext(Buffer().writeUtf8("abc")))
        assertTrue(BaseLogDataInterceptor.isPlaintext(Buffer().writeUtf8("new\r\nlines")))
        assertTrue(BaseLogDataInterceptor.isPlaintext(Buffer().writeUtf8("white\t space")))
        assertTrue(BaseLogDataInterceptor.isPlaintext(Buffer().writeByte(0x80)))
        assertFalse(BaseLogDataInterceptor.isPlaintext(Buffer().writeByte(0x00)))
        assertFalse(BaseLogDataInterceptor.isPlaintext(Buffer().writeByte(0xc0)))
    }

    @Throws(IOException::class)
    protected fun bodyGetMalformedCharset(isResponsePlainBody: Boolean, isResponseBodySizeZero: Boolean,
        isResponseBodyNull: Boolean) {

        val contentType = "text/html; charset=0"

        var mockResponse = createMockResponse()
        mockResponse = setMockResponseHeader(mockResponse, CONTENT_TYPE, contentType)

        val body = "Body with unknown charset"
        mockResponse = setMockResponseBody(mockResponse, body)

        newCall(mockResponse)

        val expectedValue = expectedLogData()
            .requestMethod(GET)
            .requestBodyState(LogDataBuilder.BodyState.NO_BODY)
            .responseCode(RESPONSE_CODE_OK)
            .responseMessage(MESSAGE_OK)
            .responseBody(if (isResponseBodyNull) null else body)
            .responseBodySize((if (isResponseBodySizeZero) 0 else body.length).toLong())
            .responseContentLength(body.length.toLong())
            .responseBodyState(if (isResponsePlainBody)
                LogDataBuilder.BodyState.PLAIN_BODY
            else
                LogDataBuilder.BodyState.CHARSET_MALFORMED)

        val appValue = TestUtils.getLogData(applicationLogManager!!)
        TestUtils.assertData(expectedValue, appValue)
        assertNull(appValue.requestHeaders)
        assertHeaderData(appValue.responseHeaders, CONTENT_TYPE, PredicateEquals(contentType))

        val networkValue = TestUtils.getLogData(networkLogManager!!)
        TestUtils.assertData(expectedValue, networkValue)
        assertRequestHeaders(networkValue.requestHeaders)
        assertHeaderData(networkValue.responseHeaders, CONTENT_TYPE, PredicateEquals(contentType))
    }

    private fun expectedLogData(): LogDataBuilder {
        return LogDataBuilder()
            .requestUrl(urlString)
            .requestUrlPath(urlPath)
            .responseUrl(urlString)
    }

    private fun assertRequestHeaders(requestHeaders: List<LogDataBuilder.HeaderDataBuilder>) {
        assertHeaderData(requestHeaders, "Host", PredicateEquals(host))
        assertHeaderData(requestHeaders, "User-Agent", PredicateStartsWith("okhttp"))
    }

    private fun assertHeaderData(headers: List<LogDataBuilder.HeaderDataBuilder>, name: String,
        assertPredicate: Predicate<String>) {
        if (CollectionHelper.isEmpty(headers)) {
            fail("Headers are empty")
        }

        if (StringUtils.isEmpty(name)) {
            fail("Header name is empty")
        }

        for (header in headers) {
            if (name.equals(header.name, ignoreCase = true)) {
                assertPredicate.assertValue(header.value)
                return
            }
        }

        fail(String.format("No header with name: %s", name))
    }

    private inner class PredicateStartsWith internal constructor(expected: String) : Predicate<String> {

        private val expected = expected.toLowerCase(Locale.US)

        override fun assertValue(value: String) {
            assertTrue(value.toLowerCase(Locale.US).startsWith(expected))
        }
    }

    private inner class PredicateEquals<T> internal constructor(private val expected: T) : Predicate<T> {

        override fun assertValue(value: T) {
            assertEquals(expected, value)
        }
    }

    private interface Predicate<in T> {
        fun assertValue(value: T)
    }

    companion object {

        const val PLAIN_STRING = "text/plain; charset=utf-8"
        const val CONTENT_TYPE = "Content-Type"
        private const val GET = "GET"
        private const val MESSAGE_OK = "OK"
        private const val RESPONSE_CODE_OK = 200
    }
}
