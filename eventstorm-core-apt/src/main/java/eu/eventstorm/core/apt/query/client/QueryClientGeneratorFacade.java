package eu.eventstorm.core.apt.query.client;

import javax.annotation.processing.ProcessingEnvironment;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.query.QueryBuilderGenerator;
import eu.eventstorm.core.apt.query.QueryClientServiceGenerator;
import eu.eventstorm.core.apt.query.QueryImplementationGenerator;
import eu.eventstorm.core.apt.query.QueryJacksonStdDeserializerGenerator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class QueryClientGeneratorFacade {

	public static void generate(ProcessingEnvironment processingEnv, SourceCode sourceCode) {
		// Query 
		new QueryImplementationGenerator().generateClient(processingEnv, sourceCode);
		new QueryBuilderGenerator().generateClient(processingEnv, sourceCode);
		
		new QueryJacksonStdDeserializerGenerator().generate(processingEnv, sourceCode);

		new QueryClientServiceGenerator().generateClient(processingEnv, sourceCode);

	}

}
