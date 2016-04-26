package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.types.HttpMethod;
import com.github.bogdanromanx.web.server.types.HttpProtocol;
import com.github.bogdanromanx.web.server.types.RequestLine;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;
import org.junit.Test;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;


public class RequestFrameTest {

    @Test
    public void requestLineFactoryConstructsCorrectly() {
        RequestLine requestLine = RequestLine.of(HttpMethod.of("GET"), URI.create("/"), HttpProtocol.HTTP_1_0);
        RequestFrame.RequestLineFrame requestLineFrame = RequestFrame.requestLine(requestLine);
        assertThat(requestLineFrame.requestLine(), equalTo(requestLine));
    }

    @Test(expected = NullPointerException.class)
    public void requestLineFactoryThrowsOnNull() {
        RequestFrame.requestLine(null);
    }

    @Test
    public void requestLineEquality() {
        RequestLine requestLine = RequestLine.of(HttpMethod.of("GET"), URI.create("/"), HttpProtocol.HTTP_1_0);
        Set<RequestFrame> set = new HashSet<>();
        set.add(RequestFrame.requestLine(requestLine));
        assertThat(set, contains(RequestFrame.requestLine(requestLine)));
    }

    @Test
    public void requestLineStringRepresentation() {
        RequestLine requestLine = RequestLine.of(HttpMethod.of("GET"), URI.create("/"), HttpProtocol.HTTP_1_0);
        RequestFrame.RequestLineFrame requestLineFrame = RequestFrame.requestLine(requestLine);
        String expected = String.format("RequestLineFrame{requestLine=%s}", requestLine);
        assertThat(requestLineFrame.toString(), equalTo(expected));
    }

    @Test
    public void headerFactoryConstructsCorrectly() {
        RawHeader header = RawHeader.of("name", "value");
        RequestFrame.HeaderFrame headerFrame = RequestFrame.header(header);
        assertThat(headerFrame.header(), equalTo(header));
    }

    @Test(expected = NullPointerException.class)
    public void headerFactoryThrowsOnNull() {
        RequestFrame.header(null);
    }

    @Test
    public void headerEquality() {
        RawHeader header = RawHeader.of("name", "value");
        Set<RequestFrame> set = new HashSet<>();
        set.add(RequestFrame.header(header));
        assertThat(set, contains(RequestFrame.header(header)));
    }

    @Test
    public void headerStringRepresentation() {
        RawHeader header = RawHeader.of("name", "value");
        RequestFrame.HeaderFrame headerFrame = RequestFrame.header(header);
        String expected = String.format("HeaderFrame{header=%s}", header);
        assertThat(headerFrame.toString(), equalTo(expected));
    }

    @Test
    public void entityStartFactoryConstructsCorrectly() {
        assertThat(RequestFrame.start(1).length(), equalTo(1L));
    }

    @Test
    public void entityStartEquality() {
        Set<RequestFrame> set = new HashSet<>();
        set.add(RequestFrame.start(1));
        assertThat(set, contains(RequestFrame.start(1)));
    }

    @Test
    public void entityStartStringRepresentation() {
        assertThat(RequestFrame.start(1).toString(), equalTo("EntityStart{length=1}"));
    }

    @Test
    public void entityFactoryConstructsCorrectly() {
        ByteString bytes = ByteString.fromString("a");
        assertThat(RequestFrame.bytes(bytes).bytes(), equalTo(bytes));
    }

    @Test
    public void entityFactoryConstructsCorrectlyOnNull() {
        assertThat(RequestFrame.bytes(null).bytes(), equalTo(ByteString.empty()));
    }

    @Test
    public void entityEquality() {
        ByteString bytes = ByteString.fromString("a");
        Set<RequestFrame> set = new HashSet<>();
        set.add(RequestFrame.bytes(bytes));
        assertThat(set, contains(RequestFrame.bytes(bytes)));
    }

    @Test
    public void entityStringRepresentation() {
        String expected = "EntityFrame{bytesLength=1}";
        assertThat(RequestFrame.bytes(ByteString.fromString("a")).toString(), equalTo(expected));
    }

    @Test
    public void entityEndEquality() {
        Set<RequestFrame> set = new HashSet<>();
        set.add(RequestFrame.end());
        assertThat(set, contains(RequestFrame.end()));
    }

    @Test
    public void entityEndStringRepresentation() {
        String expected = "EntityEnd{}";
        assertThat(RequestFrame.end().toString(), equalTo(expected));
    }
}
