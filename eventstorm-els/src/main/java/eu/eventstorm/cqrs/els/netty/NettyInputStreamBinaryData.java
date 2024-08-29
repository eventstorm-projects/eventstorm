package eu.eventstorm.cqrs.els.netty;

import co.elastic.clients.util.BinaryData;
import co.elastic.clients.util.NoCopyByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class NettyInputStreamBinaryData implements BinaryData {

    private final String contentType;
    private final InputStream inputStream;
    private boolean consumed = false;

    NettyInputStreamBinaryData(String contentType, InputStream inputStream) {
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        consume();
        try {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } finally {
            inputStream.close();
        }
    }

    @Override
    public ByteBuffer asByteBuffer() throws IOException {
        consume();
        NoCopyByteArrayOutputStream baos = new NoCopyByteArrayOutputStream();
        writeTo(baos);
        return baos.asByteBuffer();
    }

    @Override
    public InputStream asInputStream() throws IOException {
        consume();
        return inputStream;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public long size() {
        return -1;
    }

    private void consume() throws IllegalStateException {
        if (consumed) {
            throw new IllegalStateException("Data has already been consumed");
        }
        consumed = true;
    }

}
