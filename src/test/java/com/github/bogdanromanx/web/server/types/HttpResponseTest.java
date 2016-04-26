package com.github.bogdanromanx.web.server.types;

import com.github.bogdanromanx.web.server.types.headers.ContentLength;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class HttpResponseTest {

    private static StatusCode code = StatusCode.OK;
    private static RawHeader header1 = RawHeader.of("name1", "value1");
    private static RawHeader header2 = RawHeader.of("name2", "value2");
    private static List<RawHeader> headers = Arrays.asList(header1, header2);
    private static HttpEntity entity = HttpEntity.empty();

    @Test
    public void factoryConstructsCorrectly() {
        HttpResponse response = HttpResponse.of(code, headers, entity);
        assertThat(response.statusCode(), equalTo(code));
        assertThat(response.headers(), equalTo(headers));
        assertThat(response.entity(), equalTo(entity));
    }

    @Test
    public void factoryDefaultConstructsCorrectly() {
        HttpResponse response = HttpResponse.of();
        assertThat(response.statusCode(), equalTo(StatusCode.OK));
        assertThat(response.headers(), equalTo(Collections.singletonList(ContentLength.of(0).raw())));
        assertThat(response.entity(), equalTo(entity));
    }

    @Test
    public void factoryDefaultWithStatusConstructsCorrectly() {
        HttpResponse response = HttpResponse.of(StatusCode.INTERNAL_SERVER_ERROR);
        assertThat(response.statusCode(), equalTo(StatusCode.INTERNAL_SERVER_ERROR));
        assertThat(response.headers(), equalTo(Collections.singletonList(ContentLength.of(0).raw())));
        assertThat(response.entity(), equalTo(entity));
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullStatusCode() {
        HttpResponse.of(null, headers, entity);
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullHeaders() {
        HttpResponse.of(code, null, entity);
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullEntity() {
        HttpResponse.of(code, headers, null);
    }

    @Test
    public void valueEquality() {
        HttpResponse response = HttpResponse.of(code, headers, entity);
        HashSet<HttpResponse> set = new HashSet<>();
        set.add(response);
        assertThat(set, contains(HttpResponse.of(code, headers, entity)));
    }

    @Test
    public void correctRepresentation() {
        String expected = String.format("HttpResponse{statusCode=%s, headers=%s, entity=%s}", code, headers, entity);
        assertThat(HttpResponse.of(code, headers, entity).toString(), equalTo(expected));
    }
}
