package xyz.cofe.ecoll;

public sealed interface EvMapEvent<K,V> {
    record Inserted<K,V>(K key, V item) implements EvMapEvent<K,V> {}
    record Updated<K,V>(K key, V item, V old) implements EvMapEvent<K,V> {}
    record Deleted<K,V>(K key, V old) implements EvMapEvent<K,V> {}
    record FullyChanged<K,V>() implements EvMapEvent<K,V> {}
}
