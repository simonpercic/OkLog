package com.github.simonpercic.oklog.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * LogManager unit test.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CompressionUtils.class, TimberUtils.class})
public class LogManagerUnitTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(CompressionUtils.class);
    }

    @Test
    public void testGetLogUrlIOException() throws Exception {
        when(CompressionUtils.gzipBase64(anyString())).thenThrow(new IOException());

        LogManager logManager = new LogManager(null, null, false);

        String logUrl = logManager.getLogUrl("", null);

        assertNull(logUrl);
    }

    @Test
    public void testGetLogUrlEmpty() throws Exception {
        when(CompressionUtils.gzipBase64(anyString())).thenReturn("");

        LogManager logManager = new LogManager(null, null, false);

        String logUrl = logManager.getLogUrl("", null);

        assertNull(logUrl);
    }

    @Test
    public void testGetLogUrl() throws Exception {
        String gzipped = "H4sIAAAAAAAAALXVzY7TMBAH8Ps-RZXzbmKPPZ5Jr7wBV4TQ-ItWtNvQpBfQvjuGVkJa7aoGxYfk\n"
                + "ENvzT_yLxp8eNpuf5dpsumc5pm7bTXJOz8sT9Nir7vE69GM_eTkcvlzOhzJjtyzTvB0Gmfb91_2y\n"
                + "u_g-nI7DOU2neZi_X0qB4fTt96zhtnB4q-gi5_8relv4ZtGy5rhfuu31m8qDeSelejbOp2i1MMHo\n"
                + "IoforPYYlFOQg2enciJlza1MWffPb3VNnofqqD9JL-X-8vg-gu3V08cPen2IV4XXw3hV-B0QDIGi\n"
                + "FyNR40gZg5SbYSWWLUZlGLPYMrACSHVULUgTjCYQdxDKD8o-JUATM8no2WcTsrWGRRSRAKOCoPQK\n"
                + "CNVRVQimBYJpgWDuIgQ7phRT-UHJJuV8YMgoKVJwhMplhzGCZVgBoTqqCgFaIEALBLiL4CSVnfAm\n"
                + "lB0YS7_G0sM5GQFUOmeyKkbRqN0KCNVRVQi61fmgW50Puup8IO-ItPcwmjEKJIoWghgchUIOSlxi\n"
                + "BwR-jdZUG1UL0gSjCcQdBHbRknYeWaJxCAkTOfYBIbMi1qWRl9ahwwoI1VF_ER4-_wL2RqAwvQoA\n"
                + "AA==\n";

        when(CompressionUtils.gzipBase64UrlSafe(eq("response_body"))).thenReturn(gzipped.replaceAll("\n", ""));

        String baseUrl = "http://example.com";
        LogManager logManager = new LogManager(baseUrl, null, false);

        String logUrl = logManager.getLogUrl("response_body", null);

        String gzippedNoNewLine = gzipped.replaceAll("\n", "");
        String expected = String.format("%s%s%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, gzippedNoNewLine);
        assertEquals(expected, logUrl);
    }

    @Test
    public void testLogDebugCalled() throws Exception {
        String compressedString = "compressedString";
        when(CompressionUtils.gzipBase64UrlSafe(anyString())).thenReturn(compressedString);

        String baseUrl = "http://example.com";
        LogManager logManager = spy(new LogManager(baseUrl, null, false));

        logManager.log(new LogDataBuilder().responseBody("response_body"));

        String expected = String.format("%s%s%s?d=%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressedString,
                compressedString);

        verify(logManager).logDebug(eq(expected));
    }

    @Test
    public void testLogInterceptorCalled() throws Exception {
        String compressedString = "compressedString";
        when(CompressionUtils.gzipBase64UrlSafe(anyString())).thenReturn(compressedString);

        LogInterceptor logInterceptor = mock(LogInterceptor.class);

        String baseUrl = "http://example.com";
        LogManager logManager = new LogManager(baseUrl, logInterceptor, false);

        logManager.log(new LogDataBuilder().responseBody("response_body"));

        String expected = String.format("%s%s%s?d=%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressedString,
                compressedString);

        verify(logInterceptor).onLog(eq(expected));
    }

    @Test
    public void testLogInterceptorHandled() throws Exception {
        String compressedString = "compressedString";
        when(CompressionUtils.gzipBase64UrlSafe(anyString())).thenReturn(compressedString);

        LogInterceptor logInterceptor = mock(LogInterceptor.class);
        when(logInterceptor.onLog(anyString())).thenReturn(true);

        LogManager logManager = spy(new LogManager(null, logInterceptor, false));

        logManager.log(new LogDataBuilder().responseBody("response_body"));

        verify(logManager, never()).logDebug(anyString());
    }

    @Test
    public void testLogInterceptorNotHandled() throws Exception {
        String compressedString = "compressedString";
        when(CompressionUtils.gzipBase64UrlSafe(anyString())).thenReturn(compressedString);

        LogInterceptor logInterceptor = mock(LogInterceptor.class);
        when(logInterceptor.onLog(anyString())).thenReturn(false);

        String baseUrl = "http://example.com";
        LogManager logManager = spy(new LogManager(baseUrl, logInterceptor, false));

        logManager.log(new LogDataBuilder().responseBody("response_body"));

        String expected = String.format("%s%s%s?d=%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressedString,
                compressedString);

        verify(logManager).logDebug(eq(expected));
    }

    @Test
    public void testUseAndroidLog() throws Exception {
        LogManager logManager = new LogManager(null, null, true);
        assertEquals(true, logManager.useAndroidLog);
    }

    @Test
    public void testUseAndroidLogNoTimber() throws Exception {
        mockStatic(TimberUtils.class);

        when(TimberUtils.hasTimber()).thenReturn(false);

        LogManager logManager = new LogManager(null, null, false);
        assertEquals(true, logManager.useAndroidLog);
    }

    @Test
    public void testUseAndroidLogTimber() throws Exception {
        mockStatic(TimberUtils.class);

        when(TimberUtils.hasTimber()).thenReturn(true);

        LogManager logManager = new LogManager(null, null, false);
        assertEquals(false, logManager.useAndroidLog);
    }
}
