package eu.eventstorm.sql.apt.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;

public final class Logger {

	private final String name;
	private final Writer writer;
	
	public Logger(Class<?> clazz, Writer writer) {
		this.name = clazz.getSimpleName();
		this.writer = writer;
	}

	public void info(String msg) {
		StringBuilder builder = new StringBuilder();
		builder.append(LocalDateTime.now().toString()).append("[" + Thread.currentThread().getName() + "]").append(" [INFO] [").append(this.name).append("] ").append(msg).append('\n');
		try {
			this.writer.write(builder.toString());
			this.writer.flush();
		} catch (IOException cause) {
			throw new IllegalStateException(cause);
		}
	}
	
	public void error(String msg) {
		StringBuilder builder = new StringBuilder();
		builder.append(LocalDateTime.now().toString()).append(" [ERROR] [").append(this.name).append("] ").append(msg).append('\n');
		try {
			this.writer.write(builder.toString());
			this.writer.flush();
		} catch (IOException cause) {
			throw new IllegalStateException(cause);
		}
	}

	public void error(String msg, Throwable cause) {
		StringBuilder builder = new StringBuilder();
		builder.append(LocalDateTime.now().toString()).append(" [").append(this.name).append("] ").append(msg).append('\n');
		cause.printStackTrace(new PrintWriter(writer));
		try {
			this.writer.write(builder.toString());
			this.writer.flush();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
