package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.parsing.ParsingException;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;
import com.github.bogdanromanx.web.server.types.headers.ContentLength;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ExpectingEmptyLineOrHeaderTest {

    private static ExpectingEmptyLineOrHeader state(ByteString buffer, List<RawHeader> headers) {
        return new ExpectingEmptyLineOrHeader(ParsingConfig.of(10, 10, 10, 10, 10), buffer, headers);
    }

    private static ExpectingEmptyLineOrHeader state(ByteString buffer) {
        return state(buffer, Collections.emptyList());
    }

    @Test
    public void consumesAnEmptyLine() {
        ExpectingEmptyLineOrHeader state = state(ByteString.fromString("\r\n"));
        assertThat(state.frame(), equalTo(Optional.of(RequestFrame.end())));
        assertThat(state.hasBytes(), equalTo(false));
        assertThat(state.next(), instanceOf(ExpectingRequestLine.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingRequestLine.class));
    }

    @Test
    public void consumesAnEmptyLineExpectingEntity() {
        List<RawHeader> headers = Collections.singletonList(ContentLength.of(1).raw());
        ExpectingEmptyLineOrHeader state = state(ByteString.fromString("\r\n"), headers);
        assertThat(state.frame(), equalTo(Optional.of(RequestFrame.start(1))));
        assertThat(state.hasBytes(), equalTo(false));
        assertThat(state.next(), instanceOf(ExpectingEntityChunk.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingEntityChunk.class));
    }

    @Test
    public void producesAHeaderFrame() {
        ExpectingEmptyLineOrHeader state = state(ByteString.fromString("name:value\r\na"));
        RawHeader expected = RawHeader.of("name", "value");
        assertThat(state.frame(), equalTo(Optional.of(RequestFrame.header(expected))));
        assertThat(state.hasBytes(), equalTo(true));
        assertThat(state.next(), instanceOf(ExpectingEmptyLineOrHeader.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingEmptyLineOrHeader.class));
    }

    @Test
    public void doesNotProduceAFrame() {
        ExpectingEmptyLineOrHeader state = state(ByteString.fromString("n"));
        assertThat(state.frame(), equalTo(Optional.empty()));
        assertThat(state.hasBytes(), equalTo(true));
        assertThat(state.next(), instanceOf(ExpectingEmptyLineOrHeader.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingEmptyLineOrHeader.class));
    }

    @Test(expected = ParsingException.class)
    public void bubblesUpExceptions() {
        state(ByteString.fromString("TOO_LONG_HEADER_NAME:value\r\na"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullConfig() {
        new ExpectingEmptyLineOrHeader(null, ByteString.empty(), Collections.emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullBuffer() {
        state(null, Collections.emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullHeaders() {
        state(ByteString.empty(), null);
    }
}
