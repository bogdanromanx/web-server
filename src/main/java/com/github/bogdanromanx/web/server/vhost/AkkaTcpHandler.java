package com.github.bogdanromanx.web.server.vhost;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.util.ByteString;
import com.github.bogdanromanx.web.server.handling.CompositeHandler;
import com.github.bogdanromanx.web.server.handling.FileHandler;
import com.github.bogdanromanx.web.server.handling.PingHandler;
import com.github.bogdanromanx.web.server.handling.RequestHandler;
import com.github.bogdanromanx.web.server.settings.VHostConfig;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

/**
 * {@link VHostHandler} implementation that uses Akka's IO sub system for handling incoming connections.
 * @see AbstractVHostHandler
 */
public class AkkaTcpHandler extends AbstractVHostHandler {

    public AkkaTcpHandler(ActorSystem system, VHostConfig vHostConfig) {
        super(system, vHostConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        Source<Tcp.IncomingConnection, CompletionStage<Tcp.ServerBinding>> connections =
                Tcp.get(system).bind(vHostConfig.host(), vHostConfig.port());

        RequestHandler handler = CompositeHandler.of(Arrays.asList(
                new PingHandler(),
                new FileHandler(vHostConfig.path())
        ));

        Flow<ByteString, ByteString, NotUsed> flow = processingFlow(handler);

        connections.runForeach(c -> c.handleWith(flow, materializer), materializer);
    }
}
