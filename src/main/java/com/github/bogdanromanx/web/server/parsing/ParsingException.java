package com.github.bogdanromanx.web.server.parsing;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ParsingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    private ParsingException(String message) {
        super(message);
    }

    private ParsingException(Throwable cause) {
        super(cause);
    }

    private ParsingException() {
        super();
    }

    public static final class IllegalHttpMethod extends ParsingException {
        private static final long serialVersionUID = 1L;

        public IllegalHttpMethod(Throwable cause) {
            super(cause);
        }
    }

    public static final class IllegalURI extends ParsingException {
        private static final long serialVersionUID = 1L;

        public IllegalURI(Throwable cause) {
            super(cause);
        }
    }

    public static final class IllegalHttpProtocol extends ParsingException {
        private static final long serialVersionUID = 1L;

        public IllegalHttpProtocol() {
            super();
        }
    }

    public static final class ExpectingEmptyLine extends ParsingException {
        private static final long serialVersionUID = 1L;

        public ExpectingEmptyLine() {
            super();
        }
    }

    public static final class IllegalFormat extends ParsingException {
        private static final long serialVersionUID = 1L;

        public IllegalFormat() {
            super();
        }
    }

    public static final class IllegalHttpHeader extends ParsingException {
        private static final long serialVersionUID = 1L;

        public IllegalHttpHeader(Throwable cause) {
            super(cause);
        }
    }
}
