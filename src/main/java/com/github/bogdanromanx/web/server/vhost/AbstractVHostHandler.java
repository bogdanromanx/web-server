package com.github.bogdanromanx.web.server.vhost;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.javadsl.Concat;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.github.bogdanromanx.web.server.framing.FramingStage;
import com.github.bogdanromanx.web.server.framing.RequestFrame;
import com.github.bogdanromanx.web.server.handling.RequestHandler;
import com.github.bogdanromanx.web.server.settings.SettingsExtension;
import com.github.bogdanromanx.web.server.settings.VHostConfig;
import com.github.bogdanromanx.web.server.types.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Base class for all implementations of {@link VHostHandler}s.  It provides an end to end processing flow that can be
 * attached to a {@link Source} of {@link ByteString} and a {@link akka.stream.javadsl.Sink} of {@link ByteString} to
 * handle individual connections from clients.
 */
abstract class AbstractVHostHandler implements VHostHandler {

    final ActorSystem system;
    final VHostConfig vHostConfig;
    final ActorMaterializer materializer;

    private final FramingStage framingStage;

    private final RequestFoldingStage requestFoldingStage = new RequestFoldingStage();
    private final ConnectionCloseStage connectionCloseStage = new ConnectionCloseStage();

    AbstractVHostHandler(ActorSystem system, VHostConfig vHostConfig) {
        this.system = system;
        this.vHostConfig = vHostConfig;
        this.materializer = materializer();
        framingStage = new FramingStage(SettingsExtension.SettingsExtensionProvider.get(system).parsingConfig());
    }

    /**
     * Constructs a processing flow from the argument 'requestHandler' to be used for processing {@link HttpRequest}s
     * and producing {@link HttpResponse}s.
     *
     * @param requestHandler the request handler to be used by the processing flow for producing {@link HttpResponse}s
     * @return a processing flow from the argument 'requestHandler' to be used for processing {@link HttpRequest}s
     * and producing {@link HttpResponse}s.
     * @see RequestHandler
     */
    Flow<ByteString, ByteString, NotUsed> processingFlow(RequestHandler requestHandler) {
        return Flow.of(ByteString.class)
                .via(framingStage)
                .splitAfter(frame -> frame == RequestFrame.end())
                .via(requestFoldingStage)
                .mergeSubstreams()
                .mapAsync(1, req -> applyHandler(requestHandler, req))
                .via(connectionCloseStage)
                .flatMapConcat(pair -> response(pair.second().requestLine().protocol(), pair.first()));
    }

    private static ByteString CRLF = ByteString.fromString("\r\n");

    /**
     * Constructs a {@link Source} of {@link ByteString} for the argument 'protocol' and 'response' that emits the
     * status line and header bytes as {@link ByteString}.
     *
     * @param protocol the current http protocol
     * @param response the response to be sent to the client
     * @return a {@link Source} of {@link ByteString} for the argument 'protocol' and 'response' that emits the
     * status line and header bytes as {@link ByteString}.
     */
    private Source<ByteString, ?> responseMeta(HttpProtocol protocol, HttpResponse response) {
        List<ByteString> bytes = new LinkedList<>();

        ByteString status = ByteString.fromString(protocol.toString()
                + " " + response.statusCode().value()
                + " " + response.statusCode().name());

        List<ByteString> headers = response
                .headers()
                .stream()
                .map(h -> ByteString.fromString(h.name() + ":" + h.value()).concat(CRLF))
                .collect(Collectors.toList());

        bytes.add(status.concat(CRLF));
        bytes.addAll(headers);
        bytes.add(CRLF);

        return Source.from(bytes);
    }

    /**
     * Constructs a {@link Source} of {@link ByteString} for the argument 'response' that emits the bytes of the
     * 'response' entity (if one is present).
     *
     * @param response the response to be sent to the client
     * @return a {@link Source} of {@link ByteString} for the argument 'response' that emits the bytes of the
     * 'response' entity (if one is present).
     */
    private Source<ByteString, ?> responseEntity(HttpResponse response) {
        if (response.entity() instanceof HttpEntity.Streaming) {
            HttpEntity.Streaming entity = (HttpEntity.Streaming) response.entity();
            return entity.source();
        }

        if (response.entity() instanceof HttpEntity.Strict) {
            HttpEntity.Strict entity = (HttpEntity.Strict) response.entity();
            return Source.single(entity.bytes());
        }

        return Source.empty();
    }

    /**
     * Constructs a {@link Source} of {@link ByteString} for the argument 'protocol' and 'response' that emits the
     * the full bytes of the argument 'response'.  It merges the source for the status line and headers with the source
     * from the response entity.
     *
     * @param protocol the current http protocol
     * @param response the response to be sent to the client
     * @return a {@link Source} of {@link ByteString} for the argument 'protocol' and 'response' that emits the
     * full bytes of the argument 'response'.
     */
    private Source<ByteString, ?> response(HttpProtocol protocol, HttpResponse response) {
        return Source.combine(
                responseMeta(protocol, response),
                responseEntity(response),
                Collections.emptyList(),
                i -> Concat.create(ByteString.class));
    }

    /**
     * Applies the argument 'requestHandler' to the argument 'request' producing an {@link HttpResponse}.  If the
     * handler application fails, a default error response (500 Internal Server Error) will be provided instead.
     *
     * @param requestHandler the handler to apply
     * @param request the argument to the 'requestHandler'
     * @return a ({@link HttpResponse}, {@link HttpRequest} pair)
     */
    private CompletionStage<Pair<HttpResponse, HttpRequest>> applyHandler(RequestHandler requestHandler, HttpRequest request) {
        return requestHandler.apply(request)
                .exceptionally(th -> ERROR_RESPONSE)
                .thenApply(resp -> Pair.create(resp, request));
    }

    /**
     * Default error response to return in cases where handler application fails.
     */
    private static HttpResponse ERROR_RESPONSE = HttpResponse.of(StatusCode.INTERNAL_SERVER_ERROR);

    /**
     * Constructs a new {@link ActorMaterializer} instance based on the {@link AbstractVHostHandler#vHostConfig}.
     */
    private ActorMaterializer materializer() {
        ActorMaterializerSettings settings = ActorMaterializerSettings.create(system)
                .withDispatcher(vHostConfig.dispatcher());
        return ActorMaterializer.create(settings, system);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate() {
        materializer.shutdown();
    }
}
