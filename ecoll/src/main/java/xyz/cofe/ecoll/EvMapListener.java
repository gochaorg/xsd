package xyz.cofe.ecoll;

public interface EvMapListener<K,V> {
    void evMapEvent(EvMapEvent<K,V> ev);
}
