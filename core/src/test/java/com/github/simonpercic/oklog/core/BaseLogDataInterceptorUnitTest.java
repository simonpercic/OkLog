package com.github.simonpercic.oklog.core;

import org.junit.Test;

import java.io.IOException;

import okio.Buffer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class BaseLogDataInterceptorUnitTest {

    @Test public void isPlaintext() throws IOException {
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer()));
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer().writeUtf8("abc")));
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer().writeUtf8("new\r\nlines")));
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer().writeUtf8("white\t space")));
        assertTrue(BaseLogDataInterceptor.isPlaintext(new Buffer().writeByte(0x80)));
        assertFalse(BaseLogDataInterceptor.isPlaintext(new Buffer().writeByte(0x00)));
        assertFalse(BaseLogDataInterceptor.isPlaintext(new Buffer().writeByte(0xc0)));
    }
}
