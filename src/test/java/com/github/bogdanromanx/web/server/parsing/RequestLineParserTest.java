package com.github.bogdanromanx.web.server.parsing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.types.HttpMethod;
import com.github.bogdanromanx.web.server.types.HttpProtocol;
import com.github.bogdanromanx.web.server.types.RequestLine;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class RequestLineParserTest {

    private static final RequestLineParser parser = new RequestLineParser(10, 10, 10);

    @Test
    public void validInputHttp10() throws URISyntaxException {
        ByteString bytes = ByteString.fromString("GET / HTTP/1.0\r\nheader:value\r\n");
        Result<RequestLine> result = parser.apply(bytes);

        Result<RequestLine> expected = Result.of(
                RequestLine.of(HttpMethod.Standard.GET, new URI("/"), HttpProtocol.HTTP_1_0),
                ByteString.fromString("header:value\r\n"));
        assertThat(result, equalTo(expected));
    }

    @Test
    public void validInputHttp11() throws URISyntaxException {
        ByteString bytes = ByteString.fromString("GET / HTTP/1.1\r\nheader:value\r\n");
        Result<RequestLine> result = parser.apply(bytes);

        Result<RequestLine> expected = Result.of(
                RequestLine.of(HttpMethod.Standard.GET, new URI("/"), HttpProtocol.HTTP_1_1),
                ByteString.fromString("header:value\r\n"));
        assertThat(result, equalTo(expected));
    }

    @Test
    public void validInputCustomMethod() throws URISyntaxException {
        ByteString bytes = ByteString.fromString("CUSTOM / HTTP/1.1\r\nheader:value\r\n");
        Result<RequestLine> result = parser.apply(bytes);

        Result<RequestLine> expected = Result.of(
                RequestLine.of(HttpMethod.of("CUSTOM"), new URI("/"), HttpProtocol.HTTP_1_1),
                ByteString.fromString("header:value\r\n"));
        assertThat(result, equalTo(expected));
    }

    @Test(expected = ParsingException.IllegalHttpMethod.class)
    public void illegalMethod() {
        ByteString bytes = ByteString.fromString(" ");
        parser.apply(bytes);
    }

    @Test(expected = ParsingException.IllegalFormat.class)
    public void longMethod() {
        ByteString bytes = ByteString.fromString("TOO_LONG_METHOD");
        parser.apply(bytes);
    }

    @Test(expected = ParsingException.IllegalURI.class)
    public void illegalURI() {
        ByteString bytes = ByteString.fromString("GET \\ ");
        parser.apply(bytes);
    }

    @Test(expected = ParsingException.IllegalFormat.class)
    public void longURI() {
        ByteString bytes = ByteString.fromString("GET /1234567890");
        parser.apply(bytes);
    }

    @Test(expected = ParsingException.IllegalHttpProtocol.class)
    public void illegalProtocol() {
        ByteString bytes = ByteString.fromString("GET / HTTP/1.2\r\n");
        parser.apply(bytes);
    }

    @Test(expected = ParsingException.IllegalFormat.class)
    public void longProtocol() {
        ByteString bytes = ByteString.fromString("GET / HTTP/1.2345\r\n");
        parser.apply(bytes);
    }
}
