package com.github.bogdanromanx.web.server.types;

import com.github.bogdanromanx.web.server.types.headers.RawHeader;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class HttpRequestTest {

    private static RequestLine requestLine = RequestLine.of(
            HttpMethod.Standard.GET,
            URI.create("/"),
            HttpProtocol.HTTP_1_0
    );
    private static RawHeader header1 = RawHeader.of("name1", "value1");
    private static RawHeader header2 = RawHeader.of("name2", "value2");
    private static RawHeader header3 = RawHeader.of("name3", "value3");
    private static RawHeader header4 = RawHeader.of("name4", "value4");
    private static List<RawHeader> headers = Collections.singletonList(header1);
    private static HttpEntity entity = HttpEntity.empty();

    @Test
    public void factoryConstructsCorrectly() {
        HttpRequest request = HttpRequest.of(requestLine, headers, entity);
        assertThat(request.requestLine(), equalTo(requestLine));
        assertThat(request.headers(), equalTo(Collections.singletonList(header1)));
        assertThat(request.entity(), equalTo(entity));
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullRequestLine() {
        HttpRequest.of(null, headers, entity);
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullHeaders() {
        HttpRequest.of(requestLine, null, entity);
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullEntity() {
        HttpRequest.of(requestLine, headers, null);
    }

    @Test
    public void builderConstructsCorrectly() {
        HttpRequest request = HttpRequest.builder()
                .requestLine(requestLine)
                .headers(headers)
                .addHeader(header2)
                .addHeaders(header3, header4)
                .entity(entity)
                .build();
        assertThat(request.requestLine(), equalTo(requestLine));
        assertThat(request.headers(), equalTo(Arrays.asList(header1, header2, header3, header4)));
        assertThat(request.entity(), equalTo(entity));
    }

    @Test
    public void builderResetsHeader() {
        HttpRequest request = HttpRequest.builder()
                .requestLine(requestLine)
                .addHeader(header4)
                .headers(headers)
                .entity(entity)
                .build();
        assertThat(request.headers().size(), equalTo(1));
        assertThat(request.headers().get(0), equalTo(header1));
    }

    @Test(expected = NullPointerException.class)
    public void builderThrowsOnUnsetRequestLine() {
        HttpRequest.builder().headers(headers).entity(entity).build();
    }

    @Test(expected = NullPointerException.class)
    public void builderThrowsOnUnsetEntity() {
        HttpRequest.builder().requestLine(requestLine).headers(headers).build();
    }

    @Test
    public void builderDefaultsToEmptyHeaders() {
        HttpRequest request = HttpRequest.builder().requestLine(requestLine).entity(entity).build();
        assertThat(request.headers().size(), equalTo(0));
    }

    @Test
    public void valueEquality() {
        HttpRequest request = HttpRequest.of(requestLine, headers, entity);
        HashSet<HttpRequest> set = new HashSet<>();
        set.add(request);
        assertThat(set, contains(HttpRequest.of(requestLine, headers, entity)));
    }

    @Test
    public void correctRepresentation() {
        String expected = String.format("HttpRequest{requestLine=%s, headers=%s, entity=%s}", requestLine, headers, entity);
        assertThat(HttpRequest.of(requestLine, headers, entity).toString(), equalTo(expected));
    }
}
