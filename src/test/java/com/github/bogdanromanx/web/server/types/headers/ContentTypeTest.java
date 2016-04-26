package com.github.bogdanromanx.web.server.types.headers;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ContentTypeTest {

    private static final String value = " Value ";
    private static final String ws = " ";

    @Test(expected = NullPointerException.class)
    public void factoryThrowsOnNullValue() {
        ContentType.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnWhitespaceValue() {
        ContentType.of(ws);
    }

    @Test
    public void factoryTrimsValue() {
        ContentType header = ContentType.of(value);
        assertThat(header.value(), equalTo("Value"));
    }

    @Test
    public void headerEquality() {
        Set<ContentType> set = new HashSet<>();
        set.add(ContentType.of(value));
        assertThat(set, contains(ContentType.of(value)));
    }

    @Test
    public void rawReturnsAWellFormedRawHeader() {
        ContentType header = ContentType.of(value);
        assertThat(header.raw(), equalTo(RawHeader.of(ContentType.NAME, value)));
    }

    @Test
    public void returnsTheName() {
        assertThat(ContentType.of(value).name(), equalTo(ContentType.NAME));
    }

    @Test
    public void lowerCasesTheName() {
        assertThat(ContentType.of(value).lowerCaseName(), equalTo(ContentType.LOWERCASE_NAME));
    }

    @Test
    public void stringRepresentation() {
        String expected = String.format("ContentType{value='%s'}", value.trim());
        assertThat(ContentType.of(value).toString(), equalTo(expected));
    }
}
