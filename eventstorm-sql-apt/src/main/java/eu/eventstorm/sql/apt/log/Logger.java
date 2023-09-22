package eu.eventstorm.sql.apt.log;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger implements AutoCloseable{

    private static Logger main;

    private final Writer writer;

    public static Logger getLogger(ProcessingEnvironment env, String packageName, String name) {
        FileObject object;
        try {
            object = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, packageName, name);
            Logger logger = new Logger(object.openWriter());
            return logger;
        } catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }

    private Logger(Writer writer) {
        this.writer = writer;
    }

    public static void setMainLogger(Logger logger) {
        main = logger;
    }

    public static Logger getMainLogger() {
        return main;
    }

    public void info(String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())).append(" [" + Thread.currentThread().getName() + "]").append(" [INFO] ").append(msg).append('\n');
        try {
            this.writer.write(builder.toString());
            this.writer.flush();
        } catch (IOException cause) {
            throw new IllegalStateException(cause);
        }
    }

    public void error(String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append(LocalDateTime.now()).append(" [ERROR] ").append(msg).append('\n');
        try {
            this.writer.write(builder.toString());
            this.writer.flush();
        } catch (IOException cause) {
            throw new IllegalStateException(cause);
        }
        if (this != main) {
            main.error(msg);
        }
    }

    public void error(String msg, Throwable cause) {
        StringBuilder builder = new StringBuilder();
        builder.append(LocalDateTime.now()).append(" [ERROR] ").append(msg).append('\n');
        cause.printStackTrace(new PrintWriter(writer));
        try {
            this.writer.write(builder.toString());
            this.writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        if (this != main) {
            main.error(msg, cause);
        }
    }

    public void close() {
        if (this.writer != null) {
            try {
                this.writer.close();
            } catch (IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
    }
}
