package com.github.simonpercic.oklog3;

import android.support.test.runner.AndroidJUnit4;

import com.github.simonpercic.oklog.core.LogInterceptor;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(AndroidJUnit4.class)
public class OkLogInterceptorTest {

    private final MockWebServer server = new MockWebServer();
    @Rule public final MockWebServerPort serverPort = new MockWebServerPort(server);

    private OkHttpClient client;
    private HttpUrl url;

    @Test
    public void testGetDefault() throws Exception {
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        LogInterceptor logInterceptor = Mockito.mock(LogInterceptor.class);
        Mockito.when(logInterceptor.onLog(urlCaptor.capture())).thenReturn(true);

        OkLogInterceptor interceptor = OkLogInterceptor.builder()
                .setLogInterceptor(logInterceptor)
                .withResponseDuration(false)
                .build();

        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        url = server.url("/shows");

        MockResponse mockResponse = new MockResponse()
                .setBody("[{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"network\":{\"id\":2,\"name\":\"CBS\""
                        + "}},{\"id\":2,\"name\":\"Person of Interest\",\"runtime\":60,\"network\":{\"id\":2,\"name\":"
                        + "\"CBS\"}},{\"id\":4,\"name\":\"Arrow\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\"The "
                        + "CW\"}},{\"id\":5,\"name\":\"True Detective\",\"runtime\":60,\"network\":{\"id\":8,\"name\":"
                        + "\"HBO\"}},{\"id\":6,\"name\":\"The 100\",\"runtime\":60,\"network\":{\"id\":5,\"name\":\""
                        + "The CW\"}},{\"id\":7,\"name\":\"Homeland\",\"runtime\":60,\"network\":{\"id\":9,\"name\":"
                        + "\"Showtime\"}}]");

        newCall(request().build(), mockResponse);

        String loggedUrl = urlCaptor.getValue();
        Assert.assertEquals(
                "http://responseecho-simonpercic.rhcloud.com/v1/r/H4sIAAAAAAAAAIuuVspMUbIy1FHKS8xNVbJSCs1LSS1SKMlIVX"
                        + "DJBwroKBWV5pVkgqTMDICKUkvK84uylawg2ozg2pydgpVqa3XQhQNSi4rz8xTy0xQ880pSi1KLS8gz0QQu7FhUlF9"
                        + "OwBBTuOoQoD-cwxHmIMkUlQK9mFqSmlySWUbInxZwbR5O_gjTzFDsMTQwINtd5ggLgKGek5iXQsAoS7iG4Iz8crCy"
                        + "2tpYALkEI-7OAQAA?d=H4sIAAAAAAAAAONidncNEZLOKCkpsNLXz8lPTszJyC8usTI1MDDQL87ILy_WYLBgcmDwOM"
                        + "EYxOTvnXCOuYCx4hwzAHrELHA3AAAA",
                loggedUrl);
    }

    private Request.Builder request() {
        return new Request.Builder().url(url);
    }

    private void newCall(Request request, MockResponse mockResponse) throws IOException {
        server.enqueue(mockResponse);
        Response response = client.newCall(request).execute();
        response.body().close();
    }

    private static class MockWebServerPort extends ExternalResource {

        private final MockWebServer mockWebServer;

        private MockWebServerPort(MockWebServer mockWebServer) {
            this.mockWebServer = mockWebServer;
        }

        @Override protected void before() throws Throwable {
            super.before();
            mockWebServer.start(5000);
        }

        @Override protected void after() {
            super.after();
            try {
                mockWebServer.shutdown();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
