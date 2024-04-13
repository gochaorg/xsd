package xyz.cofe.xsd.http;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

//region binds
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    @JsonSubTypes.Type(value = Bind.Http.class, name = "http")
)
public sealed interface Bind {
    void configure(Server server);

    record Http(int port, String host) implements Bind {
        @Override
        public void configure(Server server) {
            if( server==null ) throw new IllegalArgumentException("server==null");

            ServerConnector connector = new ServerConnector(server);
            server.addConnector(connector);

            connector.setPort(port);
            connector.setHost(host);

            System.out.println("configured http://"+host+":"+port+"/");
        }
    }
}
