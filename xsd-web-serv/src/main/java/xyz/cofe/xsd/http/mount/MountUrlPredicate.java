package xyz.cofe.xsd.http.mount;

import org.eclipse.jetty.server.Request;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

public class MountUrlPredicate {
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
