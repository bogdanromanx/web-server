package com.github.bogdanromanx.web.server.parsing;

import akka.util.ByteString;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class ResultTest {

    private static ByteString bytes = ByteString.fromString("string");
    private static ByteString noBytes = ByteString.empty();

    @Test
    public void emptyResultConstruction() {
        Result<Integer> result = Result.of();
        assertThat(result.value(), equalTo(Optional.empty()));
        assertThat(result.remaining(), equalTo(noBytes));
    }

    @Test
    public void remainingResultConstruction() {
        ByteString remaining = bytes;
        Result<Integer> result = Result.of(remaining);
        assertThat(result.value(), equalTo(Optional.empty()));
        assertThat(result.remaining(), equalTo(remaining));
    }

    @Test
    public void nullResultConstruction() {
        Result<Integer> result = Result.of(null, null);
        assertThat(result.value(), equalTo(Optional.empty()));
        assertThat(result.remaining(), equalTo(noBytes));
    }

    @Test
    public void mappingOverResultValue() {
        Result<Integer> result = Result.of(42, bytes);
        Result<String> mapped = result.map(Object::toString);
        assertThat(mapped.value(), equalTo(Optional.of("42")));
        assertThat(mapped.remaining(), equalTo(bytes));
    }

    @Test
    public void mappingOverMissingResultValue() {
        Result<Integer> result = Result.of(bytes);
        Result<String> mapped = result.map(Object::toString);
        assertThat(mapped.value(), equalTo(Optional.empty()));
        assertThat(mapped.remaining(), equalTo(bytes));
    }

    @Test
    public void flatMappingOverResultValue() {
        Result<Integer> result = Result.of(42, bytes);
        Result<String> mapped = result.flatMap(i -> Result.of(i.toString(), noBytes));
        assertThat(mapped.value(), equalTo(Optional.of("42")));
        assertThat(mapped.remaining(), equalTo(noBytes));
    }

    @Test
    public void flatMappingOverMissingResultValue() {
        Result<Integer> result = Result.of(bytes);
        Result<String> mapped = result.flatMap(i -> Result.of(i.toString(), noBytes));
        assertThat(mapped.value(), equalTo(Optional.empty()));
        assertThat(mapped.remaining(), equalTo(bytes));
    }

    @Test
    public void composingOverResultValue() {
        Result<Integer> result = Result.of(42, bytes);
        Result<String> next = result.andThen((i, b) -> Result.of(i.toString(), noBytes));
        assertThat(next.value(), equalTo(Optional.of("42")));
        assertThat(next.remaining(), equalTo(noBytes));
    }

    @Test
    public void composingOverMissingResultValue() {
        Result<Integer> result = Result.of(bytes);
        Result<String> next = result.andThen((i, b) -> Result.of(i.toString(), noBytes));
        assertThat(next.value(), equalTo(Optional.empty()));
        assertThat(next.remaining(), equalTo(bytes));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void equalityCheck() {
        Set<Result<Integer>> set = new HashSet<>();
        set.add(Result.of(42, bytes));
        assertThat(set, contains(Result.of(42, bytes)));
    }

    @Test
    public void inequalityCheckOnDifferentValues() {
        Result<Integer> first = Result.of(42, bytes);
        Result<Integer> second = Result.of(43, bytes);
        assertThat(first, not(equalTo(second)));
        assertThat(first.hashCode(), not(equalTo(second.hashCode())));
    }

    @Test
    public void inequalityCheckOnDifferentBytes() {
        Result<Integer> first = Result.of(42, bytes);
        Result<Integer> second = Result.of(42, noBytes);
        assertThat(first, not(equalTo(second)));
        assertThat(first.hashCode(), not(equalTo(second.hashCode())));
    }

    @Test
    public void stringRepresentation() {
        String expected = "Result{value=Optional[12], remainingLength=0}";
        assertThat(Result.of(12, ByteString.empty()).toString(), equalTo(expected));
    }
}
