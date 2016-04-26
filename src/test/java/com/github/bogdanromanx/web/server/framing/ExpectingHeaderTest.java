package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.parsing.ParsingException;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ExpectingHeaderTest {

    private static ExpectingHeader state(ByteString buffer) {
        return new ExpectingHeader(ParsingConfig.of(10, 10, 10, 10, 10), buffer);
    }

    @Test
    public void producesAFrame() {
        ExpectingHeader state = state(ByteString.fromString("name:value\r\na"));
        RawHeader expected = RawHeader.of("name", "value");
        assertThat(state.frame(), equalTo(Optional.of(RequestFrame.header(expected))));
        assertThat(state.hasBytes(), equalTo(true));
        assertThat(state.next(), instanceOf(ExpectingEmptyLineOrHeader.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingEmptyLineOrHeader.class));
    }

    @Test
    public void doesNotProduceAFrame() {
        ExpectingHeader state = state(ByteString.fromString("name:value"));
        assertThat(state.frame(), equalTo(Optional.empty()));
        assertThat(state.hasBytes(), equalTo(true));
        assertThat(state.next(), instanceOf(ExpectingHeader.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingHeader.class));
    }

    @Test(expected = ParsingException.class)
    public void bubblesUpExceptions() {
        state(ByteString.fromString("TOO_LONG_HEADER_NAME:value\r\na"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullConfig() {
        new ExpectingHeader(null, ByteString.empty());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullBuffer() {
        new ExpectingHeader(ParsingConfig.of(10, 10, 10, 10, 10), null);
    }
}
