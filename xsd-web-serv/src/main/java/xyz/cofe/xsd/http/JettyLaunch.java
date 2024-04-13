package xyz.cofe.xsd.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JettyLaunch {
    public static void main(String[] args) {
        var launcher = new JettyLaunch();
        launcher.start(args);
    }

    public void start(String[] args) {
        parseCommandLine(args);
        try {
            server().start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start(WebConf conf){
        if( conf==null ) throw new IllegalArgumentException("conf==null");
        this.webConf = conf;
        try {
            server().start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void parseCommandLine(String[] args) {
        List<String> cmdline = new ArrayList<>(Arrays.asList(args));
        String state = "";
        while (!cmdline.isEmpty()){
            var arg = cmdline.remove(0);
            switch (state){
                case "conf" -> {
                    try {
                        webConf = WebConf.parseJson(Files.readString(Path.of(arg), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> {
                    switch (arg) {
                        case "-f" -> {
                            state = "conf";
                        }
                        default -> {
                            System.out.println("undefined arg "+arg);
                        }
                    }
                }
            }
        }
    }

    private WebConf webConf = new WebConf();

    //region threads
    private QueuedThreadPool createThreadPool() {
        return webConf.getThreads().createThreadPool();
    }

    private volatile QueuedThreadPool threadPool;

    private QueuedThreadPool threadPool() {
        if (threadPool != null) return threadPool;
        synchronized (this) {
            if (threadPool != null) return threadPool;
            threadPool = createThreadPool();
            return threadPool;
        }
    }
    //endregion

    //region server
    private volatile Server server;

    private Server server() {
        if (server != null) return server;
        synchronized (this) {
            if (server != null) return server;
            server = createServer(threadPool());
            return server;
        }
    }

    private Server createServer(QueuedThreadPool threadPool) {
        var server = new Server(threadPool);

        var mountHandler = Mount.mountHandler(webConf.getMounts());
        server.setHandler(mountHandler);

        webConf.getBinds().forEach( bind -> {
            bind.configure(server);
        });
        return server;
    }
    //endregion
}
