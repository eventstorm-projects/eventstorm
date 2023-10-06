package eu.eventstorm.sql.apt;

import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes({"org.junit.jupiter.api.Test"})
public final class SqlProcessorTest extends AbstractProcessor {

    private boolean firstTime = true;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Start SqlProcessorTest");

        if (!firstTime) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "SqlProcessorTest done");
            return true;
        } else {
            firstTime = false;
        }

        try (Logger logger = Logger.getLogger(processingEnv, "log", "SqlProcessorTest")) {
            try {
                FileObject temp = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "junit", "reset.sql");
                Path reset = Paths.get(temp.toUri()).getParent().getParent().getParent().resolve("generated-sources").resolve("annotations").resolve("junit").resolve("reset.sql");
                if (Files.exists(reset)) {
                    copyReset(temp, reset);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

        }


        return true;
    }

    private void copyReset(FileObject to, Path from) throws IOException {
        String content = Files.readString(from);
        try (Writer writer = to.openWriter()) {
            writer.append(content);
        }
        Files.delete(from);
        Files.delete(from.getParent());
    }

}