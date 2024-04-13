package xyz.cofe.xsd.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebConfTest {
    private String sampleConf =
        """
        {
          "threads" : {
            "min": 1,
            "max": 4,
            "idleTimeoutSec": 30
          },
          "binds": [{
            "port": 8080,
            "host": "0.0.0.0",
            "type": "http"
          }],
          "mounts" : [{
            "source": "/home/user/code/xsd/xsd-om/src/test/resources/XMLSchemas",
            "url": "/xsd",
            "pattern": "(?is).*(\\\\.xsd)$"
          }]
        }
        """;

    @Test
    public void read(){
        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();

        try {
            var conf = om.readValue(sampleConf, WebConf.class);
            System.out.println(conf);
            assertTrue(!conf.getMounts().isEmpty());

            System.out.println(conf.getMounts());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
