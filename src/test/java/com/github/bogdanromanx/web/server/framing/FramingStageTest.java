package com.github.bogdanromanx.web.server.framing;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;
import com.github.bogdanromanx.web.server.types.HttpMethod;
import com.github.bogdanromanx.web.server.types.HttpProtocol;
import com.github.bogdanromanx.web.server.types.RequestLine;
import com.github.bogdanromanx.web.server.types.headers.ContentLength;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class FramingStageTest {

    private static final ParsingConfig config = ParsingConfig.of(20, 10, 10, 10, 10);

    private static ActorSystem system;
    private static ActorMaterializer materializer;

    @BeforeClass
    public static void beforeClass() {
        system = ActorSystem.create();
        materializer = ActorMaterializer.create(system);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        system.terminate();
        Await.result(system.whenTerminated(), Duration.Inf());
    }

    @Test
    public void fullFramingStage() throws ExecutionException, InterruptedException {
        ByteString bytes = ByteString.fromString("" +
                "GET / HTTP/1.0\r\n" +
                "Content-Length:2\r\n" +
                "name:value\r\n" +
                "\r\n" +
                "abcd"
        );
        CompletionStage<LinkedList<RequestFrame>> completionStage = Source.single(bytes)
                .via(new FramingStage(config))
                .take(6)
                .fold(new LinkedList<RequestFrame>(), (list, frame) -> {
                    list.add(frame);
                    return list;
                }).runWith(Sink.head(), materializer);

        LinkedList<RequestFrame> frames = completionStage.toCompletableFuture().get();
        List<RequestFrame> expected = Arrays.asList(
                RequestFrame.requestLine(RequestLine.of(
                        HttpMethod.of("GET"), URI.create("/"), HttpProtocol.HTTP_1_0)),
                RequestFrame.header(ContentLength.of(2).raw()),
                RequestFrame.header(RawHeader.of("name", "value")),
                RequestFrame.start(2),
                RequestFrame.bytes(ByteString.fromString("ab")),
                RequestFrame.end()
        );
        assertThat(frames, equalTo(expected));
    }
}
