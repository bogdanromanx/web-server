package com.github.bogdanromanx.web.server.types;

import org.junit.Test;

import java.util.HashSet;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class StatusCodeTest {

    @Test(expected = NullPointerException.class)
    public void factoryThrowsOnNullName() {
        StatusCode.of(null, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnEmptyName() {
        StatusCode.of(" ", 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnLowValue() {
        StatusCode.of("Code", 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnHighValue() {
        StatusCode.of("Code", 1000);
    }

    @Test
    public void factoryConstructsCorrectly() {
        StatusCode code = StatusCode.of("Code", 100);
        assertThat(code.name(), equalTo("Code"));
        assertThat(code.value(), equalTo(100));
    }

    @Test
    public void correctRepresentation() {
        String expected = "StatusCode{name='Code', value=100}";
        assertThat(StatusCode.of("Code", 100).toString(), equalTo(expected));
    }

    @Test
    public void valueEquality() {
        StatusCode code = StatusCode.of("Code", 100);
        HashSet<StatusCode> set = new HashSet<>();
        set.add(code);
        assertThat(set, contains(StatusCode.of("Code", 100)));
    }
}
