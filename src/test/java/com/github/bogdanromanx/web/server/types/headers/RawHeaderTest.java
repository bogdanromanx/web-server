package com.github.bogdanromanx.web.server.types.headers;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class RawHeaderTest {

    private static final String name = " Name ";
    private static final String value = " Value ";
    private static final String ws = " ";

    @Test(expected = NullPointerException.class)
    public void factoryThrowsOnNullName() {
        RawHeader.of(null, value);
    }

    @Test(expected = NullPointerException.class)
    public void factoryThrowsOnNullValue() {
        RawHeader.of(name, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnWhitespaceName() {
        RawHeader.of(ws, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnWhitespaceValue() {
        RawHeader.of(name, ws);
    }

    @Test
    public void factoryTrimsNameAndValue() {
        RawHeader header = RawHeader.of(name, value);
        assertThat(header.name(), equalTo("Name"));
        assertThat(header.value(), equalTo("Value"));
    }

    @Test
    public void headerEquality() {
        Set<RawHeader> set = new HashSet<>();
        set.add(RawHeader.of(name, value));
        assertThat(set, contains(RawHeader.of(name, value)));
    }

    @Test
    public void rawReturnsThis() {
        RawHeader header = RawHeader.of(name, value);
        assertThat(header.raw(), sameInstance(header));
    }

    @Test
    public void lowerCasesTheTrimmedName() {
        assertThat(RawHeader.of(name, value).lowerCaseName(), equalTo(name.trim().toLowerCase()));
    }

    @Test
    public void stringRepresentation() {
        String expected = String.format("RawHeader{name='%s', value='%s'}", name.trim(), value.trim());
        assertThat(RawHeader.of(name, value).toString(), equalTo(expected));
    }
}
