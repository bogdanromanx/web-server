package com.github.bogdanromanx.web.server.parsing;

import akka.NotUsed;
import akka.util.ByteString;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class EmptyLineParserTest {

    private static final EmptyLineParser parser = new EmptyLineParser();

    @Test
    public void validCRLFline() {
        ByteString input = ByteString.fromString("\r\nremaining");
        assertThat(parser.apply(input), equalTo(Result.of(NotUsed.getInstance(), ByteString.fromString("remaining"))));
    }

    @Test
    public void validLFLine() {
        ByteString input = ByteString.fromString("\nremaining");
        assertThat(parser.apply(input), equalTo(Result.of(NotUsed.getInstance(), ByteString.fromString("remaining"))));
    }

    @Test(expected = ParsingException.ExpectingEmptyLine.class)
    public void unmatched() {
        ByteString input = ByteString.fromString("other");
        parser.apply(input);
    }

    @Test
    public void emptyInput() {
        ByteString input = ByteString.empty();
        assertThat(parser.apply(input), equalTo(Result.of()));
    }
}
