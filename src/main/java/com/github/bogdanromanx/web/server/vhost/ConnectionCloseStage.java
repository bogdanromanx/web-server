package com.github.bogdanromanx.web.server.vhost;

import akka.japi.Pair;
import akka.stream.Attributes;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;
import com.github.bogdanromanx.web.server.types.HttpProtocol;
import com.github.bogdanromanx.web.server.types.HttpRequest;
import com.github.bogdanromanx.web.server.types.HttpResponse;
import com.github.bogdanromanx.web.server.types.headers.Connection;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@link GraphStage} implementation that determines whether a connection needs to be closed based on the protocol
 * defined in the incoming {@link HttpRequest}.  The connections is closed by completing the stage, thus completing the
 * entire processing flow.
 */
@SuppressWarnings("WeakerAccess")
public class ConnectionCloseStage extends GraphStage<FlowShape<Pair<HttpResponse, HttpRequest>, Pair<HttpResponse, HttpRequest>>> {

    private Inlet<Pair<HttpResponse, HttpRequest>> in = Inlet.create("ConnectionCloseStage.in");
    private Outlet<Pair<HttpResponse, HttpRequest>> out = Outlet.create("ConnectionCloseStage.out");
    private FlowShape<Pair<HttpResponse, HttpRequest>, Pair<HttpResponse, HttpRequest>> shape = FlowShape.of(in, out);

    @Override
    public FlowShape<Pair<HttpResponse, HttpRequest>, Pair<HttpResponse, HttpRequest>> shape() {
        return shape;
    }

    @Override
    public GraphStageLogic createLogic(Attributes inheritedAttributes) {
        return new GraphStageLogic(shape) {
            {
                setHandler(in, new AbstractInHandler() {
                    @Override
                    public void onPush() throws Exception {
                        Pair<HttpResponse, HttpRequest> pair = grab(in);
                        push(out, pair);
                        if (shouldClose(pair.second())) {
                            completeStage();
                        }
                    }
                });

                setHandler(out, new AbstractOutHandler() {
                    @Override
                    public void onPull() throws Exception {
                        pull(in);
                    }
                });
            }

            /**
             * Should close for HTTP/1.0 with no Connection: 'keep-alive', or HTTP/1.1 with Connection: 'close'.
             */
            boolean shouldClose(HttpRequest request) {
                Optional<Connection> connection = connection(request);
                if (HttpProtocol.HTTP_1_0.equals(request.requestLine().protocol())) {
                    return !connection.isPresent() || connection.isPresent() && !connection.get().hasKeepAlive();
                }
                if (HttpProtocol.HTTP_1_1.equals(request.requestLine().protocol())) {
                    return connection.isPresent() && connection.get().hasClose();
                }
                return true;
            }

            /**
             * Attempts to retrieve the {@link Connection} header off of the argument {@link HttpRequest}.
             * @return an {@link Optional} inhabited by a {@link Connection} header if the header is defined on the
             * argument 'request', uninhabited otherwise.
             */
            Optional<Connection> connection(HttpRequest request) {
                return request.headers().stream()
                        .flatMap(h -> Connection.of(h).map(Stream::of).orElse(Stream.empty()))
                        .findFirst();
            }
        };
    }
}
