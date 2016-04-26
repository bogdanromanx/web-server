package com.github.bogdanromanx.web.server.handling;

import com.github.bogdanromanx.web.server.types.HttpRequest;
import com.github.bogdanromanx.web.server.types.HttpResponse;
import com.github.bogdanromanx.web.server.types.StatusCode;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * {@link RequestHandler} implementation that joins multiple handlers together and attempts to match them to the
 * request in order.  The first handler that matches a request, it will be used for handling the request.  If none of
 * the handlers match the request, a default 404 Not Found response will be provided.
 */
public class CompositeHandler implements RequestHandler {

    private final List<RequestHandler> handlers;
    private final HttpResponse unmatched;

    private CompositeHandler(Collection<RequestHandler> handlers, HttpResponse unmatched) {
        this.handlers = new LinkedList<>(handlers);
        this.unmatched = unmatched;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(HttpRequest request) {
        return true;
    }

    /**
     * Attempts to match the inner handlers to the argument request.  The first handler that matches a request, it will
     * be used for handling the request.  If none of the handlers match the request, a default
     * {@link CompositeHandler#unmatched} response will be provided.
     *
     * @param request the request to apply this handler to
     * @return a future {@link HttpResponse}
     */
    @Override
    public CompletionStage<HttpResponse> apply(HttpRequest request) {
        return handlers.stream()
                .filter(handler -> handler.matches(request))
                .findFirst()
                .<Function<HttpRequest, CompletionStage<HttpResponse>>>map(Function.identity())
                .orElse(req -> CompletableFuture.completedFuture(unmatched))
                .apply(request);
    }

    /**
     * Constructs a new {@link CompositeHandler} from the argument 'handlers'.
     *
     * @param handlers the collections of inner handlers
     * @return a new {@link CompositeHandler} from the argument 'handlers'.
     * @see CompositeHandler
     */
    public static CompositeHandler of(Collection<RequestHandler> handlers) {
        return of(handlers, HttpResponse.of(StatusCode.NOT_FOUND));
    }

    /**
     * Constructs a new {@link CompositeHandler} from the 'handlers' and 'unmatched' arguments.
     *
     * @param handlers  the collections of inner handlers
     * @param unmatched the default {@link HttpResponse} to return if none of the inner handlers matches the 'request'
     * @return a new {@link CompositeHandler} from the argument 'handlers'.
     * @see CompositeHandler
     */
    public static CompositeHandler of(Collection<RequestHandler> handlers, HttpResponse unmatched) {
        return new CompositeHandler(handlers, unmatched);
    }
}
