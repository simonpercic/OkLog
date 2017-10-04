package com.github.simonpercic.oklog.core

import com.github.simonpercic.oklog.shared.data.BodyState
import com.github.simonpercic.oklog.shared.data.HeaderData
import com.github.simonpercic.oklog.shared.data.LogData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.anyObject
import org.mockito.Matchers.anyString
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner
import java.io.IOException
import java.util.Arrays

/**
 * LogManager unit test.
 *
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@RunWith(MockitoJUnitRunner::class)
class LogManagerUnitTest {

    @Mock internal lateinit var compressionUtil: CompressionUtil
    @Mock internal lateinit var logger: Logger

    @Test
    @Throws(Exception::class)
    fun testGetLogUrlIOException() {
        `when`(compressionUtil.gzipBase64UrlSafe(anyString())).thenThrow(IOException())

        val baseUrl = "http://example.com"
        val logManager = LogManager(baseUrl, null, logger, false, false, false, LOG_DATA_CONFIG_NONE, compressionUtil)

        val logUrl = logManager.getLogUrl("", "", null)
        val expected = String.format("%s%s0", baseUrl, "/v1/re/")

        assertEquals(expected, logUrl)
    }

    @Test
    @Throws(Exception::class)
    fun testGetLogUrlEmpty() {
        `when`(compressionUtil.gzipBase64UrlSafe(anyString())).thenReturn("")

        val baseUrl = "http://example.com"
        val logManager = LogManager(baseUrl, null, logger, false, false, false, LOG_DATA_CONFIG_ALL, compressionUtil)

        val logUrl = logManager.getLogUrl("", "", null)
        val expected = String.format("%s%s0", baseUrl, "/v1/r/")

        assertEquals(expected, logUrl)
    }

    @Test
    @Throws(Exception::class)
    fun testGetLogUrl() {
        val gzipped = "H4sIAAAAAAAAALXVzY7TMBAH8Ps-RZXzbmKPPZ5Jr7wBV4TQ-ItWtNvQpBfQvjuGVkJa7aoGxYfk\n" +
            "ENvzT_yLxp8eNpuf5dpsumc5pm7bTXJOz8sT9Nir7vE69GM_eTkcvlzOhzJjtyzTvB0Gmfb91_2y\n" +
            "u_g-nI7DOU2neZi_X0qB4fTt96zhtnB4q-gi5_8relv4ZtGy5rhfuu31m8qDeSelejbOp2i1MMHo\n" +
            "IoforPYYlFOQg2enciJlza1MWffPb3VNnofqqD9JL-X-8vg-gu3V08cPen2IV4XXw3hV-B0QDIGi\n" +
            "FyNR40gZg5SbYSWWLUZlGLPYMrACSHVULUgTjCYQdxDKD8o-JUATM8no2WcTsrWGRRSRAKOCoPQK\n" +
            "CNVRVQimBYJpgWDuIgQ7phRT-UHJJuV8YMgoKVJwhMplhzGCZVgBoTqqCgFaIEALBLiL4CSVnfAm\n" +
            "lB0YS7_G0sM5GQFUOmeyKkbRqN0KCNVRVQi61fmgW50Puup8IO-ItPcwmjEKJIoWghgchUIOSlxi\n" +
            "BwR-jdZUG1UL0gSjCcQdBHbRknYeWaJxCAkTOfYBIbMi1qWRl9ahwwoI1VF_ER4-_wL2RqAwvQoA\n" +
            "AA==\n"

        `when`(compressionUtil.gzipBase64UrlSafe(eq("response_body"))).thenReturn(gzipped.replace("\n".toRegex(), ""))
        `when`(compressionUtil.gzipBase64UrlSafe(eq("request_body"))).thenReturn("compressed_request_body")

        val baseUrl = "http://example.com"
        val logManager = LogManager(baseUrl, null, logger, false, true, false, LOG_DATA_CONFIG_ALL, compressionUtil)

        val logUrl = logManager.getLogUrl("response_body", "request_body", null)

        val gzippedNoNewLine = gzipped.replace("\n".toRegex(), "")
        val expected = String.format("%s%s%s?qb=%s", baseUrl, "/v1/r/", gzippedNoNewLine,
            "compressed_request_body")
        assertEquals(expected, logUrl)
    }

    @Test
    @Throws(Exception::class)
    fun testGetLogUrlLogData() {
        val gzipped = "H4sIAAAAAAAAALXVzY7TMBAH8Ps-RZXzbmKPPZ5Jr7wBV4TQ-ItWtNvQpBfQvjuGVkJa7aoGxYfk\n" +
            "ENvzT_yLxp8eNpuf5dpsumc5pm7bTXJOz8sT9Nir7vE69GM_eTkcvlzOhzJjtyzTvB0Gmfb91_2y\n" +
            "u_g-nI7DOU2neZi_X0qB4fTt96zhtnB4q-gi5_8relv4ZtGy5rhfuu31m8qDeSelejbOp2i1MMHo\n" +
            "IoforPYYlFOQg2enciJlza1MWffPb3VNnofqqD9JL-X-8vg-gu3V08cPen2IV4XXw3hV-B0QDIGi\n" +
            "FyNR40gZg5SbYSWWLUZlGLPYMrACSHVULUgTjCYQdxDKD8o-JUATM8no2WcTsrWGRRSRAKOCoPQK\n" +
            "CNVRVQimBYJpgWDuIgQ7phRT-UHJJuV8YMgoKVJwhMplhzGCZVgBoTqqCgFaIEALBLiL4CSVnfAm\n" +
            "lB0YS7_G0sM5GQFUOmeyKkbRqN0KCNVRVQi61fmgW50Puup8IO-ItPcwmjEKJIoWghgchUIOSlxi\n" +
            "BwR-jdZUG1UL0gSjCcQdBHbRknYeWaJxCAkTOfYBIbMi1qWRl9ahwwoI1VF_ER4-_wL2RqAwvQoA\n" +
            "AA==\n"

        `when`(compressionUtil.gzipBase64UrlSafe(eq("response_body"))).thenReturn(gzipped.replace("\n".toRegex(), ""))
        `when`(compressionUtil.gzipBase64UrlSafe(eq("request_body"))).thenReturn("compressed_request_body")

        val baseUrl = "http://example.com"
        val logManager = LogManager(baseUrl, null, logger, false, true, false, LOG_DATA_CONFIG_ALL, compressionUtil)

        val requestHeaders = Arrays.asList(HeaderData("q_n_1", "q_v_1"),
            HeaderData("q_n_2", "q_v_2"))

        val responseHeaders = Arrays.asList(HeaderData("s_n_1", "s_v_1"),
            HeaderData("s_n_2", "s_v_2"))

        val logData = LogData("request_method", "request_url", "protocol", "request_content_type", 123L,
            requestHeaders, BodyState.PLAIN_BODY, false, 200, "response_message", 456L, 789L, responseHeaders,
            BodyState.ENCODED_BODY, 777L, "response_url")

        val bytes = byteArrayOf(10, 14, 114, 101, 113, 117, 101, 115, 116, 95, 109, 101, 116, 104, 111, 100, 18, 11, 114, 101, 113, 117, 101, 115, 116, 95, 117, 114, 108, 26, 8, 112, 114, 111, 116, 111, 99, 111, 108, 34, 20, 114, 101, 113, 117, 101, 115, 116, 95, 99, 111, 110, 116, 101, 110, 116, 95, 116, 121, 112, 101, 40, 123, 50, 14, 10, 5, 113, 95, 110, 95, 49, 18, 5, 113, 95, 118, 95, 49, 50, 14, 10, 5, 113, 95, 110, 95, 50, 18, 5, 113, 95, 118, 95, 50, 56, 1, 64, 0, 72, -56, 1, 82, 16, 114, 101, 115, 112, 111, 110, 115, 101, 95, 109, 101, 115, 115, 97, 103, 101, 88, -56, 3, 96, -107, 6, 106, 14, 10, 5, 115, 95, 110, 95, 49, 18, 5, 115, 95, 118, 95, 49, 106, 14, 10, 5, 115, 95, 110, 95, 50, 18, 5, 115, 95, 118, 95, 50, 112, 3, 120, -119, 6, -126, 1, 12, 114, 101, 115, 112, 111, 110, 115, 101, 95, 117, 114, 108)

        val compressedLogData = "H4sIAAAAAAAAAJ2Rz27TQBDGSYwKXXGo9gARpMISQkLIG69T8qc5YTlWW9HEUbKgHlmvN8lunV3L3" +
            "jrNlbfiDTjlmdgUiQPqicPMZX7zzcw3wLmICeytjSlGvl-UoqaGo3SQsk_I1NVabytaiI4NWu7QRrPbDtMb_6Hw-vmhzQ8" +
            "6wYe9M2x-fnL5qzFvJl9ufj77vnfkCTha8LLmJTyK9DbVO9kCINJKcWaEVhDccl4gmouay7egdYPCP0PmdoNcbIRB14cMn" +
            "aCL5TvQfgSY8w0VSqiVhYJz6YIXVt9wZRDZFRye0KLIBaOHcb6stLLEm5AxXlXoAJY6R2Ge6y1KSrESCjY-yiloP0pMuFn" +
            "rrIKdZEaukunCs755l3E49mbJgnizr8Qbx9cxiT0yD6PYi5LpNI6IbIOX_-hN6D0KVxw2AyzPwKu_Z5GSqoo-eIOuMtjqD" +
            "Za9ZZYGwTDr0nOcYrzsZn2Wyffg6dhaAE_J-s5zcd9NmHG7OOi7eDA6w6Pe0L2YEOu_801QeGz_49Z8dVcWjfu986Pxf8_" +
            "-DXy6JbEpAgAA"

        `when`(compressionUtil.gzipBase64UrlSafe(eq(bytes))).thenReturn(compressedLogData)

        val logUrl = logManager.getLogUrl("response_body", "request_body", logData)

        val gzippedNoNewLine = gzipped.replace("\n".toRegex(), "")
        val expected = String.format("%s%s%s?qb=%s&d=%s", baseUrl, "/v1/r/",
            gzippedNoNewLine, "compressed_request_body", compressedLogData)
        assertEquals(expected, logUrl)
    }

    @Test
    @Throws(Exception::class)
    fun testGetLogUrlShorten() {
        `when`(compressionUtil.gzipBase64UrlSafe(eq("response_body"))).thenReturn("compressed_response_body")
        `when`(compressionUtil.gzipBase64UrlSafe(eq("request_body"))).thenReturn("compressed_request_body")

        val baseUrl = "http://example.com"
        val logManager = LogManager(baseUrl, null, logger, false, true, true, LOG_DATA_CONFIG_ALL, compressionUtil)

        val logUrl = logManager.getLogUrl("response_body", "request_body", null)

        val expected = String.format("%s%s%s?qb=%s&s=1", baseUrl, "/v1/r/", "compressed_response_body",
            "compressed_request_body")
        assertEquals(expected, logUrl)
    }

    @Test
    @Throws(Exception::class)
    fun testLogDebugCalled() {
        `when`(compressionUtil.gzipBase64UrlSafe(eq("response_body"))).thenReturn("compressed_string")
        `when`(compressionUtil.gzipBase64UrlSafe(anyObject<Any>() as ByteArray?)).thenReturn("compressed_data_string")

        val baseUrl = "http://example.com"
        val logManager = spy(
            LogManager(baseUrl, null, logger, false, true, false, LOG_DATA_CONFIG_ALL, compressionUtil))

        val requestMethod = "request_method"
        val requestUrlPath = "request_url_path"

        logManager.log(LogDataBuilder()
            .responseBody("response_body")
            .requestMethod(requestMethod)
            .requestUrlPath(requestUrlPath))

        val expected = String.format("%s%s%s?d=%s", baseUrl, "/v1/r/",
            "compressed_string", "compressed_data_string")

        verify(logManager).logDebug(eq(expected), eq(requestMethod), eq(requestUrlPath))
    }

    @Test
    @Throws(Exception::class)
    fun testLogInterceptorCalled() {
        `when`(compressionUtil.gzipBase64UrlSafe(eq("response_body"))).thenReturn("compressed_string")
        `when`(compressionUtil.gzipBase64UrlSafe(anyObject<Any>() as ByteArray?)).thenReturn("compressed_data_string")

        val logInterceptor = mock(LogInterceptor::class.java)

        val baseUrl = "http://example.com"
        val logManager = LogManager(baseUrl, logInterceptor, logger, false, true, false, LOG_DATA_CONFIG_ALL, compressionUtil)

        logManager.log(LogDataBuilder().responseBody("response_body"))

        val expected = String.format("%s%s%s?d=%s", baseUrl, "/v1/r/",
            "compressed_string", "compressed_data_string")

        verify(logInterceptor).onLog(eq(expected))
    }

    @Test
    @Throws(Exception::class)
    fun testLogInterceptorHandled() {
        val compressedString = "compressedString"
        `when`(compressionUtil.gzipBase64UrlSafe(anyString())).thenReturn(compressedString)

        val logInterceptor = mock(LogInterceptor::class.java)
        `when`(logInterceptor.onLog(anyString())).thenReturn(true)

        val logManager = spy(LogManager(null, logInterceptor, null, false, true, false,
            LOG_DATA_CONFIG_ALL, compressionUtil))

        logManager.log(LogDataBuilder()
            .responseBody("response_body")
            .requestMethod("request_method")
            .requestUrlPath("request_url_path"))

        verify(logManager, never()).logDebug(anyString(), anyString(), anyString())
    }

    @Test
    @Throws(Exception::class)
    fun testLogInterceptorNotHandled() {
        `when`(compressionUtil.gzipBase64UrlSafe(eq("response_body"))).thenReturn("compressed_string")
        `when`(compressionUtil.gzipBase64UrlSafe(anyObject<Any>() as ByteArray?)).thenReturn("compressed_data_string")

        val logInterceptor = mock(LogInterceptor::class.java)
        `when`(logInterceptor.onLog(anyString())).thenReturn(false)

        val baseUrl = "http://example.com"
        val logManager = spy(LogManager(baseUrl, logInterceptor, logger, false, true, false,
            LOG_DATA_CONFIG_ALL, compressionUtil))

        val requestMethod = "request_method"
        val requestUrlPath = "request_url_path"

        logManager.log(LogDataBuilder()
            .responseBody("response_body")
            .requestMethod(requestMethod)
            .requestUrlPath(requestUrlPath))

        val expected = String.format("%s%s%s?d=%s", baseUrl, "/v1/r/",
            "compressed_string", "compressed_data_string")

        verify(logManager).logDebug(eq(expected), eq(requestMethod), eq(requestUrlPath))
    }

    companion object {

        private val LOG_DATA_CONFIG_NONE = LogDataConfig(false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false)

        private val LOG_DATA_CONFIG_ALL = LogDataConfig(true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true)
    }
}
