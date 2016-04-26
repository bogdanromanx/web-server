package com.github.bogdanromanx.web.server.types.headers;

import org.junit.Test;

import java.util.*;

import static com.github.bogdanromanx.web.server.types.headers.Connection.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ConnectionTest {

    private static final List<String> tokens = Arrays.asList(CLOSE, KEEP_ALIVE, UPGRADE);
    private static final String tokenString = String.format("%s,%s,%s", CLOSE, KEEP_ALIVE, UPGRADE);

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void factoryThrowsOnNullCollection() {
        List<String> tokens = null;
        Connection.of(tokens);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryThrowsOnEmptyCollection() {
        List<String> tokens = Arrays.asList("random", "other");
        Connection.of(tokens);
    }

    @Test
    public void factoryFiltersUnknownTokens() {
        List<String> many = new LinkedList<>(Arrays.asList("random", "other"));
        many.addAll(tokens);
        assertThat(Connection.of(many).value(), equalTo(tokens));
    }

    @Test
    public void headerEquality() {
        Set<Connection> set = new HashSet<>();
        set.add(Connection.of(tokens));
        assertThat(set, contains(Connection.of(tokens)));
    }

    @Test
    public void rawReturnsAWellFormedRawHeader() {
        Connection header = Connection.of(tokens);
        RawHeader expected = RawHeader.of(Connection.NAME, tokenString);
        assertThat(header.raw(), equalTo(expected));
    }

    @Test
    public void returnsTheName() {
        assertThat(Connection.of(tokens).name(), equalTo(Connection.NAME));
    }

    @Test
    public void returnsTheValue() {
        assertThat(Connection.of(tokens).value(), equalTo(tokens));
    }

    @Test
    public void lowerCasesTheName() {
        assertThat(Connection.of(tokens).lowerCaseName(), equalTo(Connection.LOWERCASE_NAME));
    }

    @Test
    public void stringRepresentation() {
        String expected = String.format("Connection{tokens=[%s]}", tokenString);
        assertThat(Connection.of(tokens).toString(), equalTo(expected));
    }

    @Test
    public void hasClose() {
        Connection connection = Connection.of(Collections.singleton(CLOSE));
        assertThat(connection.hasClose(), equalTo(true));
        assertThat(connection.hasKeepAlive(), equalTo(false));
        assertThat(connection.hasUpgrade(), equalTo(false));
    }

    @Test
    public void hasNotClose() {
        Connection connection = Connection.of(Arrays.asList(KEEP_ALIVE, UPGRADE));
        assertThat(connection.hasClose(), equalTo(false));
        assertThat(connection.hasKeepAlive(), equalTo(true));
        assertThat(connection.hasUpgrade(), equalTo(true));
    }

    @Test
    public void factoryReturnsEmptyForIllegalName() {
        RawHeader header = RawHeader.of(Connection.NAME + "a", tokenString);
        assertThat(Connection.of(header), equalTo(Optional.empty()));
    }

    @Test
    public void factoryReturnsEmptyForUnknownTokens() {
        RawHeader header = RawHeader.of(Connection.NAME, "random, other");
        assertThat(Connection.of(header), equalTo(Optional.empty()));
    }

    @Test
    public void factoryReturnsConnectionForKnownTokens() {
        RawHeader header = RawHeader.of(Connection.NAME, tokenString);
        assertThat(Connection.of(header), equalTo(Optional.of(Connection.of(tokens))));
    }
}
