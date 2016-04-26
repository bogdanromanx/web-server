package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;

import java.util.Optional;

/**
 * Contract definition for state implementations of the {@link FramingStage}.  {@link FramingStageState} implementations
 * attempt to produce {@link RequestFrame} instances by applying parsers to the accumulated bytes immediately after
 * construction.  If there are no sufficient bytes available, a state can point to itself as the next possible state.
 */
interface FramingStageState {

    /**
     * @return the next {@link FramingStageState} with no additional bytes.
     */
    default FramingStageState next() {
        return next(ByteString.empty());
    }

    /**
     * Returns a reference to the next {@link FramingStageState} appending the argument 'bytes' to the currently
     * accumulated bytes.
     *
     * @param bytes the bytes to append to the currently accumulated bytes
     * @return a reference to the next {@link FramingStageState} appending the argument 'bytes' to the currently
     * accumulated bytes.
     */
    FramingStageState next(ByteString bytes);

    /**
     * @return true if <code>this</code> state has any unconsumed bytes accumulated, false otherwise
     */
    boolean hasBytes();

    /**
     * @return an {@link Optional} {@link RequestFrame} inhabited if the state managed to produce its expected
     * {@link RequestFrame} from its accumulated bytes, uninhabited otherwise.
     */
    Optional<RequestFrame> frame();
}
