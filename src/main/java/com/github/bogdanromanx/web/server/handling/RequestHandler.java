package com.github.bogdanromanx.web.server.handling;

import com.github.bogdanromanx.web.server.types.HttpRequest;
import com.github.bogdanromanx.web.server.types.HttpResponse;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Type definition for {@link HttpRequest} handlers.  It extends the {@link Function} contract with an additional
 * method that determines whether the handler can be applied to the argument {@link HttpRequest}.
 */
public interface RequestHandler extends Function<HttpRequest, CompletionStage<HttpResponse>> {

    /**
     * Determines whether <code>this</code> handler can be applied to the argument {@link HttpRequest}.
     *
     * @param request the request to test this handler against
     * @return true if the handler can be applied to the request, false otherwise
     */
    boolean matches(HttpRequest request);
}
