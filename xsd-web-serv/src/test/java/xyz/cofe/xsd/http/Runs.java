package xyz.cofe.xsd.http;

import xyz.cofe.xsd.http.mount.Mount;

public class Runs {
    public static void main(String[] args){
        new JettyLaunch().start(
            new WebConf().configure( conf -> {
                conf.getBinds().add(new Bind.Http(8080,"127.0.0.1"));
                conf.setThreads(new WebConf.Threads(1,4,15000));
                conf.getMounts().add(new Mount("/home/user/code/xsd/xsd-om/src/test/resources/XMLSchemas", "/xsd", ".*"));
            })
        );
    }
}
