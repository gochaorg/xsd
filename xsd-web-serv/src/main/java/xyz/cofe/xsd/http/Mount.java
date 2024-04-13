package xyz.cofe.xsd.http;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Монтирование каталогов/файлов
 */
public class Mount {
    public Mount() {}

    public Mount(
        String source,
        String url,
        String pattern
    ) {
        this.source = source;
        this.url = url;
        this.pattern = pattern;
    }

    public static Handler mountHandler(List<Mount> mounts) {
        if (mounts == null) throw new IllegalArgumentException("mounts==null");

        List<MountUrlPredicate> predicates = new ArrayList<>();
        predicates.addAll(mounts.stream().map(Mount::toPredicate).toList());

        return new Handler.Abstract() {
            @Override
            public boolean handle(Request request, Response response, Callback callback) throws Exception {
                for (var pred : predicates) {
                    var matchedPathOpt = pred.test(request);
                    if (matchedPathOpt.isPresent()) {
                        var matched = matchedPathOpt.get();
                        matched.send(response, callback);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public MountUrlPredicate toPredicate() {

        return new MountUrlPredicate(
            Path.of(source),
            url == null ? "/" : url,
            pattern != null ? Pattern.compile(pattern) : Pattern.compile(".*")
        );
    }

    //region source : String
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    //endregion
    //region url : String
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //endregion
    //region pattern : String
    private String pattern;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    //endregion

    @Override
    public String toString() {
        return "Mount{" +
            "source='" + source + '\'' +
            ", url='" + url + '\'' +
            ", pattern='" + pattern + '\'' +
            '}';
    }

    public static class MountUrlPredicate {
        public final Path source;
        public final String baseUrl;
        public final Pattern pattern;

        public MountUrlPredicate(Path source, String baseUrl, Pattern pattern) {
            if (source == null) throw new IllegalArgumentException("source==null");
            if (baseUrl == null) throw new IllegalArgumentException("baseUrl==null");
            if (pattern == null) throw new IllegalArgumentException("pattern==null");
            this.source = source;
            this.baseUrl = baseUrl;
            this.pattern = pattern;
        }

        public Optional<MatchedPath> test(Request request) {
            if (request == null) throw new IllegalArgumentException("request==null");

            var nixPathOpt = new MountUrl(baseUrl).test(request.getHttpURI().getPath());
            if (nixPathOpt.isEmpty()) return Optional.empty();

            UnixPath nixPath = nixPathOpt.get();
            if (nixPath.isEscapeOfRoot()) return Optional.empty();
            if (!pattern.matcher(nixPath.toString()).matches()) return Optional.empty();

            var target = nixPath.isEmpty() ? source : nixPath.resolve(source);
            if (!Files.exists(target)) return Optional.empty();

            return Optional.of(new MatchedPath(baseUrl, source, nixPath, target));
        }
    }

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
            if( !unixPath.isEndsWithSlash() ){
                response.setStatus(307);
                var target = baseUrl + unixPath + "/";
                response.getHeaders().put(HttpHeader.LOCATION,target);
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
}
