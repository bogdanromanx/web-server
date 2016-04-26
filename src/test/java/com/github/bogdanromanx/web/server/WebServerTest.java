package com.github.bogdanromanx.web.server;

import com.github.bogdanromanx.web.server.types.headers.ContentType;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class WebServerTest {

    private static Config config = ConfigFactory.load();
    private static WebServer server;
    private static String uri = String.format("http://%s:%s", config.getString("host"), config.getInt("port"));

    @BeforeClass
    public static void beforeClass() {
        server = new WebServer(config);
        server.start();
    }

    @Test
    public void http10Ping() throws IOException {
        Response response = Request.Get(uri + "/ping")
                .version(HttpVersion.HTTP_1_0)
                .execute();
        Content content = response.returnContent();
        assertThat(content.asString(), equalTo("pong"));
        assertThat(content.getType().toString(), equalTo(ContentType.TEXT_PLAIN.value()));
    }

    @Test
    public void http11Ping() throws IOException {
        Response response = Request.Get(uri + "/ping")
                .version(HttpVersion.HTTP_1_1)
                .execute();
        Content content = response.returnContent();
        assertThat(content.asString(), equalTo("pong"));
        assertThat(content.getType().toString(), equalTo(ContentType.TEXT_PLAIN.value()));
    }

    @Test
    public void getResource() throws IOException {
        // TODO use temporary file support
        if (!isUnix()) {
            return;
        }
        String expected = "content";
        ensureContent(new File(config.getString("tmp") + "/testfile"), expected);
        String result = Request.Get(uri + "/testfile")
                .version(HttpVersion.HTTP_1_1)
                .execute()
                .returnContent()
                .asString();
        assertThat(result, equalTo(expected));
    }

    @AfterClass
    public static void afterClass() {
        server.terminate();
        server = null;
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    private static void ensureContent(File file, String content) throws IOException {
        if (file.exists() && file.isFile()) {
            if (!file.delete()) {
                throw new RuntimeException("Unable to remove existing file: " + file.getAbsolutePath());
            }
        }
        if (!file.createNewFile()) {
            throw new RuntimeException("Unable to create empty file: " + file.getAbsolutePath());
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static boolean isUnix() {
        String name = System.getProperty("os.name").toLowerCase();
        return name.contains("nix") || name.contains("mac");
    }
}
