package xyz.cofe.xsd.http.mount;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.nixpath.UnixPath;
import xyz.cofe.xsd.http.HtmlBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static xyz.cofe.im.struct.Result.err;
import static xyz.cofe.im.struct.Result.ok;

public record MatchedPath(String baseUrl, Path source, UnixPath unixPath, Path path, Encode encode) {
    public enum Encode {
        Html,
        Json
    }

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
            sendError(response,callback,500,"can't read " + path + "\n" + e);
            return;
        }

        var msg = switch (encode){
            case Html -> encodeHtml(ImList.from(files));
            case Json -> encodeJson(ImList.from(files));
        };

        msg.fold( succ -> {
            switch (encode){
                case Json -> Mime.applicationJson.setContentType(response);
                case Html -> Mime.textHtml.setContentType(response);
            }
            response.setStatus(200);
            Content.Sink.write(response, true, succ, callback);
            return null;
        }, err -> {
            sendError(response,callback,500,err);
            return null;
        });
    }

    private void sendError(Response response, Callback callback, int code, String message){
        switch (encode){
            case Html -> {
                response.setStatus(code);
                Mime.textPlain.setContentType(response);
                Content.Sink.write(response, true,
                    message,
                    callback
                );
            }
            case Json -> {
                response.setStatus(code);
                Mime.applicationJson.setContentType(response);
                Content.Sink.write(response, true,
                    message,
                    callback
                );
            }
        }
    }

    private Result<String, String> encodeHtml(ImList<Path> files){
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

        return ok(buff.toString());
    }

    private static final ObjectMapper om;
    static {
        om = new ObjectMapper();
        om.findAndRegisterModules();
    }

    private Result<String,String> encodeJson(ImList<Path> files){
        var lst = files.map(f -> Map.of(
            "name", (Object) f.getFileName().toString(),
            "dir", Files.isDirectory(f)
        )
        ).toList();

        try {
            return ok(om.writeValueAsString(lst));
        } catch (JsonProcessingException e) {
            return err(e.toString());
        }
    }
}
