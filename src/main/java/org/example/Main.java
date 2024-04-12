package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        var cmdline = new ArrayList<>(Arrays.asList(args));
        var state = "";
        while (!cmdline.isEmpty()){
            var arg = cmdline.remove(0);
            switch (state){
                case "" -> {
                    switch (arg){
                        case "-static" -> state = "-static";
                        case "-port" -> state = "-port";
                        default -> {
                            System.out.println("undefined key "+arg);
                        }
                    }
                }
                case "-static" -> {
                    state = "";
                    System.out.println("static files "+arg);
                    staticFiles.externalLocation(arg);
                }
                case "-port" -> {
                    state = "";
                    System.out.println("port "+arg);
                    port(Integer.parseInt(arg));
                }
            }
        }
        get("/", (req,res) -> "yy");
    }
}