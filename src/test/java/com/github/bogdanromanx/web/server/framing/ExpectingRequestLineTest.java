package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.parsing.ParsingException;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;
import com.github.bogdanromanx.web.server.types.HttpMethod;
import com.github.bogdanromanx.web.server.types.HttpProtocol;
import com.github.bogdanromanx.web.server.types.RequestLine;
import org.junit.Test;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ExpectingRequestLineTest {

    private static ExpectingRequestLine state(ByteString buffer) {
        return new ExpectingRequestLine(ParsingConfig.of(10, 10, 10, 10, 10), buffer);
    }

    @Test
    public void producesAFrame() {
        ExpectingRequestLine state = state(ByteString.fromString("GET / HTTP/1.0\r\na"));
        RequestLine expected = RequestLine.of(HttpMethod.of("GET"), URI.create("/"), HttpProtocol.HTTP_1_0);
        assertThat(state.frame(), equalTo(Optional.of(RequestFrame.requestLine(expected))));
        assertThat(state.hasBytes(), equalTo(true));
        assertThat(state.next(), instanceOf(ExpectingHeader.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingHeader.class));
    }

    @Test
    public void doesNotProduceAFrame() {
        ExpectingRequestLine state = state(ByteString.fromString("GET / HT"));
        assertThat(state.frame(), equalTo(Optional.empty()));
        assertThat(state.hasBytes(), equalTo(true));
        assertThat(state.next(), instanceOf(ExpectingRequestLine.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingRequestLine.class));
    }

    @Test(expected = ParsingException.class)
    public void bubblesUpExceptions() {
        state(ByteString.fromString("TOO_LONG_METHOD / HTTP/1.0\r\na"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullConfig() {
        new ExpectingRequestLine(null, ByteString.empty());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullBuffer() {
        new ExpectingRequestLine(ParsingConfig.of(10, 10, 10, 10, 10), null);
    }
}
