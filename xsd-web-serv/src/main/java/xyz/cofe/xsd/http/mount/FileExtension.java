package xyz.cofe.xsd.http.mount;

import java.nio.file.Path;
import java.util.Optional;

public record FileExtension(String name) {
    public static Optional<FileExtension> of(Path path){
        if( path==null ) throw new IllegalArgumentException("path==null");
        var name = path.getFileName().toString();
        var dot = name.lastIndexOf(".");
        if( dot>=0 && dot<name.length()-1) return Optional.of(new FileExtension(name.substring(dot+1)));
        return Optional.empty();
    }
}
