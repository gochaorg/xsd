package xyz.cofe.xsd.http.mount;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
}
