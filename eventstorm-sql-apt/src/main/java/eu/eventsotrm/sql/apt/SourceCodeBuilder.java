package eu.eventsotrm.sql.apt;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.ViewDescriptor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SourceCodeBuilder {

	private final ProcessingEnvironment processingEnv;
	private List<PojoDescriptor> tableDescriptors;
	private List<PojoDescriptor> joinTableDescriptors;
	private List<ViewDescriptor> viewDescriptors;
	
    public SourceCodeBuilder(ProcessingEnvironment processingEnv) {
    	this.processingEnv = processingEnv;
	}

	public SourceCodeBuilder withTableDescriptors(List<PojoDescriptor> descriptors) {
		this.tableDescriptors = descriptors;
		return this;
	}

	public SourceCodeBuilder withJoinTableDescriptors(List<PojoDescriptor> joinTableDescriptors) {
		this.joinTableDescriptors = joinTableDescriptors;
		return this;
	}

	public SourceCodeBuilder withViewDescriptors(List<ViewDescriptor> viewDescriptors) {
		this.viewDescriptors = viewDescriptors;
		return this;
	}

	public SourceCode build() {
		if (tableDescriptors == null) {
			tableDescriptors = ImmutableList.of();
		}
		if (joinTableDescriptors == null) {
			joinTableDescriptors = ImmutableList.of();
		}
		if (viewDescriptors == null) {
			viewDescriptors = ImmutableList.of();
		}
		return new SourceCode(processingEnv, tableDescriptors, joinTableDescriptors, viewDescriptors);
	}
    
}