package eu.eventstorm.problem;

import com.fasterxml.jackson.databind.module.SimpleModule;

@SuppressWarnings("serial")
public final class ProblemModule extends SimpleModule {

	public ProblemModule() {
        addSerializer(Problem.class, ProblemStdSerializer.INSTANCE);
        addDeserializer(Problem.class, ProblemStdDeserializer.INSTANCE);
	}
        
}