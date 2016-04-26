package com.github.bogdanromanx.web.server.handling;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.types.*;
import com.github.bogdanromanx.web.server.types.headers.ContentLength;
import com.github.bogdanromanx.web.server.types.headers.ContentType;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Simple {@link RequestHandler} implementation that matches against 'GET /ping *' requests and produces 200 OK,
 * text/plain, 'pong' responses.
 */
public class PingHandler implements RequestHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(HttpRequest request) {
        return request.requestLine().method().equals(HttpMethod.Standard.GET) &&
                request.requestLine().uri().toString().equals("/ping");
    }

    /**
     * Applies this handler to the argument {@link HttpRequest}.
     *
     * @param request the {@link HttpRequest} to apply this handler to
     * @return a future {@link HttpResponse}
     */
    @Override
    public CompletionStage<HttpResponse> apply(HttpRequest request) {
        ByteString body = ByteString.fromString("pong");
        return CompletableFuture.completedFuture(HttpResponse.of(
                StatusCode.OK,
                Arrays.asList(ContentLength.of(body.length()).raw(), ContentType.TEXT_PLAIN.raw()),
                HttpEntity.strict(body)
        ));
    }
}
