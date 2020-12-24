package eu.eventstorm.core.protobuf;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.TypeRegistry.Builder;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DescriptorModule {

	private final String name;
	private final ImmutableList<Descriptor> descriptors;
	
	public DescriptorModule(String name, ImmutableList<Descriptor> descriptors) {
		super();
		this.name = name;
		this.descriptors = descriptors;
	}

	public void appendTo(Builder builder) {
		descriptors.forEach(builder::add);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("name", name)
				.toString();
	}

}