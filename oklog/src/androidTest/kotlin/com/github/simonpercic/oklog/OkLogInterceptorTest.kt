package com.github.simonpercic.oklog

import android.support.test.runner.AndroidJUnit4
import com.github.simonpercic.oklog.core.LogInterceptor
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.IOException

/**
 * @author Simon Percic [https://github.com/simonpercic](https://github.com/simonpercic)
 */
@RunWith(AndroidJUnit4::class)
class OkLogInterceptorTest {

    companion object {
        private const val PLAIN_STRING = "text/plain; charset=utf-8"
        private const val REQUEST_HEADER_NAME = "ReqHeaderName"
        private const val REQUEST_HEADER_VALUE = "ReqExampleValue"
        private const val RESPONSE_HEADER_NAME = "ResHeaderName"
        private const val RESPONSE_HEADER_VALUE = "ResExampleValue"
        private const val CONTENT_TYPE = "Content-Type"
        private const val APPLICATION_JSON = "application/json"
    }

    private val server = MockWebServer()
    @Rule @JvmField val serverPort = MockWebServerPort(server)

    @Test
    fun testGetDefault() {
        val (client, urlCaptor) = createClientArgCaptor()

        val url = server.url("/shows")

        val mockResponse = MockResponse()
            .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}},{\"id\"" +
                ":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":\"Showtime\"}}]")

        newCall(client, request(url).build(), mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAIuuVspMUbIy1FHKS8xNVbJSCs1LSS1SKMlIVXDJBw" +
                "roKBWV5pVkgqTMDICKUkvK84uylawg2ozg2pydgpVqa3XQhQNSi4rz8xTy0xQ880pSi1KLS8gz0QQu7FhUlF9OwBBTuOoQoD-cw" +
                "xHmIMkUlQK9mFqSmlySWUbInxZwbR5O_gjTzFDsMTQwINtd5ggLgKGek5iXQsAoS7iG4Iz8crCy2tpYALkEI-7OAQAA?d=H4sIA" +
                "AAAAAAAAONidncNEZLOKCkpsNLXz8lPTszJyC8usTI1MDDQL87ILy_WYLBgcmDwOMEYxOTvnXCOuYCx4hwzAHrELHA3AAAA",
            urlCaptor.value)
    }

    @Test
    fun testGetAll() {
        val (client, urlCaptor) = createClientArgCaptor { it.withAllLogData() }

        val url = server.url("/shows")

        val request = request(url)
            .addHeader(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE)
            .build()

        val mockResponse = MockResponse()
            .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}},{\"id\"" +
                ":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":\"Showtime\"}}]")
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAIuuVspMUbIy1FHKS8xNVbJSCs1LSS1SKMlIVXDJBw" +
                "roKBWV5pVkgqTMDICKUkvK84uylawg2ozg2pydgpVqa3XQhQNSi4rz8xTy0xQ880pSi1KLS8gz0QQu7FhUlF9OwBBTuOoQoD-cw" +
                "xHmIMkUlQK9mFqSmlySWUbInxZwbR5O_gjTzFDsMTQwINtd5ggLgKGek5iXQsAoS7iG4Iz8crCy2tpYALkEI-7OAQAA?d=H4sIA" +
                "AAAAAAAAONidncNEZLOKCkpsNLXz8lPTszJyC8usTI1MDDQL87ILy-W4gBJ6hvqGWowGClw8QalFnqkJqakFvkl5qYK8QO5rhWJ" +
                "uQU5qWGJOaWpFkwODB4nGIOY_L0TzjFnKXDxOOfnlaTmleiGVBakCgkkFhTkZCYnlmTm5-lnFefnZYFNLEY1sRjZxALGinPMTYz" +
                "4nAgACeqmZsUAAAA=",
            urlCaptor.value)
    }

    @Test
    fun testPostDefault() {
        val (client, urlCaptor) = createClientArgCaptor()

        val url = server.url("/watched")

        val requestBody = createRequestBody("{\"show\":5}")

        val mockResponse = MockResponse()
            .setBody("{\"show\":{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\"" +
                ":\"HBO\"}},\"watched_count\":107}")

        newCall(client, request(url).post(requestBody).build(), mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAKtWKs7IL1eyqlbKTFGyMtVRykvMTVWyUgopKk1VcE" +
                "ktSU0uySxLVdJRKirNK8kESZkZABWllpTnF2XDtFnAtXk4-SvV1uoolSeWJGekpsQn5wO1KVkZGpjXAgANYYQRagAAAA==?qb=H" +
                "4sIAAAAAAAAAKtWKs7IL1eyMq0FAFlhsMYKAAAA&d=H4sIAAAAAAAAAONiCfAPDhGSzSgpKbDS18_JT07MycgvLrEyNTAw0C9PL" +
                "EnOSE3R4LJgdGDwOMEYxOTvnZBVwFiRBQDstqnDOAAAAA==",
            urlCaptor.value)
    }

    @Test
    fun testPostAll() {
        val (client, urlCaptor) = createClientArgCaptor { it.withAllLogData() }

        val url = server.url("/watched")

        val requestBody = createRequestBody("{\"show\":5}")

        val request = request(url)
            .post(requestBody)
            .addHeader(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE)
            .build()

        val mockResponse = MockResponse()
            .setBody("{\"show\":{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\"" +
                ":\"HBO\"}},\"watched_count\":107}")
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAKtWKs7IL1eyqlbKTFGyMtVRykvMTVWyUgopKk1VcE" +
                "ktSU0uySxLVdJRKirNK8kESZkZABWllpTnF2XDtFnAtXk4-SvV1uoolSeWJGekpsQn5wO1KVkZGpjXAgANYYQRagAAAA==?qb=H" +
                "4sIAAAAAAAAAKtWKs7IL1eyMq0FAFlhsMYKAAAA&d=H4sIAAAAAAAAAONiCfAPDhGSzSgpKbDS18_JT07MycgvLrEyNTAw0C9PL" +
                "EnOSE2R4gBJ6xvqGSpJlqRWlOgX5CRm5lkrJGckFhWnltiWlqTpWmhwGSlw8QalFnqkJqakFvkl5qYK8QO5rhWJuQU5qWGJOaWp" +
                "FowODB4nGIOY_L0TsrIUuHic8_NKUvNKdEMqC1KFBBILCnIykxNLMvPz9LOK8_OywAYWoxpYjGxgAWNFVhMjftcDAPvg-mjjAAAA",
            urlCaptor.value)
    }

    @Test
    fun testPutDefault() {
        val (client, urlCaptor) = createClientArgCaptor()

        val url = server.url("/show")

        val requestBody = createRequestBody("{\"name\":\"True Detective\",\"network\":{\"id\":8},\"runtime\":60}")

        val mockResponse = MockResponse()
            .setBody("{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}}")
            .setStatus("HTTP/1.1 %d %s".format(201, "Created"))

        newCall(client, request(url).put(requestBody).build(), mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1" +
                "XSUSoqzSvJBEmZGQAVpZaU5xdlK1lVg7VZwLV5OPkr1dYCAB5WJYFNAAAA?qb=H4sIAAAAAAAAAKtWykvMTVWyUgopKk1VcEktS" +
                "U0uySxLVdJRykstKc8vylayqlbKTFGysqjVUSoqzSvJBKk2M6gFAPS5LSY5AAAA&d=H4sIAAAAAAAAAONiDggNEZLKKCkpsNLXz" +
                "8lPTszJyC8usTI1MDDQL87IL9ewtGB0YPA4yRjE7lyUmliSmpLgW8BY4QsAVHkRTzkAAAA=",
            urlCaptor.value)
    }

    @Test
    fun testPutAll() {
        val (client, urlCaptor) = createClientArgCaptor { it.withAllLogData() }

        val url = server.url("/show")

        val requestBody = createRequestBody("{\"name\":\"True Detective\",\"network\":{\"id\":8},\"runtime\":60}")

        val request = request(url)
            .put(requestBody)
            .addHeader(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE)
            .build()

        val mockResponse = MockResponse()
            .setBody("{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}}")
            .setStatus("HTTP/1.1 %d %s".format(201, "Created"))
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1" +
                "XSUSoqzSvJBEmZGQAVpZaU5xdlK1lVg7VZwLV5OPkr1dYCAB5WJYFNAAAA?qb=H4sIAAAAAAAAAKtWykvMTVWyUgopKk1VcEktS" +
                "U0uySxLVdJRykstKc8vylayqlbKTFGysqjVUSoqzSvJBKk2M6gFAPS5LSY5AAAA&d=H4sIAAAAAAAAAONiDggNEZLKKCkpsNLXz" +
                "8lPTszJyC8usTI1MDDQL87IL5fiAMnpG-oZKkmWpFaU6BfkJGbmWSskZyQWFaeW2JaWpOlaaFgaKXDxBqUWeqQmpqQW-SXmpgrx" +
                "A7muFYm5BTmpYYk5pakWjA4MHicZg9idi1ITS1JTEnyzFLh4nPPzSlLzSnRDKgtShQQSCwpyMpMTSzLz8_SzivPzssCmFqOaWox" +
                "sagFjhW8TIx73AwC26Pni4QAAAA==",
            urlCaptor.value)
    }

    @Test
    fun testDeleteDefault() {
        val (client, urlCaptor) = createClientArgCaptor()

        val url = server.url("/show/5")

        val mockResponse = MockResponse()
            .setBody("{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}}")

        newCall(client, request(url).delete(null).build(), mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1" +
                "XSUSoqzSvJBEmZGQAVpZaU5xdlK1lVg7VZwLV5OPkr1dYCAB5WJYFNAAAA?d=H4sIAAAAAAAAAONic3H1cQ1xFZLJKCkpsNLXz8" +
                "lPTszJyC8usTI1MDDQL87IL9c31WCwYHJg8DjBGMTk753gW8BY4QsAuiJp6DkAAAA=",
            urlCaptor.value)
    }

    @Test
    fun testDeleteAll() {
        val (client, urlCaptor) = createClientArgCaptor { it.withAllLogData() }

        val url = server.url("/show/5")

        val request = request(url)
            .delete(null)
            .addHeader(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE)
            .build()

        val mockResponse = MockResponse()
            .setBody("{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}}")
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1" +
                "XSUSoqzSvJBEmZGQAVpZaU5xdlK1lVg7VZwLV5OPkr1dYCAB5WJYFNAAAA?d=H4sIAAAAAAAAAONic3H1cQ1xFZLJKCkpsNLXz8" +
                "lPTszJyC8usTI1MDDQL87IL9c3leIAyeob6hlqMBgpcPEGpRZ6pCampBb5JeamCvEDua4VibkFOalhiTmlqRZMDgweJxiDmPy9E" +
                "3yzFLh4nPPzSlLzSnRDKgtShQQSCwpyMpMTSzLz8_SzivPzssAGFqMaWIxsYAFjhW8TI14XAgBwEMrmyAAAAA==",
            urlCaptor.value)
    }

    @Test
    fun testHeaderDefault() {
        val (client, urlCaptor) = createClientArgCaptor()

        val url = server.url("/shows")

        val mockResponse = MockResponse()
            .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}},{\"id\"" +
                ":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":\"Showtime\"}}]")
            .setStatus("HTTP/1.1 %d %s".format(404, "Not Found"))

        newCall(client, request(url).head().build(), mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/0?d=H4sIAAAAAAAAAONi8XB1dBGSzigpKbDS18_JT07MycgvLrEyNT" +
                "Aw0C_OyC8v1mCwYHJg8JjCHMTpl1-i4JZfmpeScI65gKmCAQAvsqwcPgAAAA==",
            urlCaptor.value)
    }

    @Test
    fun testHeaderAll() {
        val (client, urlCaptor) = createClientArgCaptor { it.withAllLogData() }

        val url = server.url("/shows")

        val request = request(url)
            .head()
            .addHeader(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE)
            .build()

        val mockResponse = MockResponse()
            .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}},{\"id\"" +
                ":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":\"Showtime\"}}]")
            .setStatus("HTTP/1.1 %d %s".format(404, "Not Found"))
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        assertEquals(
            "http://responseecho-simonpercic.rhcloud.com/v1/r/0?d=H4sIAAAAAAAAAONi8XB1dBGSzigpKbDS18_JT07MycgvLrEyNT" +
                "Aw0C_OyC8vluIASeob6hlqMBgpcPEGpRZ6pCampBb5JeamCvEDua4VibkFOalhiTmlqRZMDgweU5iDOP3ySxTc8kvzUhLOMWcpc" +
                "PE45-eVpOaV6IZUFqQKCSQWFORkJieWZObn6WcV5-dlgQ0uRjW4GNngAqYKhiZGfA4FAIN8dQDMAAAA",
            urlCaptor.value)
    }

    private fun request(url: HttpUrl): Request.Builder {
        return Request.Builder().url(url)
    }

    private fun newCall(client: OkHttpClient, request: Request, mockResponse: MockResponse) {
        server.enqueue(mockResponse)
        val response = client.newCall(request).execute()
        response.body()!!.close()
    }

    private fun createRequestBody(body: String) = RequestBody.create(MediaType.parse(PLAIN_STRING), body)

    private fun createClientArgCaptor(okLogBuilderAction: (OkLogInterceptor.Builder) -> Unit = {}): ClientArgCaptor {
        val urlCaptor = ArgumentCaptor.forClass(String::class.java)

        val logInterceptor = mock(LogInterceptor::class.java)
        `when`(logInterceptor.onLog(urlCaptor.capture())).thenReturn(true)

        val interceptorBuilder = OkLogInterceptor.builder()
            .setLogInterceptor(logInterceptor)

        okLogBuilderAction(interceptorBuilder)

        interceptorBuilder.withResponseDuration(false)

        val interceptor = interceptorBuilder.build()

        val client = OkHttpClient()
        client.interceptors().add(interceptor)

        return ClientArgCaptor(client, urlCaptor)
    }

    data class ClientArgCaptor(val client: OkHttpClient, val urlCaptor: ArgumentCaptor<String>)

    class MockWebServerPort(private val mockWebServer: MockWebServer) : ExternalResource() {

        override fun before() {
            super.before()
            mockWebServer.start(5000)
        }

        override fun after() {
            super.after()
            try {
                mockWebServer.shutdown()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}
