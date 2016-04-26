# Web Server

(for lack of a better name)

The purpose of this project is to test out the Akka Streams Java API and how its implementation behaves wrt alternatives.
For more details, please have a look at the [Reactive Streams](http://www.reactive-streams.org/) initiative and of
course the [Akka Documentation](http://doc.akka.io/docs/akka/current/java.html) for its streaming API.

### Note

The implementation is not in any way complete or compliant with the HTTP standards, but feel free to use it as you see
fit.  Inspiration from: Akka HTTP & Grizzly.

### Supported Features

*   HttpRequest decoding (incl. entities) / HttpResponse encoding
*   Pipelining (comes for free when using a stream based design)
*   Static file handling
*   HTTP/1.0 Connection: keep-alive / Http/1.1 Connection: close

### ToDo List (ordered by urgency)

*   Additional Unit & Integration Tests
*   Proper logging
*   SSL / TLS support
*   Additional Source / Sink implementations: i.e.: JDK blocking / non-blocking IO, netty
*   Benchmarks
*   Additional transport handling
*   100 Continue
*   Multi-line header parsing
*   Caching hot files
*   Optimizations everywhere
*   WebSockets
*   HTTP/2.0
*   Compliance with Servlet
*   etc.

### Booting the Server

Currently the only way to start off the server is by running the `com.github.bogdanromanx.web.server.Bootstrap` class.
The server will bind for each defined `vhost` entry in the [config](src/main/resources/reference.conf).

The default configuration will bind the server on a single interface / port, and serve files stored under `/tmp`:
```
web.server {
    vhosts = [
        {
          host = "127.0.0.1"
          port = 8080
          path = "/tmp"
          dispatcher = "web.server.default-dispatcher"
        }
    ]
}
```

VHosts are to be similar to how virtual hosts are implemented in Apache's http with the distinction that they will be
able to configure every aspect of the server internals, i.e.: io layer, stages, req. handlers etc.

### Overall View

The implementation processes requests in several stages `inbound` -> `framing` -> `folding` ->
`handling` -> `writing` -> `outbound`.  The diagram attempt below describes how and what data flows through the system
for each individual incoming connection.

```
             +-------+  ByteString  +---------+  RequestFrame  +---------+  HttpRequest
     +------>|       |------------->| framing |--------------->| folding |--------------+
+--------+   |  IO   |              +---------+                +---------+              |
| client |   | layer |                                                                  |
+--------+   |       |    ByteString    +---------+  HttpResponse  +----------+         |
     ^-------|       |<-----------------| writing |<---------------| handling |<--------+
             +-------+                  +---------+                +----------+
```

#### Inbound

Reads `ByteString` chunks from the inbound connection.  The reason for using `ByteString` instead of the JDK's
`ByteBuffer` is twofold: Akka's TCP layer produces `ByteString` instances and these are supposedly more efficient due to
its avoidance of unnecessary array copying.

#### Framing

The stream of inbound bytes are parsed incrementally into `RequestFrames` and emitted downstream separately to be
folded into an `HttpRequest`.  The reason for that is offering the possibility of collapsing the stream and closing the
connection early, without the need of buffering data in memory.

Http entities, when present, are sent downstream in chunks as they are read from the socket.  The entity series of
frames is delimited by an `RequestFrame.EntityStart` and `RequestFrame.EntityEnd`.  If an `HttpRequest` does not present
an entity the framing stage will mark the end of the frames of an `HttpRequest` by emitting an `RequestFrame.EntityEnd`.

Parsers are just `Function<ByteString, Result<T>>` types, where `Result<T>` is a monadic data structure that allows a
simple composition of the parsing attempts.  It handles situations where there aren't enough bytes available to produce
the expected data structure, or there are remaining bytes after parsing.

#### Folding

This stage splits the incoming http request frames into individual sub-streams, delimited by `RequestFrame.EntityEnd`,
in order to properly fold each sub-stream into an `HttpRequest` instance.  The entity frames, when present, are
presented as a `Source<ByteString, ?>` field of the `HttpEntity.Streaming` data type.  The request handlers need to
verify the entity type and consume the source of `HttpEntity.Streaming` entities, otherwise it will create
back-pressure upstream (the bytes are not read from the socket).

There are three `HttpEntity` types available:
*   `HttpEntity.Empty` - for http requests and responses that have no entity
*   `HttpEntity.Strict` - for entities that are small enough to be loaded fully in memory
*   `HttpEntity.Streaming` - for entities of unbounded size to avoid buffering in memory

#### Handling

`RequestHandler`s are presented as functions that take `HttpRequest` instances and produce future `HttpResponse` results
(`CompletionStage<HttpResponse>`).  The `RequestHandler` adds one additional method to the `Function<T, R>` that is
used to check that the handler can be applied for a `HttpRequest` instance before its application:

```
public interface RequestHandler extends Function<HttpRequest, CompletionStage<HttpResponse>> {
    boolean matches(HttpRequest request);
}
```

Currently there are three available handlers:
*   `PingHandler` - responds to `GET /ping` requests with `200 OK pong`
*   `FileHandler` - produces an `HttpResponse` instance with a `HttpEntity.Streaming` entity that represent disk
    resources
*   `CompositeHandler` - takes a list of `RequestHandler`s and attempts to apply them in order until one matches, or
    produces a default `HttpResponse` for no matches

#### Writing

This stage transforms the `HttpResponse` instances produced by the previous stage into a stream of `ByteString`s and
sends them to `outbound` be written to the connection.  It also checks whether the connection needs to be closed in
which case it collapses the stream, forcing a connection close.

#### Outbound

Writes the received `ByteString` chunks to the client.

#### IO Layer

The IO layer is responsible for providing sources and sinks of `ByteString` for each inbound connection.  Currently in
use is Akka's TCP sub system.

This is the location where SSL/TLS termination would take place.

### Implementation Notes

#### Data Types

The data types do not follow the Java Bean convention as there's no immediate need to serialize these types.  Instead
types are defined to be immutable, and null safe; while not always efficient, it does provide some guarantees when
dealing with concurrency.

#### Efficiency

The implementation can be heavily optimized in all places (especially parsing) and additional boxing of the types
passed through the stream can reduce the number of stages.

#### Concurrency

At this stage of the implementation Akka's abstraction over the thread pools and execution is more than enough and can
be configured fine grained.  For the next IO stacks that will no longer be the case, but Akka's Streaming API allows
constructing Publishers and Subscribers from actors which can be isolated to their own dispatchers (i.e. different
thread pools).

### Benchmarks

While I haven't done any proper benchmarking, I did do a quick test to see some rough numbers (using the default
fork-join thread pool):
*   ping requests (no disk reads): ~12K req/sec
*   230KB file download: ~2400 req/sec
Specs: Intel Quad Core 2.5GHz, 16 GB RAM, SSD.

### Final Thoughts

Java 8 is a major leap forward, but it still lacks some incredibly powerful features (common in other languages) that
save a lot of development time, i.e.: pattern matching, tuples, case classes, proper type inference etc.