package xyz.cofe.xsd.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Конфигурация сервера
 */
public class WebConf {
    public WebConf configure(Consumer<WebConf> configurer){
        if( configurer==null ) throw new IllegalArgumentException("configurer==null");
        configurer.accept(this);
        return this;
    }

    //region threads
    public record Threads(
        int min,
        int max,
        int idleTimeoutSec
    ) {
        public QueuedThreadPool createThreadPool(){
            return new QueuedThreadPool(max, min, idleTimeoutSec * 1000);
        }
    }

    private Threads threads;

    public Threads getThreads() {
        return threads;
    }

    public void setThreads(Threads threads) {
        this.threads = threads;
    }
    //endregion

    //#region binds
    private List<Bind> binds = new ArrayList<>();

    public List<Bind> getBinds() {
        return binds;
    }

    public void setBinds(List<Bind> binds) {
        this.binds = binds;
    }
    //endregion

    //#region mounts
    private List<Mount> mounts = new ArrayList<>();

    public List<Mount> getMounts() {
        return mounts;
    }

    public void setMounts(List<Mount> mounts) {
        this.mounts = mounts;
    }
    //endregion

    public static WebConf parseJson(String json){
        if( json==null ) throw new IllegalArgumentException("json==null");

        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();

        try {
            return om.readValue(json, WebConf.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
