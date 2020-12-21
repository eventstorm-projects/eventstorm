package eu.eventstorm.core.descriptor;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.TypeRegistry.Builder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DescriptorModule {

	private final ImmutableList<Descriptor> descriptors;
	
	public DescriptorModule(ImmutableList<Descriptor> descriptors) {
		super();
		this.descriptors = descriptors;
	}

	public void appendTo(Builder builder) {
		descriptors.forEach(desc -> builder.add(desc));
	}

}