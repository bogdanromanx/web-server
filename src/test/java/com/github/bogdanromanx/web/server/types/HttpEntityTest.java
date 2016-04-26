package com.github.bogdanromanx.web.server.types;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class HttpEntityTest {

    @Test
    public void emptyEntityZeroLength() {
        assertThat(HttpEntity.empty().length(), equalTo(0L));
    }

    @Test
    public void emptyEntityStringRepresentation() {
        assertThat(HttpEntity.empty().toString(), equalTo("HttpEntity.Empty{length=0}"));
    }

    @Test
    public void emptyEntityEquality() {
        assertThat(HttpEntity.empty(), equalTo(HttpEntity.empty()));
    }

    @Test
    public void strictEntityNullByteStringToEmpty() {
        HttpEntity.Strict strict = HttpEntity.strict(null);
        assertThat(strict.length(), equalTo(0L));
        assertThat(strict.bytes().isEmpty(), equalTo(true));
    }

    @Test
    public void strictEntityLengthFromByteString() {
        ByteString bytes = ByteString.fromString("bytes");
        assertThat(HttpEntity.strict(bytes).length(), equalTo((long) bytes.length()));
    }

    @Test
    public void strictEntityCorrectBytes() {
        ByteString bytes = ByteString.fromString("bytes");
        assertThat(HttpEntity.strict(bytes).bytes(), equalTo(bytes));
    }

    @Test
    public void strictEntityEquality() {
        HttpEntity.Strict strict = HttpEntity.strict(ByteString.fromString("bytes"));
        Set<HttpEntity> set = new HashSet<>();
        set.add(strict);
        assertThat(set, contains(HttpEntity.strict(ByteString.fromString("bytes"))));
    }

    @Test
    public void strictEntityStringRepresentation() {
        ByteString bytes = ByteString.fromString("bytes");
        HttpEntity.Strict strict = HttpEntity.strict(bytes);
        assertThat(strict.toString(), equalTo("HttpEntity.Strict{length=5}"));
    }

    @Test(expected = NullPointerException.class)
    public void streamingThrowsOnNullSource() {
        HttpEntity.streaming(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void streamingThrowsOnNegativeLength() {
        HttpEntity.streaming(Source.empty(), -1);
    }

    @Test
    public void streamingEntityCorrectlyConstructed() {
        HttpEntity.Streaming streaming = HttpEntity.streaming(Source.empty(), 0);
        assertThat(streaming.source(), equalTo(Source.empty()));
        assertThat(streaming.length(), equalTo(0L));
    }

    @Test
    public void streamingEntityEquality() {
        HttpEntity.Streaming streaming = HttpEntity.streaming(Source.empty(), 0);
        Set<HttpEntity> set = new HashSet<>();
        set.add(streaming);
        assertThat(set, contains(HttpEntity.streaming(Source.empty(), 0)));
    }

    @Test
    public void streamingEntityStringRepresentation() {
        HttpEntity.Streaming streaming = HttpEntity.streaming(Source.empty(), 2);
        assertThat(streaming.toString(), equalTo("HttpEntity.Streaming{length=2}"));
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    @Test
    public void streamingEntityShouldCompleteWhenConsumed() throws Exception {
        HttpEntity.Streaming streaming = HttpEntity.streaming(Source.empty(), 0);
        ActorSystem system = ActorSystem.create();
        try {
            Materializer mat = ActorMaterializer.create(system);
            assertThat(streaming.consume(mat).toCompletableFuture().get(), equalTo(NotUsed.getInstance()));
        } finally {
            system.terminate();
            Await.result(system.whenTerminated(), Duration.Inf());
        }
    }
}
