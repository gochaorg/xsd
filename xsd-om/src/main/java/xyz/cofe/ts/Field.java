package xyz.cofe.ts;

import xyz.cofe.coll.im.Result;

public record Field(String name, Result<Type,String> type) {}
