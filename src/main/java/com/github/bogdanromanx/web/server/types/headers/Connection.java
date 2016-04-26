package com.github.bogdanromanx.web.server.types.headers;

import com.github.bogdanromanx.web.server.types.HttpHeader;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Http 'Connection' header type definition.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class Connection implements HttpHeader<List<String>> {

    /**
     * The constant name of the {@link Connection} header.
     */
    public static final String NAME = "Connection";

    /**
     * The constant lower cased name of the {@link Connection} header.
     */
    public static final String LOWERCASE_NAME = NAME.toLowerCase();

    /**
     * The constant 'close' token of the {@link Connection} header.
     */
    public static final String CLOSE = "close";

    /**
     * The constant 'keep-alive' token of the {@link Connection} header.
     */
    public static final String KEEP_ALIVE = "keep-alive";

    /**
     * The constant 'upgrade' token of the {@link Connection} header.
     */
    public static final String UPGRADE = "upgrade";

    private static Set<String> VALID_TOKENS = new HashSet<>();

    static {
        VALID_TOKENS.add(CLOSE);
        VALID_TOKENS.add(KEEP_ALIVE);
        VALID_TOKENS.add(UPGRADE);
    }

    private final List<String> tokens;
    private final RawHeader raw;

    /**
     * <p>
     * Constructs a new {@link Connection} header instance from the 'tokens' collection argument.  The unknown tokens
     * are filtered out from the constructed instance.
     * </p>
     * <p>
     * <strong>Note:</strong> the constructor creates a shallow copy of the collection,  changes in argument collection
     * will NOT be reflected in the {@link Connection} header instance.
     * </p>
     *
     * @param tokens the collection of 'tokens' of the {@link Connection} header
     * @throws NullPointerException     if the token collection is null
     * @throws IllegalArgumentException if the filtered token collection is empty
     */
    private Connection(Collection<String> tokens) {
        this.tokens = Collections.unmodifiableList(
                requireNonNull(tokens)
                        .stream()
                        .filter(t -> VALID_TOKENS.contains(t))
                        .collect(Collectors.toList()));
        if (this.tokens.isEmpty()) {
            throw new IllegalArgumentException("The Connection tokens must contain at least a valid token");
        }
        this.raw = RawHeader.of(NAME, this.tokens.stream().reduce((l, r) -> l + "," + r).orElse(""));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> value() {
        return tokens;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lowerCaseName() {
        return LOWERCASE_NAME;
    }

    /**
     * @return <code>true</code> if the header contains the 'close' token, <code>false</code> otherwise
     */
    public boolean hasClose() {
        return value().stream().anyMatch(v -> v.toLowerCase().equals(CLOSE));
    }

    /**
     * @return <code>true</code> if the header contains the 'keep-alive' token, <code>false</code> otherwise
     */
    public boolean hasKeepAlive() {
        return value().stream().anyMatch(v -> v.toLowerCase().equals(KEEP_ALIVE));
    }

    /**
     * @return <code>true</code> if the header contains the 'upgrade' token, <code>false</code> otherwise
     */
    public boolean hasUpgrade() {
        return value().stream().anyMatch(v -> v.toLowerCase().equals(UPGRADE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RawHeader raw() {
        return raw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return tokens.equals(that.tokens);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return tokens.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Connection{tokens=[" + raw.value() + "]}";
    }

    /**
     * <p>
     * Constructs a new {@link Connection} header instance from the 'tokens' collection argument.  The unknown tokens
     * are filtered out from the constructed instance.
     * </p>
     * <p>
     * <strong>Note:</strong> the constructor creates a shallow copy of the collection,  changes in argument collection
     * will NOT be reflected in the {@link Connection} header instance.
     * </p>
     *
     * @param tokens the collection of 'tokens' of the {@link Connection} header
     * @throws NullPointerException     if the token collection is null
     * @throws IllegalArgumentException if the filtered token collection is empty
     */
    public static Connection of(Collection<String> tokens) {
        return new Connection(tokens);
    }

    /**
     * Attempts to construct a new {@link Connection} instance from the 'raw' header argument by splitting the header
     * value into a list of tokens.
     *
     * @param raw the source {@link RawHeader}
     * @return an {@link Optional} {@link Connection}, inhabited if the lowercase name of the argument header equals
     * to the lowercase name constant of the {@link Connection} header and the value can be parsed into a non empty
     * list of valid tokens, uninhabited otherwise.
     */
    public static Optional<Connection> of(RawHeader raw) {
        if (raw.lowerCaseName().equals(LOWERCASE_NAME)) {
            List<String> tokens = Arrays.stream(raw.value().split(","))
                    .map(String::trim)
                    .filter(t -> VALID_TOKENS.contains(t))
                    .collect(Collectors.toList());
            try {
                return Optional.of(Connection.of(tokens));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
