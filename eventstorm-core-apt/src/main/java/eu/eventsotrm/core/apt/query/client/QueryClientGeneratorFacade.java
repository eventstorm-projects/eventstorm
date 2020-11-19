package eu.eventsotrm.core.apt.query.client;

import javax.annotation.processing.ProcessingEnvironment;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.query.QueryBuilderGenerator;
import eu.eventsotrm.core.apt.query.QueryImplementationGenerator;
import eu.eventsotrm.core.apt.query.QueryJacksonStdDeserializerGenerator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class QueryClientGeneratorFacade {

	public static void generate(ProcessingEnvironment processingEnv, SourceCode sourceCode) {
		// Query 
		new QueryImplementationGenerator().generateClient(processingEnv, sourceCode);
		new QueryBuilderGenerator().generateClient(processingEnv, sourceCode);
		
		new QueryJacksonStdDeserializerGenerator().generate(processingEnv, sourceCode);
	}

}
