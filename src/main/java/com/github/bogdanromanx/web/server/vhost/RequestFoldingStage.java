package com.github.bogdanromanx.web.server.vhost;

import akka.stream.Attributes;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.javadsl.Source;
import akka.stream.stage.*;
import akka.util.ByteString;
import com.github.bogdanromanx.web.server.framing.RequestFrame;
import com.github.bogdanromanx.web.server.types.HttpEntity;
import com.github.bogdanromanx.web.server.types.HttpRequest;

import java.lang.reflect.Constructor;

/**
 * {@link GraphStage} implementation that folds a series on {@link RequestFrame} instances into a single
 * {@link HttpRequest} instance.
 */
@SuppressWarnings("WeakerAccess")
public class RequestFoldingStage extends GraphStage<FlowShape<RequestFrame, HttpRequest>> {

    private Inlet<RequestFrame> in = Inlet.create("FoldingStage.in");
    private Outlet<HttpRequest> out = Outlet.create("FoldingStage.out");
    private FlowShape<RequestFrame, HttpRequest> shape = FlowShape.of(in, out);

    @Override
    public FlowShape<RequestFrame, HttpRequest> shape() {
        return shape;
    }

    @Override
    public GraphStageLogic createLogic(Attributes inheritedAttributes) {
        return new RequestFoldingLogic(shape);
    }

    private class RequestFoldingLogic extends GraphStageLogic {
        SubSourceOutlet<ByteString> entitySourceOutlet = null;
        HttpRequest.Builder builder = HttpRequest.builder().entity(HttpEntity.empty());

        private RequestFoldingLogic(FlowShape<RequestFrame, HttpRequest> shape) {
            super(shape);
            setDefaultHandlers();
        }

        InHandler inHandler = new AbstractInHandler() {
            @Override
            public void onPush() throws Exception {
                RequestFrame frame = grab(in);
                handleRequestLine(frame);
                handleHeader(frame);
                handleEntityStart(frame);
                handleEntityEnd(frame);
                if (!isClosed(in) && entitySourceOutlet == null) {
                    pull(in);
                }
            }
        };

        OutHandler outHandler = new AbstractOutHandler() {
            @Override
            public void onPull() throws Exception {
                pull(in);
            }
        };

        InHandler streamingEntityInHandler = new AbstractInHandler() {
            @Override
            public void onPush() throws Exception {
                RequestFrame frame = grab(in);
                handleEntityFrame(frame);
                handleEntityEnd(frame);
            }

            @Override
            public void onUpstreamFinish() {
                entitySourceOutlet.complete();
                completeStage();
            }

            public void onUpstreamFailure(Throwable th) {
                entitySourceOutlet.fail(th);
                failStage(th);
            }
        };

        OutHandler streamingEntityOutHandler = new AbstractOutHandler() {
            @Override
            public void onPull() throws Exception {
            }
        };

        private void setDefaultHandlers() {
            setHandler(in, inHandler);
            setHandler(out, outHandler);
        }

        private void handleRequestLine(RequestFrame frame) {
            if (frame instanceof RequestFrame.RequestLineFrame) {
                RequestFrame.RequestLineFrame requestLineFrame = (RequestFrame.RequestLineFrame) frame;
                requestLineFrame.requestLine().protocol();
                builder.requestLine(requestLineFrame.requestLine());
            }
        }

        private void handleHeader(RequestFrame frame) {
            if (frame instanceof RequestFrame.HeaderFrame) {
                builder.addHeader(((RequestFrame.HeaderFrame) frame).header());
            }
        }

        private void handleEntityStart(RequestFrame frame) {
            if (frame instanceof RequestFrame.EntityStart) {
                entitySourceOutlet = constructSubSourceOutlet();
                entitySourceOutlet.setHandler(outHandler);

                RequestFrame.EntityStart entityStart = (RequestFrame.EntityStart) frame;
                HttpEntity entity = HttpEntity.streaming(
                        Source.fromGraph(entitySourceOutlet.source()),
                        entityStart.length());
                builder.entity(entity);

                setHandler(in, streamingEntityInHandler);
                setHandler(out, streamingEntityOutHandler);

                push(out, builder.build());
            }
        }

        private void handleEntityFrame(RequestFrame frame) {
            if (frame instanceof RequestFrame.EntityFrame) {
                entitySourceOutlet.push(((RequestFrame.EntityFrame) frame).bytes());
            }
        }

        private void handleEntityEnd(RequestFrame frame) {
            if (frame instanceof RequestFrame.EntityEnd) {
                if (entitySourceOutlet != null) {
                    entitySourceOutlet.complete();
                    entitySourceOutlet = null;
                    setDefaultHandlers();
                } else {
                    push(out, builder.build());
                }
                completeStage();
            }
        }

        /**
         * The use of reflection is required due to the bytecode encoding of inner class written in Scala.  While the
         * constructor is available in Scala, it is not accessible from Java.
         *
         * @return a new {@link SubSourceOutlet}
         */
        // TODO implement an alternative to the private API SubSourceOutlet
        @SuppressWarnings("unchecked")
        private SubSourceOutlet<ByteString> constructSubSourceOutlet() {
            Constructor<?> constructor = SubSourceOutlet.class.getConstructors()[0];
            constructor.setAccessible(true);
            try {
                return (SubSourceOutlet<ByteString>) constructor.newInstance(this, "FoldingStage.entityOut");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
