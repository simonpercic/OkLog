package com.github.simonpercic.oklog3;

import com.github.simonpercic.oklog.core.LogDataBuilder;
import com.github.simonpercic.oklog.core.LogManager;
import com.github.simonpercic.oklog.coretest.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * OkLogInterceptor unit test. Inspired by: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class OkLogInterceptorUnitTest {

    private static final String GET = "GET";

    @Mock LogManager applicationLogManager;

    @Rule public final MockWebServer server = new MockWebServer();

    private HttpUrl url;

    @Before
    public void setUp() throws Exception {
        url = server.url("/");
    }

    @Test public void connectFail() throws IOException {
        OkLogInterceptor applicationInterceptor = new OkLogInterceptor(applicationLogManager);

        OkHttpClient client = new Builder()
                .dns(new Dns() {
                    @Override public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                        throw new UnknownHostException("reason");
                    }
                })
                .addInterceptor(applicationInterceptor)
                .build();

        try {
            client.newCall(request().build()).execute();
            fail();
        } catch (UnknownHostException expected) {
            LogDataBuilder expectedValue = new LogDataBuilder()
                    .requestMethod(GET)
                    .requestUrl(url.toString())
                    .requestBodyState(LogDataBuilder.NO_BODY)
                    .requestFailed();

            LogDataBuilder appValue = TestUtils.getLogData(applicationLogManager);
            TestUtils.assertData(expectedValue, appValue);
            assertNull(appValue.getRequestHeaders());
        }
    }

    private Request.Builder request() {
        return new Request.Builder().url(url);
    }
}
