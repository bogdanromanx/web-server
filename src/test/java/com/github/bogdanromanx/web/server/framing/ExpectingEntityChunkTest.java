package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ExpectingEntityChunkTest {

    private static ExpectingEntityChunk state(ByteString buffer, long length) {
        return new ExpectingEntityChunk(ParsingConfig.of(10, 10, 10, 10, 10), buffer, length);
    }

    @Test
    public void producesAFrame() {
        ByteString chunk = ByteString.fromString("a");
        ExpectingEntityChunk state = state(chunk, 10);
        assertThat(state.frame(), equalTo(Optional.of(RequestFrame.bytes(chunk))));
        assertThat(state.hasBytes(), equalTo(false));
        assertThat(state.next(), instanceOf(ExpectingEntityChunk.class));
        assertThat(state.next(chunk), instanceOf(ExpectingEntityChunk.class));
    }

    @Test
    public void producesTheLastFrame() {
        ByteString chunk = ByteString.fromString("ab");
        ExpectingEntityChunk state = state(chunk, 1);
        assertThat(state.frame(), equalTo(Optional.of(RequestFrame.bytes(ByteString.fromString("a")))));
        assertThat(state.hasBytes(), equalTo(true));
        assertThat(state.next(), instanceOf(ExpectingEntityEnd.class));
        assertThat(state.next(chunk), instanceOf(ExpectingEntityEnd.class));
    }

    @Test
    public void doesNotProduceAFrame() {
        ExpectingEntityChunk state = state(ByteString.empty(), 10);
        assertThat(state.frame(), equalTo(Optional.empty()));
        assertThat(state.hasBytes(), equalTo(false));
        assertThat(state.next(), instanceOf(ExpectingEntityChunk.class));
        assertThat(state.next(ByteString.fromString("a")), instanceOf(ExpectingEntityChunk.class));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullConfig() {
        new ExpectingEntityChunk(null, ByteString.empty(), 1);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullBuffer() {
        new ExpectingEntityChunk(ParsingConfig.of(10, 10, 10, 10, 10), null, 0);
    }
}
