package eu.eventsotrm.sql.apt;

import javax.annotation.processing.ProcessingEnvironment;

import eu.eventsotrm.sql.apt.model.PojoDescriptor;

import java.util.List;
import java.util.Map;

public interface Generator {

    void generate(ProcessingEnvironment env, List<PojoDescriptor> descriptors, Map<String, Object> properties);

}