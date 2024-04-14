package xyz.cofe.xsd.http.mount;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import xyz.cofe.xsd.http.HtmlBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record MatchedPath(String baseUrl, Path source, UnixPath unixPath, Path path) {
    public void send(Response response, Callback callback) {
        if (response == null) throw new IllegalArgumentException("response==null");
        if (Files.isRegularFile(path)) {
            sendFile(response, callback);
        } else if (Files.isDirectory(path)) {
            sendDir(response, callback);
        }
    }

    private void sendFile(Response response, Callback callback) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            response.setStatus(500);
            response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/plain");
            Content.Sink.write(response, true,
                "can't read " + path + "\n" + e,
                callback
            );
            return;
        }

        response.setStatus(200);
        FileExtension.of(path).map(Mime::of).ifPresent(mime -> mime.setContentType(response));
        response.getHeaders().put(HttpHeader.CONTENT_LENGTH, bytes.length);

        try {
            try (var out = Content.Sink.asOutputStream(response)) {
                out.write(bytes);
                out.flush();
            }
            callback.succeeded();
        } catch (IOException e) {
            System.err.println("can't write to socket " + e);
            throw new RuntimeException(e);
        }
    }

    private void sendDir(Response response, Callback callback) {
        if (!unixPath.isEndsWithSlash()) {
            response.setStatus(307);
            var target = baseUrl + unixPath + "/";
            response.getHeaders().put(HttpHeader.LOCATION, target);
            callback.succeeded();
            return;
        }

        List<Path> files;
        try {
            files = Files.list(path).toList();
        } catch (IOException e) {
            response.setStatus(500);
            response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/plain");
            Content.Sink.write(response, true,
                "can't read " + path + "\n" + e,
                callback
            );
            return;
        }

        StringBuilder buff = new StringBuilder();
        new HtmlBuilder(buff).tag("html").body(w -> {
            w.tag("head").body(w2 -> w2.tag("title").body("dir content " + path.getFileName().toString()));
            w.tag("body").body(body -> {
                files.forEach(file -> {
                    body.tag("div").body(div -> {
                        var filename = file.getFileName().toString();
                        var text = Files.isDirectory(file) ? filename + "/" : filename;
                        var href = Files.isDirectory(file) ? filename + "/" : filename;
                        div.tag("a").attribute("href", href)
                            .body(text);
                    });
                });
            });
        });

        response.setStatus(200);
        Mime.textHtml.setContentType(response);
        Content.Sink.write(response, true, buff.toString(), callback);
    }
}
