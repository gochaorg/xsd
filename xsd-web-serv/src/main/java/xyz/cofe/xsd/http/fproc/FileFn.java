package xyz.cofe.xsd.http.fproc;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public sealed interface FileFn permits FileFn.FileStringFn {
    public byte[] apply(byte[] data);

    record FileStringFn(
        Supplier<Charset> readCharset,
        Supplier<Charset> writeCharset,
        Supplier<StringFn> stringFn
    ) implements FileFn {
        @Override
        public byte[] apply(byte[] data) {
            if (data == null) throw new IllegalArgumentException("data==null");
            return stringFn.get().apply(new String(data, readCharset.get())).getBytes(writeCharset.get());
        }
    }
}
