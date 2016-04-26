package com.github.bogdanromanx.web.server.framing;

import akka.stream.Attributes;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;
import akka.util.ByteString;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * {@link GraphStage} implementation of a {@link FlowShape} that converts inbound {@link ByteString} instances into
 * well formed {@link RequestFrame} instances.  It uses a state machine to mutate the inbound bytes expectations based
 * on previously emitted {@link RequestFrame} instances.
 */
public class FramingStage extends GraphStage<FlowShape<ByteString, RequestFrame>> {

    private Inlet<ByteString> in = Inlet.create("FramingStage.in");
    private Outlet<RequestFrame> out = Outlet.create("FramingStage.out");
    private FlowShape<ByteString, RequestFrame> shape = FlowShape.of(in, out);

    private ParsingConfig parsingConfig;

    /**
     * Constructs a new {@link FramingStage} from the argument 'parsingConfig'.
     *
     * @param parsingConfig the request parsing configuration
     * @throws NullPointerException for null parsingConfig argument
     */
    public FramingStage(ParsingConfig parsingConfig) {
        this.parsingConfig = requireNonNull(parsingConfig);
    }

    @Override
    public FlowShape<ByteString, RequestFrame> shape() {
        return shape;
    }

    @Override
    public GraphStageLogic createLogic(Attributes inheritedAttributes) {
        return new GraphStageLogic(shape) {
            private FramingStageState state = new ExpectingRequestLine(parsingConfig, ByteString.empty());

            {
                setHandler(out, new AbstractOutHandler() {
                    @Override
                    public void onPull() throws Exception {
                        emitFrame();
                    }
                });

                setHandler(in, new AbstractInHandler() {
                    @Override
                    public void onPush() throws Exception {
                        state = state.next(grab(in));
                        emitFrame();
                    }

                    @Override
                    public void onUpstreamFinish() throws Exception {
                        if (!state.hasBytes()) completeStage();
                    }
                });
            }

            /**
             * Attempts to emit the frame produced by the current state and pull additional bytes.
             * @see FramingStageState
             */
            private void emitFrame() {
                Optional<RequestFrame> frame = state.frame();
                if (frame.isPresent()) {
                    push(out, frame.get());
                    state = state.next();
                } else if (!state.hasBytes() && isClosed(in)) {
                    completeStage();
                } else {
                    pull(in);
                }
            }
        };
    }
}
