package com.github.simonpercic.oklog3

import com.github.simonpercic.oklog.core.LogDataBuilder
import com.github.simonpercic.oklog.core.LogManager
import com.github.simonpercic.oklog.core.TestUtils
import okhttp3.HttpUrl
import okhttp3.OkHttpClient.Builder
import okhttp3.Request
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.io.IOException
import java.net.UnknownHostException

/**
 * OkLogInterceptor unit test. Inspired by: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor.
 *
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@RunWith(MockitoJUnitRunner::class)
class OkLogInterceptorUnitTest {

    @Mock internal lateinit var applicationLogManager: LogManager

    @Rule
    @JvmField
    val server = MockWebServer()

    private lateinit var url: HttpUrl

    @Before
    @Throws(Exception::class)
    fun setUp() {
        url = server.url("/")
    }

    @Test
    @Throws(IOException::class)
    fun connectFail() {
        val applicationInterceptor = OkLogInterceptor(applicationLogManager)

        val client = Builder()
            .dns { throw UnknownHostException("reason") }
            .addInterceptor(applicationInterceptor)
            .build()

        try {
            client.newCall(request().build()).execute()
            fail()
        } catch (expected: UnknownHostException) {
            val appValue = TestUtils.getLogData(applicationLogManager)

            TestUtils.assertData(GET, url.toString(), url.encodedPath(), LogDataBuilder.BodyState.NO_BODY, true,
                appValue)

            TestUtils.assertNoRequestHeaders(appValue)
        }
    }

    private fun request(): Request.Builder = Request.Builder().url(url)

    companion object {

        private const val GET = "GET"
    }
}
