package xyz.cofe;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static spark.Spark.*;

public class Main {
    public interface CliParse {
        public CliParse input(String arg);
        public default void eof() {};
    }

    public static class Init implements CliParse {
        @Override
        public CliParse input(String arg) {
            return switch (arg) {
                case "-port" -> new Port(this);
                case "-thread" -> new ThreadConf(this);
                case "-dir" -> new StaticFiles(this, "dir");
                default -> this;
            };
        }
    }
    public record Port(CliParse parent) implements CliParse {
        @Override
        public CliParse input(String arg) {
            port(Integer.parseInt(arg));
            return parent;
        }
    }
    public static class ThreadConf implements CliParse {
        private final CliParse parent;

        public ThreadConf(CliParse parent) {
            if( parent==null ) throw new IllegalArgumentException("parent==null");
            this.parent = parent;
        }

        private String state = "";
        private Integer min = null;
        private Integer max = null;
        private Integer timeoutSec = null;

        @Override
        public CliParse input(String arg) {
            return
                switch (state) {
                    case "min" -> {
                        state = "";
                        min = Integer.parseInt(arg);
                        yield this;
                    }
                    case "max" -> {
                        state = "";
                        max = Integer.parseInt(arg);
                        yield this;
                    }
                    case "timeout" -> {
                        state = "";
                        timeoutSec = Integer.parseInt(arg);
                        yield this;
                    }
                    default -> switch (arg) {
                        case "min", "max", "timeout" -> {
                            state = arg;
                            yield this;
                        }
                        default -> {
                            if( max!=null && min!=null && timeoutSec!=null ){
                                threadPool(max,min,timeoutSec*1000);
                            }else if( max!=null ){
                                threadPool(max);
                            }
                            yield parent.input(arg);
                        }
                    };
                };
        }
    }
    public static class StaticFiles implements CliParse {
        private final CliParse parent;

        public StaticFiles(CliParse parent, String state){
            this.parent = parent;
            this.state = state;
        }

        private Pattern pattern;
        private String baseUrl;
        private String dir;
        private String state = "dir";

        @Override
        public void eof() {
            if( pattern!=null && baseUrl!=null && dir!=null ){
                get(baseUrl, new StaticFile(Path.of(dir),pattern,baseUrl)::processing);
                pattern = null;
                baseUrl = null;
                dir = null;
            }else if( baseUrl!=null && dir!=null ){
                get(baseUrl, new StaticFile(Path.of(dir),baseUrl)::processing);
                pattern = null;
                baseUrl = null;
                dir = null;
            }
        }

        @Override
        public CliParse input(String arg) {
            return switch (state) {
                default -> switch (arg) {
                    case "regex" -> {
                        state = "regex";
                        yield this;
                    }
                    case "baseUrl", "url" -> {
                        state = "baseUrl";
                        yield this;
                    }
                    case "dir" -> {
                        state = "dir";
                        yield this;
                    }
                    default -> {
                        eof();
                        yield parent.input(arg);
                    }
                };
                case "dir" -> {
                    dir = arg;
                    state = "";
                    yield this;
                }
                case "regex" -> {
                    pattern = Pattern.compile(arg);
                    state = "";
                    yield this;
                }
                case "baseUrl" -> {
                    baseUrl = arg;
                    state = "";
                    yield this;
                }
            };
        }
    }

    public static void main(String[] args) {
        var cmdline = new ArrayList<>(Arrays.asList(args));
        CliParse parse = new Init();

        while (!cmdline.isEmpty()) {
            parse = parse.input(cmdline.remove(0));
        }
        parse.eof();

        post("/stop", (req, res) -> {
            stop();
            return "stopping";
        });
    }
}