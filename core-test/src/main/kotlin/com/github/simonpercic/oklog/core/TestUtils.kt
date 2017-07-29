package com.github.simonpercic.oklog.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
object TestUtils {

    fun getLogData(logManager: LogManager): LogDataBuilder {
        val dataCaptor = ArgumentCaptor.forClass(LogDataBuilder::class.java)
        verify(logManager).log(dataCaptor.capture())
        return dataCaptor.value
    }

    internal fun assertData(expected: LogDataBuilder, value: LogDataBuilder) {
        assertEquals(expected.requestMethod, value.requestMethod)
        assertEquals(expected.requestUrl, value.requestUrl)
        assertEquals(expected.requestUrlPath, value.requestUrlPath)
        assertEquals(expected.requestContentType, value.requestContentType)
        assertEquals(expected.requestContentLength, value.requestContentLength)
        assertEquals(expected.requestBody, value.requestBody)
        assertEquals(expected.requestBodyState, value.requestBodyState)
        assertEquals(expected.isRequestFailed, value.isRequestFailed)
        assertEquals(expected.responseCode.toLong(), value.responseCode.toLong())
        assertEquals(expected.responseMessage, value.responseMessage)
        assertEquals(expected.responseUrl, value.responseUrl)
        assertEquals(expected.responseContentLength, value.responseContentLength)
        assertEquals(expected.responseBodyState, value.responseBodyState)
        assertEquals(expected.responseBodySize, value.responseBodySize)
        assertEquals(expected.responseBody, value.responseBody)
    }

    fun assertData(requestMethod: String, requestUrl: String, requestUrlPath: String,
        requestBodyState: LogDataBuilder.BodyState, failed: Boolean, value: LogDataBuilder) {

        val expectedValue = LogDataBuilder()
            .requestMethod(requestMethod)
            .requestUrl(requestUrl)
            .requestUrlPath(requestUrlPath)
            .requestBodyState(requestBodyState)

        if (failed) {
            expectedValue.requestFailed()
        }

        assertData(expectedValue, value)
    }

    fun assertNoRequestHeaders(value: LogDataBuilder) {
        assertNull(value.requestHeaders)
    }
}
