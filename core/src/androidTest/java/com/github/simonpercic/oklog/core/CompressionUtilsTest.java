package com.github.simonpercic.oklog.core;

import junit.framework.Assert;

import org.junit.Test;

/**
 * CompressionUtils unit test.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class CompressionUtilsTest {

    @Test
    public void testGzipBase64Empty() throws Exception {
        String nullResult = CompressionUtils.gzipBase64((String) null);
        Assert.assertEquals(null, nullResult);

        String emptyResult = CompressionUtils.gzipBase64("");
        Assert.assertEquals("", emptyResult);
    }

    @Test
    public void testGzipBase64() throws Exception {
        String json = "[\n"
                + "  {\n"
                + "    \"name\":\"parent-2.5.0\",\n"
                + "    \"zipball_url\":\"https://api.github.com/repos/square/okhttp/zipball/parent-2.5.0\",\n"
                + "    \"tarball_url\":\"https://api.github.com/repos/square/okhttp/tarball/parent-2.5.0\",\n"
                + "    \"commit\":{\n"
                + "      \"sha\":\"f36bed41a87296d8cd641b5c0602fcb860fe7043\",\n"
                + "      \"url\":\"https://api.github.com/repos/square/okhttp/commits/f36bed41a87296d8cd641b5c0602fcb860fe7043\"\n"
                + "    }\n"
                + "  },\n"
                + "  {\n"
                + "    \"name\":\"parent-2.4.0-RC1\",\n"
                + "    \"zipball_url\":\"https://api.github.com/repos/square/okhttp/zipball/parent-2.4.0-RC1\",\n"
                + "    \"tarball_url\":\"https://api.github.com/repos/square/okhttp/tarball/parent-2.4.0-RC1\",\n"
                + "    \"commit\":{\n"
                + "      \"sha\":\"5cc7dba3ad1597f5ca7f5380a4845d0385fa497f\",\n"
                + "      \"url\":\"https://api.github.com/repos/square/okhttp/commits/5cc7dba3ad1597f5ca7f5380a4845d0385fa497f\"\n"
                + "    }\n"
                + "  },\n"
                + "  {\n"
                + "    \"name\":\"parent-2.4.0\",\n"
                + "    \"zipball_url\":\"https://api.github.com/repos/square/okhttp/zipball/parent-2.4.0\",\n"
                + "    \"tarball_url\":\"https://api.github.com/repos/square/okhttp/tarball/parent-2.4.0\",\n"
                + "    \"commit\":{\n"
                + "      \"sha\":\"7298bee253df7a9b8bf3cf4438aa077a28502c01\",\n"
                + "      \"url\":\"https://api.github.com/repos/square/okhttp/commits/7298bee253df7a9b8bf3cf4438aa077a28502c01\"\n"
                + "    }\n"
                + "  },\n"
                + "  {\n"
                + "    \"name\":\"parent-2.3.0\",\n"
                + "    \"zipball_url\":\"https://api.github.com/repos/square/okhttp/zipball/parent-2.3.0\",\n"
                + "    \"tarball_url\":\"https://api.github.com/repos/square/okhttp/tarball/parent-2.3.0\",\n"
                + "    \"commit\":{\n"
                + "      \"sha\":\"c49eedeba374e06bc82f5aed7c67506f65dd2482\",\n"
                + "      \"url\":\"https://api.github.com/repos/square/okhttp/commits/c49eedeba374e06bc82f5aed7c67506f65dd2482\"\n"
                + "    }\n"
                + "  },\n"
                + "  {\n"
                + "    \"name\":\"parent-2.2.0\",\n"
                + "    \"zipball_url\":\"https://api.github.com/repos/square/okhttp/zipball/parent-2.2.0\",\n"
                + "    \"tarball_url\":\"https://api.github.com/repos/square/okhttp/tarball/parent-2.2.0\",\n"
                + "    \"commit\":{\n"
                + "      \"sha\":\"6aef5ab3c5dd964156be8e3a2501ff740dda1516\",\n"
                + "      \"url\":\"https://api.github.com/repos/square/okhttp/commits/6aef5ab3c5dd964156be8e3a2501ff740dda1516\"\n"
                + "    }\n"
                + "  },\n"
                + "  {\n"
                + "    \"name\":\"parent-2.1.0-RC1\",\n"
                + "    \"zipball_url\":\"https://api.github.com/repos/square/okhttp/zipball/parent-2.1.0-RC1\",\n"
                + "    \"tarball_url\":\"https://api.github.com/repos/square/okhttp/tarball/parent-2.1.0-RC1\",\n"
                + "    \"commit\":{\n"
                + "      \"sha\":\"7b6771bb2939da2e7d42ca359a7cfc0a6e86272b\",\n"
                + "      \"url\":\"https://api.github.com/repos/square/okhttp/commits/7b6771bb2939da2e7d42ca359a7cfc0a6e86272b\"\n"
                + "    }\n"
                + "  },\n"
                + "  {\n"
                + "    \"name\":\"parent-2.1.0\",\n"
                + "    \"zipball_url\":\"https://api.github.com/repos/square/okhttp/zipball/parent-2.1.0\",\n"
                + "    \"tarball_url\":\"https://api.github.com/repos/square/okhttp/tarball/parent-2.1.0\",\n"
                + "    \"commit\":{\n"
                + "      \"sha\":\"86d4716b58ad3652e5e768bc52f807817a97c61c\",\n"
                + "      \"url\":\"https://api.github.com/repos/square/okhttp/commits/86d4716b58ad3652e5e768bc52f807817a97c61c\"\n"
                + "    }\n"
                + "  }\n"
                + "]";

        String compressed = CompressionUtils.gzipBase64(json);

        String expected = "H4sIAAAAAAAAALXVzY7TMBAH8Ps-RZXzbmKPPZ5Jr7wBV4TQ-ItWtNvQpBfQvjuGVkJa7aoGxYfk\n"
                + "ENvzT_yLxp8eNpuf5dpsumc5pm7bTXJOz8sT9Nir7vE69GM_eTkcvlzOhzJjtyzTvB0Gmfb91_2y\n"
                + "u_g-nI7DOU2neZi_X0qB4fTt96zhtnB4q-gi5_8relv4ZtGy5rhfuu31m8qDeSelejbOp2i1MMHo\n"
                + "IoforPYYlFOQg2enciJlza1MWffPb3VNnofqqD9JL-X-8vg-gu3V08cPen2IV4XXw3hV-B0QDIGi\n"
                + "FyNR40gZg5SbYSWWLUZlGLPYMrACSHVULUgTjCYQdxDKD8o-JUATM8no2WcTsrWGRRSRAKOCoPQK\n"
                + "CNVRVQimBYJpgWDuIgQ7phRT-UHJJuV8YMgoKVJwhMplhzGCZVgBoTqqCgFaIEALBLiL4CSVnfAm\n"
                + "lB0YS7_G0sM5GQFUOmeyKkbRqN0KCNVRVQi61fmgW50Puup8IO-ItPcwmjEKJIoWghgchUIOSlxi\n"
                + "BwR-jdZUG1UL0gSjCcQdBHbRknYeWaJxCAkTOfYBIbMi1qWRl9ahwwoI1VF_ER4-_wL2RqAwvQoA\n"
                + "AA==\n";

        Assert.assertEquals(expected, compressed);
    }
}
