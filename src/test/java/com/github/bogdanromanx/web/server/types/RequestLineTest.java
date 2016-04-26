package com.github.bogdanromanx.web.server.types;

import org.junit.Test;

import java.net.URI;
import java.util.HashSet;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class RequestLineTest {

    private static HttpMethod method = HttpMethod.Standard.GET;
    private static URI uri = URI.create("/");
    private static HttpProtocol protocol = HttpProtocol.HTTP_1_0;

    @Test
    public void factoryConstructsCorrectly() {
        RequestLine requestLine = RequestLine.of(method, uri, protocol);
        assertThat(requestLine.method(), equalTo(method));
        assertThat(requestLine.uri(), equalTo(uri));
        assertThat(requestLine.protocol(), equalTo(protocol));
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullMethod() {
        RequestLine.of(null, uri, protocol);
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullURI() {
        RequestLine.of(method, null, protocol);
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullProtocol() {
        RequestLine.of(method, uri, null);
    }

    @Test
    public void builderConstructsCorrectly() {
        RequestLine requestLine = RequestLine.builder().method(method).uri(uri).protocol(protocol).build();
        assertThat(requestLine.method(), equalTo(method));
        assertThat(requestLine.uri(), equalTo(uri));
        assertThat(requestLine.protocol(), equalTo(protocol));
    }

    @Test(expected = NullPointerException.class)
    public void builderThrowsOnUnsetMethod() {
        RequestLine.builder().uri(uri).protocol(protocol).build();
    }

    @Test(expected = NullPointerException.class)
    public void builderThrowsOnUnsetURI() {
        RequestLine.builder().method(method).protocol(protocol).build();
    }

    @Test(expected = NullPointerException.class)
    public void builderThrowsOnUnsetProtocol() {
        RequestLine.builder().method(method).uri(uri).build();
    }

    @Test
    public void correctRepresentation() {
        String expected = String.format("RequestLine{method=%s, uri=%s, protocol=%s}", method, uri, protocol);
        assertThat(RequestLine.of(method, uri, protocol).toString(), equalTo(expected));
    }

    @Test
    public void valueEquality() {
        RequestLine requestLine = RequestLine.of(method, uri, protocol);
        HashSet<RequestLine> set = new HashSet<>();
        set.add(requestLine);
        assertThat(set, contains(RequestLine.of(method, uri, protocol)));
    }
}
