package com.github.bogdanromanx.web.server.types;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class HttpMethodTest {

    @Test
    public void factoryYieldsStandard() {
        Arrays.stream(HttpMethod.Standard.values())
                .forEach(standard ->
                        assertThat(HttpMethod.of(standard.value()), equalTo(standard)));
    }

    @Test
    public void factoryYieldsACustom() {
        HttpMethod method = HttpMethod.of("NEW_METHOD");
        assertThat(method, instanceOf(HttpMethod.Custom.class));
        assertThat(method.value(), equalTo("NEW_METHOD"));
    }

    @Test
    public void factoryYieldsATrimmedCustom() {
        HttpMethod method = HttpMethod.of(" NEW_METHOD ");
        assertThat(method, instanceOf(HttpMethod.Custom.class));
        assertThat(method.value(), equalTo("NEW_METHOD"));
    }

    @Test(expected = NullPointerException.class)
    public void factoryThrowsOnNull() {
        HttpMethod.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnEmpty() {
        HttpMethod.of("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnWhitespaceOnly() {
        HttpMethod.of(" ");
    }

    @Test
    public void standardRepresentation() {
        Arrays.stream(HttpMethod.Standard.values())
                .forEach(standard ->
                        assertThat(standard.value(), equalTo(standard.name())));
    }

    @Test
    public void standardToString() {
        Arrays.stream(HttpMethod.Standard.values())
                .forEach(standard -> {
                            String expected = String.format("HttpMethod.Standard{value='%s'}", standard.value());
                            assertThat(standard.toString(), equalTo(expected));
                        }
                );
    }

    @Test
    public void customRepresentation() {
        assertThat(HttpMethod.of("CUSTOM").value(), equalTo("CUSTOM"));
    }

    @Test
    public void customToString() {
        String value = "CUSTOM";
        String expected = String.format("HttpMethod.Custom{value='%s'}", value);
        assertThat(HttpMethod.of(value).toString(), equalTo(expected));
    }

    @Test
    public void customEquality() {
        String value = "CUSTOM";
        Set<HttpMethod> set = new HashSet<>();
        set.add(HttpMethod.of(value));
        assertThat(set, contains(HttpMethod.of(value)));
    }
}
