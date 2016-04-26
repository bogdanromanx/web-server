package com.github.bogdanromanx.web.server.types;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class HttpProtocolTest {

    @Test
    public void httpProtocol10Value() {
        assertThat(HttpProtocol.HTTP_1_0.value(), equalTo("HTTP/1.0"));
    }

    @Test
    public void httpProtocol11Value() {
        assertThat(HttpProtocol.HTTP_1_1.value(), equalTo("HTTP/1.1"));
    }

    @Test
    public void httpProtocol10ToString() {
        assertThat(HttpProtocol.HTTP_1_0.toString(), equalTo("HTTP/1.0"));
    }

    @Test
    public void httpProtocol11ToString() {
        assertThat(HttpProtocol.HTTP_1_1.toString(), equalTo("HTTP/1.1"));
    }

    @Test
    public void httpProtocol10OfMatchingString() {
        assertThat(HttpProtocol.of("HTTP/1.0"), equalTo(Optional.of(HttpProtocol.HTTP_1_0)));
    }

    @Test
    public void httpProtocol11OfMatchingString() {
        assertThat(HttpProtocol.of("HTTP/1.1"), equalTo(Optional.of(HttpProtocol.HTTP_1_1)));
    }

    @Test
    public void httpProtocolOfUnknownString() {
        assertThat(HttpProtocol.of("HTTP/random"), equalTo(Optional.empty()));
    }

    @Test
    public void httpProtocolFromNull() {
        assertThat(HttpProtocol.of(null), equalTo(Optional.empty()));
    }
}
