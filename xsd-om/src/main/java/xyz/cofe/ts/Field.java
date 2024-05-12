package xyz.cofe.ts;

import xyz.cofe.im.struct.Result;

public record Field(String name, Result<Type,String> type) {}
