package xyz.cofe.xsd.http.mount;

import java.util.Optional;

public record MountUrl(String baseUrl) {
    public Optional<UnixPath> test(String uriPath) {
        if (uriPath == null)
            return Optional.empty();

        if (!uriPath.startsWith(baseUrl))
            return Optional.empty();

        var subUriPath = uriPath.substring(baseUrl.length());
        if (subUriPath.isEmpty()) return Optional.of(UnixPath.empty);
        if (!subUriPath.startsWith("/")) return Optional.empty();

        return UnixPath.parse(subUriPath, true);
    }
}
