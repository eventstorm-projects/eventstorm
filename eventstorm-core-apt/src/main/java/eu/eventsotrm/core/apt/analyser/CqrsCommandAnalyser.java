package eu.eventsotrm.core.apt.analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandPropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsCommandAnalyser implements Function<Element, CommandDescriptor> {

    private final Logger logger;

    public CqrsCommandAnalyser() {
    	this.logger = LoggerFactory.getInstance().getLogger(CqrsCommandAnalyser.class);
    }

    @Override
    public CommandDescriptor apply(Element element) {

        try {
            return doApply(element);
        } catch (Exception cause) {
            this.logger.error(cause.getMessage(), cause);
          //  throw new AnalyserException(cause);
            return null;
        }

    }

    public CommandDescriptor doApply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
        	logger.error("element [" + element + "] should be an interface");
            return null;
        }

        logger.info("Analyse " + element);
        
        List<ExecutableElement> setters = new ArrayList<>();
        List<CommandPropertyDescriptor> properties = new ArrayList<>();
        
        for (Element method : element.getEnclosedElements()) {
        	
        	 if (ElementKind.METHOD != method.getKind()) {
             	 logger.error( "element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                 return null;
             }

             ExecutableElement executableElement = (ExecutableElement) method;
             
             if (executableElement.getSimpleName().toString().startsWith("set")) {

                 if (executableElement.getParameters().size() == 0) {
                 	logger.error( "setter [" + executableElement + "] in [" + element + "] has 0 parameter !");
                 }

                 if (executableElement.getParameters().size() > 1) {
                 	logger.error( "setter [" + executableElement + "] in [" + element + "] has more than 1 parameter !");
                 }

                 setters.add(executableElement);
                 continue;
             }
             
             
             if (executableElement.getSimpleName().toString().startsWith("get")) {
            	 properties.add(new CommandPropertyDescriptor(executableElement));
             }
        }
        
        setters.forEach(s -> {
            String property = Helper.propertyName(s.getSimpleName().subSequence(3, s.getSimpleName().length()).toString());
            Optional<CommandPropertyDescriptor> cpd = properties.stream().filter(p -> p.name().equals(property)).findFirst();
            if (!cpd.isPresent()) {
                logger.error("setter [" + s + "] in [" + element + "] is not link to a getter");
            } else {
                checkGetterAndSetterParamerts(cpd.get(), s);
                cpd.get().setSetter(s);
            }
        });

        return new CommandDescriptor(element, properties);
        /*
        List<PojoPropertyDescriptor> ppds = new ArrayList<>();
        List<PojoPropertyDescriptor> ids = new ArrayList<>();
        PojoPropertyDescriptor version = null;
        List<ExecutableElement> setters = new ArrayList<>();

        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.METHOD != method.getKind()) {
            	logger.error( "element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                return null;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            if (executableElement.getSimpleName().toString().startsWith("set")) {

                if (executableElement.getParameters().size() == 0) {
                	logger.error( "setter [" + executableElement + "] in [" + element + "] has 0 parameter !");
                }

                if (executableElement.getParameters().size() > 1) {
                	logger.error( "setter [" + executableElement + "] in [" + element + "] has more than 1 parameter !");
                }

                setters.add(executableElement);
                continue;
            }

            if (executableElement.getSimpleName().toString().startsWith("get")) {
                PrimaryKey primaryKey = executableElement.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    ids.add(new PojoPropertyDescriptor(executableElement));
                    continue;
                }

                Version versionAnnotation = executableElement.getAnnotation(Version.class);
                if (versionAnnotation != null) {
                    if (version == null) {
                        // only one version ...
                    } else {
                        validateVersion(executableElement, versionAnnotation);
                        version = new PojoPropertyDescriptor(executableElement);
                        continue;
                    }
                }


                Column column = executableElement.getAnnotation(Column.class);
                if (column == null) {
                	logger.error( "getter [" + method + "] in [" + element + "] should have @Column.");
                } else {

                    // check if column nullable.
                    if (column.nullable() && isPrimitiveType(executableElement.getReturnType().toString())) {
                    	logger.error( "getter [" + method + "] in [" + element + "] indicate a nullable type [" + executableElement.getReturnType() + "] -> it's not a nullable type.");
                    }


                    ppds.add(new PojoPropertyDescriptor(executableElement));
                }
                continue;
            }

            logger.error("method [" + method + "] in [" + element + "] should start with [set] or [get] prefix");
        }

        setters.forEach(s -> {
            String property = propertyName(s.getSimpleName().subSequence(3, s.getSimpleName().length()).toString());
            Optional<PojoPropertyDescriptor> ppd = ppds.stream().filter(p -> p.name().equals(property)).findFirst();
            if (!ppd.isPresent()) {
                Optional<PojoPropertyDescriptor> id = ids.stream().filter(p -> p.name().equals(property)).findFirst();
                if (!id.isPresent()) {
                	logger.error("setter [" + s + "] in [" + element + "] is not link to a getter");
                } else {
                    checkGetterAndSetterParamerts(id.get(), s);
                    id.get().setSetter(s);
                }
            } else {
                checkGetterAndSetterParamerts(ppd.get(), s);
                ppd.get().setSetter(s);
            }
        });

        return new PojoDescriptor(element, ids, version, ppds);
    }


    private void validateVersion(ExecutableElement executableElement, Version Version) {
        String type = executableElement.getReturnType().toString();
         if ("int".equals(type) || "short".equals(type) || "long".equals(type) || "byte".equals(type)) {
             // ok
         } else {
             throw new AnalyserException("@Version [" + executableElement + "] in [" + executableElement.getEnclosingElement() + "]  invalid type, support only [long,int, short, byte]");
         }
    }




    private void checkGetterAndSetterParamerts(PojoPropertyDescriptor ppd, ExecutableElement setter) {
        if (!setter.getParameters().get(0).asType().toString().equals(ppd.getter().getReturnType().toString())) {
        	logger.error("setter [" + setter + "] in [" + setter.getEnclosingElement() + "] type error : getter=[" + ppd.getter().getReturnType() + "] and setter=[" + setter.getParameters().get(0).asType().toString() + "]");
        }
    }
    
*/

       
    }
    
    private void checkGetterAndSetterParamerts(CommandPropertyDescriptor cpd, ExecutableElement setter) {
        if (!setter.getParameters().get(0).asType().toString().equals(cpd.getter().getReturnType().toString())) {
        	logger.error("setter [" + setter + "] in [" + setter.getEnclosingElement() + "] type error : getter=[" + cpd.getter().getReturnType() + "] and setter=[" + setter.getParameters().get(0).asType().toString() + "]");
        }
    }
}