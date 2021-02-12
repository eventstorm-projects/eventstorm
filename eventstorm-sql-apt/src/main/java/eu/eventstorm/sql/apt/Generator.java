package eu.eventstorm.sql.apt;

import javax.annotation.processing.ProcessingEnvironment;

public interface Generator {

    void generate(ProcessingEnvironment env, SourceCode sourceCode);

}