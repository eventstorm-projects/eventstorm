package eu.eventsotrm.sql.apt.analyser;

import static eu.eventsotrm.sql.apt.Helper.isPrimitiveType;
import static eu.eventsotrm.sql.apt.Helper.propertyName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Version;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlInterfaceAnalyser implements Function<Element, PojoDescriptor> {

    private final Logger logger;

    public SqlInterfaceAnalyser() {
    	this.logger = LoggerFactory.getInstance().getLogger(SqlInterfaceAnalyser.class);
    }

    @Override
    public PojoDescriptor apply(Element element) {

        try {
            return doApply(element);
        } catch (AnalyserException cause) {
            this.logger.error(cause.getMessage(), cause);
            throw cause;
        } catch (Exception cause) {
            this.logger.error(cause.getMessage(), cause);
            throw new AnalyserException(cause);
        }

    }

    public PojoDescriptor doApply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
        	logger.error("element [" + element + "] should be an interface");
            return null;
        }

        logger.info("Analyse " + element);

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

   /* private void validatePrimaryKey(ExecutableElement executableElement, PrimaryKey primaryKey) {

        Class<?> identifier = Helper.extractPrimaryKeyGenerator(executableElement);

        if (SequenceGenerator.class.isAssignableFrom(identifier)) {
            // check if @Sequence exist.
            if (executableElement.getAnnotation(Sequence.class) == null) {
            	logger.error("PrimaryKey [" + primaryKey + "] in [" + executableElement.getEnclosingElement() + "]  type sequence -> missing annotation @Sequence");
            }
        }
    }*/

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

}