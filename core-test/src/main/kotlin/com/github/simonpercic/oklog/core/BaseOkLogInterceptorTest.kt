package com.github.simonpercic.oklog.core

import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(AndroidJUnit4::class)
abstract class BaseOkLogInterceptorTest<MockWebServer, HttpUrl, MockResponse, RequestBuilder, Request, RequestBody, OkHttpClient, OkLogInterceptorBuilder, OkLogInterceptor> {

    companion object {
        const val PLAIN_STRING = "text/plain; charset=utf-8"
        private const val REQUEST_HEADER_NAME = "ReqHeaderName"
        private const val REQUEST_HEADER_VALUE = "ReqExampleValue"
        private const val RESPONSE_HEADER_NAME = "ResHeaderName"
        private const val RESPONSE_HEADER_VALUE = "ResExampleValue"
        private const val CONTENT_TYPE = "Content-Type"
        private const val APPLICATION_JSON = "application/json"
    }

    abstract val server: MockWebServer

    abstract fun mockWebServerUrl(server: MockWebServer, path: String): HttpUrl
    abstract fun mockWebServerEnqueue(server: MockWebServer, response: MockResponse)
    abstract fun mockResponse(): MockResponse
    abstract fun mockResponseSetBody(mockResponse: MockResponse, body: String): MockResponse
    abstract fun mockResponseAddHeader(mockResponse: MockResponse, name: String, value: Any): MockResponse
    abstract fun mockResponseSetStatus(mockResponse: MockResponse, status: String): MockResponse
    abstract fun request(url: HttpUrl): RequestBuilder
    abstract fun requestAddHeader(requestBuilder: RequestBuilder, name: String, value: String): RequestBuilder
    abstract fun requestPost(requestBuilder: RequestBuilder, body: RequestBody): RequestBuilder
    abstract fun requestPut(requestBuilder: RequestBuilder, body: RequestBody): RequestBuilder
    abstract fun requestHead(requestBuilder: RequestBuilder): RequestBuilder
    abstract fun requestDelete(requestBuilder: RequestBuilder, body: RequestBody?): RequestBuilder
    abstract fun requestBuild(requestBuilder: RequestBuilder): Request
    abstract fun clientNewCallExecute(client: OkHttpClient, request: Request)
    abstract fun okHttpClientWithInterceptor(interceptor: OkLogInterceptor): OkHttpClient

    fun MockWebServer.url(path: String): HttpUrl = mockWebServerUrl(this, path)
    fun MockWebServer.enqueue(response: MockResponse) = mockWebServerEnqueue(this, response)
    fun MockResponse.setBody(body: String): MockResponse = mockResponseSetBody(this, body)
    fun MockResponse.addHeader(name: String, value: Any): MockResponse = mockResponseAddHeader(this, name, value)
    fun MockResponse.setStatus(status: String): MockResponse = mockResponseSetStatus(this, status)
    fun RequestBuilder.addHeader(name: String, value: String): RequestBuilder = requestAddHeader(this, name, value)
    fun RequestBuilder.post(body: RequestBody): RequestBuilder = requestPost(this, body)
    fun RequestBuilder.put(body: RequestBody): RequestBuilder = requestPut(this, body)
    fun RequestBuilder.head(): RequestBuilder = requestHead(this)
    fun RequestBuilder.delete(body: RequestBody?): RequestBuilder = requestDelete(this, body)
    fun RequestBuilder.build(): Request = requestBuild(this)
    fun OkHttpClient.newCallExecute(request: Request) = clientNewCallExecute(this, request)

    abstract fun createRequestBody(body: String): RequestBody
    abstract fun okLogInterceptorBuilder(): OkLogInterceptorBuilder
    abstract fun okLogInterceptorBuilderSetLogInterceptor(okLogInterceptorBuilder: OkLogInterceptorBuilder, logInterceptor: LogInterceptor): OkLogInterceptorBuilder
    abstract fun okLogInterceptorBuilderWithResponseDuration(okLogInterceptorBuilder: OkLogInterceptorBuilder, responseDuration: Boolean): OkLogInterceptorBuilder
    abstract fun okLogInterceptorBuilderWithAllLogData(okLogInterceptorBuilder: OkLogInterceptorBuilder): OkLogInterceptorBuilder
    abstract fun okLogInterceptorBuild(okLogInterceptorBuilder: OkLogInterceptorBuilder): OkLogInterceptor

    fun OkLogInterceptorBuilder.setLogInterceptor(logInterceptor: LogInterceptor): OkLogInterceptorBuilder = okLogInterceptorBuilderSetLogInterceptor(this, logInterceptor)
    fun OkLogInterceptorBuilder.withResponseDuration(responseDuration: Boolean): OkLogInterceptorBuilder = okLogInterceptorBuilderWithResponseDuration(this, responseDuration)
    fun OkLogInterceptorBuilder.withAllLogData(): OkLogInterceptorBuilder = okLogInterceptorBuilderWithAllLogData(this)
    fun OkLogInterceptorBuilder.buildInterceptor(): OkLogInterceptor = okLogInterceptorBuild(this)

    @Test
    fun testGetDefault() {
        val (client, urlCaptor) = createClientArgCaptor()

        val url = server.url("/shows")

        val mockResponse = mockResponse()
            .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}},{\"id\"" +
                ":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":\"Showtime\"}}]")

        newCall(client, request(url).build(), mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAIuuVspMUbIy1FHKS8xNVbJSCs1LSS1SKMlIVXDJBw" +
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

        val mockResponse = mockResponse()
            .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}},{\"id\"" +
                ":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":\"Showtime\"}}]")
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAIuuVspMUbIy1FHKS8xNVbJSCs1LSS1SKMlIVXDJBw" +
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

        val mockResponse = mockResponse()
            .setBody("{\"show\":{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\"" +
                ":\"HBO\"}},\"watched_count\":107}")

        newCall(client, request(url).post(requestBody).build(), mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAKtWKs7IL1eyqlbKTFGyMtVRykvMTVWyUgopKk1VcE" +
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

        val mockResponse = mockResponse()
            .setBody("{\"show\":{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\"" +
                ":\"HBO\"}},\"watched_count\":107}")
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAKtWKs7IL1eyqlbKTFGyMtVRykvMTVWyUgopKk1VcE" +
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

        val mockResponse = mockResponse()
            .setBody("{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}}")
            .setStatus("HTTP/1.1 %d %s".format(201, "Created"))

        newCall(client, request(url).put(requestBody).build(), mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1" +
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

        val mockResponse = mockResponse()
            .setBody("{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}}")
            .setStatus("HTTP/1.1 %d %s".format(201, "Created"))
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1" +
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

        val mockResponse = mockResponse()
            .setBody("{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}}")

        newCall(client, request(url).delete(null).build(), mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1" +
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

        val mockResponse = mockResponse()
            .setBody("{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}}")
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(RESPONSE_HEADER_NAME, RESPONSE_HEADER_VALUE)

        newCall(client, request, mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/H4sIAAAAAAAAAKtWykxRsjLVUcpLzE1VslIKKSpNVXBJLUlNLsksS1" +
                "XSUSoqzSvJBEmZGQAVpZaU5xdlK1lVg7VZwLV5OPkr1dYCAB5WJYFNAAAA?d=H4sIAAAAAAAAAONic3H1cQ1xFZLJKCkpsNLXz8" +
                "lPTszJyC8usTI1MDDQL87IL9c3leIAyeob6hlqMBgpcPEGpRZ6pCampBb5JeamCvEDua4VibkFOalhiTmlqRZMDgweJxiDmPy9E" +
                "3yzFLh4nPPzSlLzSnRDKgtShQQSCwpyMpMTSzLz8_SzivPzssAGFqMaWIxsYAFjhW8TI14XAgBwEMrmyAAAAA==",
            urlCaptor.value)
    }

    @Test
    fun testHeaderDefault() {
        val (client, urlCaptor) = createClientArgCaptor()

        val url = server.url("/shows")

        val mockResponse = mockResponse()
            .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\"" +
                "}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":\"HBO\"}},{\"id\"" +
                ":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The CW\"}},{\"id\"" +
                ":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":\"Showtime\"}}]")
            .setStatus("HTTP/1.1 %d %s".format(404, "Not Found"))

        newCall(client, request(url).head().build(), mockResponse)

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/0?d=H4sIAAAAAAAAAONi8XB1dBGSzigpKbDS18_JT07MycgvLrEyNT" +
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

        val mockResponse = mockResponse()
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

        Assert.assertEquals(
            "http://oklog.responseecho.com/v1/r/0?d=H4sIAAAAAAAAAONi8XB1dBGSzigpKbDS18_JT07MycgvLrEyNT" +
                "Aw0C_OyC8vluIASeob6hlqMBgpcPEGpRZ6pCampBb5JeamCvEDua4VibkFOalhiTmlqRZMDgweU5iDOP3ySxTc8kvzUhLOMWcpc" +
                "PE45-eVpOaV6IZUFqQKCSQWFORkJieWZObn6WcV5-dlgQ0uRjW4GNngAqYKhiZGfA4FAIN8dQDMAAAA",
            urlCaptor.value)
    }

    private fun newCall(client: OkHttpClient, request: Request, mockResponse: MockResponse) {
        server.enqueue(mockResponse)
        client.newCallExecute(request)
    }

    private fun createClientArgCaptor(okLogBuilderAction: (OkLogInterceptorBuilder) -> Unit = {}): ClientArgCaptor<OkHttpClient> {
        val urlCaptor = ArgumentCaptor.forClass(String::class.java)

        val logInterceptor = Mockito.mock(LogInterceptor::class.java)
        Mockito.`when`(logInterceptor.onLog(urlCaptor.capture())).thenReturn(true)

        val interceptorBuilder = okLogInterceptorBuilder()
            .setLogInterceptor(logInterceptor)

        okLogBuilderAction(interceptorBuilder)

        interceptorBuilder.withResponseDuration(false)

        val interceptor = interceptorBuilder.buildInterceptor()

        val client = okHttpClientWithInterceptor(interceptor)

        return ClientArgCaptor(client, urlCaptor)
    }

    data class ClientArgCaptor<OkHttpClient>(val client: OkHttpClient, val urlCaptor: ArgumentCaptor<String>)
}
