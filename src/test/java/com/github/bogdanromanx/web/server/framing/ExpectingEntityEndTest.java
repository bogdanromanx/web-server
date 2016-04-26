package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ExpectingEntityEndTest {

    private static ExpectingEntityEnd state(ByteString buffer) {
        return new ExpectingEntityEnd(ParsingConfig.of(10, 10, 10, 10, 10), buffer);
    }

    @Test
    public void producesAFrame() {
        ExpectingEntityEnd state = state(ByteString.fromString("a"));
        assertThat(state.frame(), equalTo(Optional.of(RequestFrame.end())));
        assertThat(state.hasBytes(), equalTo(true));
        assertThat(state.next(), instanceOf(ExpectingRequestLine.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingRequestLine.class));
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
