package com.github.simonpercic.oklog.core;

import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class TestUtils {

    private TestUtils() {
        // no instance
    }

    public static LogDataBuilder getLogData(LogManager logManager) {
        ArgumentCaptor<LogDataBuilder> dataCaptor = ArgumentCaptor.forClass(LogDataBuilder.class);
        verify(logManager).log(dataCaptor.capture());
        return dataCaptor.getValue();
    }

    public static void assertData(LogDataBuilder expected, LogDataBuilder value) {
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
}
