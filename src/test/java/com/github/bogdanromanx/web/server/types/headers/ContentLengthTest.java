package com.github.bogdanromanx.web.server.types.headers;

import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ContentLengthTest {

    private static final Long value = 1L;

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnNegativeValues() {
        ContentLength.of(-1);
    }

    @Test
    public void headerEquality() {
        Set<ContentLength> set = new HashSet<>();
        set.add(ContentLength.of(value));
        assertThat(set, contains(ContentLength.of(value)));
    }

    @Test
    public void rawReturnsAWellFormedRawHeader() {
        ContentLength header = ContentLength.of(value);
        assertThat(header.raw(), equalTo(RawHeader.of(ContentLength.NAME, Long.toString(value))));
    }

    @Test
    public void returnsTheName() {
        assertThat(ContentLength.of(value).name(), equalTo(ContentLength.NAME));
    }

    @Test
    public void returnsTheValue() {
        assertThat(ContentLength.of(value).value(), equalTo(value));
    }

    @Test
    public void lowerCasesTheName() {
        assertThat(ContentLength.of(value).lowerCaseName(), equalTo(ContentLength.LOWERCASE_NAME));
    }

    @Test
    public void stringRepresentation() {
        String expected = String.format("ContentLength{value=%s}", value);
        assertThat(ContentLength.of(value).toString(), equalTo(expected));
    }

    @Test
    public void factoryReturnsEmptyForIllegalName() {
        RawHeader header = RawHeader.of(ContentLength.NAME + "a", "12");
        assertThat(ContentLength.of(header), equalTo(Optional.empty()));
    }

    @Test
    public void factoryReturnsEmptyForNegativeValue() {
        RawHeader header = RawHeader.of(ContentLength.NAME, "-1");
        assertThat(ContentLength.of(header), equalTo(Optional.empty()));
    }

    @Test
    public void factoryReturnsEmptyForIllegalValueValue() {
        RawHeader header = RawHeader.of(ContentLength.NAME, "asd");
        assertThat(ContentLength.of(header), equalTo(Optional.empty()));
    }

    @Test
    public void factoryReturnsNonEmptyForCorrectValue() {
        RawHeader header = RawHeader.of(ContentLength.LOWERCASE_NAME, "1");
        assertThat(ContentLength.of(header), equalTo(Optional.of(ContentLength.of(1))));
    }
}
