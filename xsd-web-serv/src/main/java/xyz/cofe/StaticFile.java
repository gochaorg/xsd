package xyz.cofe;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import spark.Request;
import spark.Response;

public class StaticFile {
    private Path dir;
    private final Optional<Pattern> pattern;
    private String baseUrl;

    public StaticFile(Path dir, Pattern pattern, String baseUrl) {
        this.dir = dir;
        this.pattern = Optional.of(pattern);
        this.baseUrl = baseUrl;
    }

    public StaticFile(Path dir, String baseUrl) {
        this.dir = dir;
        this.baseUrl = baseUrl;
        this.pattern = Optional.empty();
    }

    public String processing(Request req, Response res) {
        var urlPathStr = req.pathInfo();
        if (urlPathStr == null) {
            res.status(400);
            return "urlPathStr is null";
        }

        if (pattern.isPresent() && !pattern.get().matcher(urlPathStr).matches()) {
            res.status(400);
            return "not matched";
        }


        var path1 = urlPathStr.replace("/", System.getProperties().getProperty("file.separator","/"))
            .replaceFirst("(?is)/(.*)","$1");

        Path urlPath = Path.of(path1);

        Path path = dir.resolve(urlPath);

        if (!Files.isRegularFile(path)) {
            res.status(404);
            res.body("file " + path + " not found");
            return "";
        }

        String name = path.getFileName().toString();
        int dot = name.indexOf(".");
        if (dot > 0 && dot < (name.length() - 1)) {
            var ext = name.substring(dot + 1).toLowerCase();
            mime(ext).ifPresent(res::type);
        }

        try {
            var bytes = Files.readAllBytes(path);
            res.raw().setContentLength(bytes.length);
            res.raw().getOutputStream().write(bytes);
            res.raw().getOutputStream().flush();
        } catch (IOException e) {
            res.status(500);
            res.type("text/plain");
            res.body(e.toString());
            throw new RuntimeException(e);
        }

        return "";
    }

    private Optional<String> mime(String ext) {
        return switch (ext) {
            case "txt" -> Optional.of("text/plain");
            case "html" -> Optional.of("text/html");
            case "js" -> Optional.of("text/javascript");
            case "css" -> Optional.of("text/css");
            case "jpg", "jpeg" -> Optional.of("image/jpeg");
            case "gif" -> Optional.of("image/gif");
            case "png" -> Optional.of("image/png");
            case "svg" -> Optional.of("image/svg+xml");
            case "xml" -> Optional.of("text/xml");
            case "xsl" -> Optional.of("text/xsl");
            default -> Optional.empty();
        };
    }
}
