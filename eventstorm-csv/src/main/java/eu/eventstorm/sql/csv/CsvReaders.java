package eu.eventstorm.sql.csv;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CsvReaders {

    private CsvReaders() {
    }

    public static CsvReader newReader(FileChannel fc) throws IOException {
        MappedByteBuffer map = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        return new CsvReaderImpl(map);
    }

}
