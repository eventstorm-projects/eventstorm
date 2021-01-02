package eu.eventstorm.sql.csv;

import java.nio.Buffer;
import java.nio.MappedByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.util.Buffers;
import eu.eventstorm.util.unsafe.UnsafeHelper;
import sun.misc.Unsafe;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("restriction")
final class CsvReaderImpl implements CsvReader {

    /**
     * Unsafe reference;
     */
    private static final Unsafe UNSAFE = UnsafeHelper.getUnsafe();

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvReaderImpl.class);

    private static final byte LF = '\n';
    private static final byte CR = '\r';
    private static final byte QUOTE = '\"';
    private static final byte COMMA = ',';

    private final MappedByteBuffer buffer;
    private final long addressMax;
    private final FileEndOfLine fileEndOfLine;

    private long offset;
    private int line = 0;

    CsvReaderImpl(MappedByteBuffer buffer) {
        this.buffer = buffer;
        long address = UNSAFE.getLong(buffer, UnsafeHelper.getFieldOffset(Buffer.class, "address"));
        this.addressMax = address + buffer.limit();
        this.offset = address;

        ByteOrderMark bom = ByteOrderMark.read(buffer);
        if (bom != null) {
            buffer.position(bom.length());
            this.offset += bom.length();
        }
        fileEndOfLine = findEndOfLine();

        LOGGER.debug("find file eol [{}]", fileEndOfLine);
    }

    private FileEndOfLine findEndOfLine() {
        long off = this.offset;
        for (; off < addressMax; off++) {
            byte b = UNSAFE.getByte(off);

            if (b == CR) {
                if (off + 1 < addressMax) {
                    b = UNSAFE.getByte(off + 1);
                    if (b == LF) {
                        return FileEndOfLine.WIN;
                    } else {
                        return FileEndOfLine.MAC;
                    }
                } else {
                    return FileEndOfLine.MAC;
                }
            }
            if (b == LF) {
                return FileEndOfLine.UNIX;
            }
        }
        return FileEndOfLine.NONE;
    }

    public CsvLine line() {

        long[] cols = new long[64];
        boolean quoted = false;
        long off = this.offset;
        int i = 0;
        for (; off < addressMax; off++) {
            byte b = UNSAFE.getByte(off);

            if (QUOTE == b) {
                quoted = !quoted;
            }

            if (COMMA == b && !quoted) {
                cols[i++] = off - 1;
            }

            if (CR == b) {
                if (FileEndOfLine.MAC == this.fileEndOfLine) {
                    cols[i++] = off - 1;
                    CsvLine csvLine = new CsvLineImpl(this.offset, cols, line++, i);
                    this.offset = off + 1;
                    return csvLine;
                } else if (FileEndOfLine.WIN == this.fileEndOfLine) {
                    cols[i++] = off - 1;
                    CsvLine csvLine = new CsvLineImpl(this.offset, cols, line++, i);
                    this.offset = off + 2;
                    return csvLine;
                }
            }

            if (LF == b) {
                if (FileEndOfLine.UNIX == this.fileEndOfLine) {
                    cols[i++] = off - 1;
                    CsvLine csvLine = new CsvLineImpl(this.offset, cols, line++, i);
                    this.offset = off + 1;
                    return csvLine;
                } else {
                    throw new IllegalStateException();
                }
            }


        }

        return null;
    }

    @Override
    public void close() {
        Buffers.releaseDirectByteBuffer(buffer);
    }


}
