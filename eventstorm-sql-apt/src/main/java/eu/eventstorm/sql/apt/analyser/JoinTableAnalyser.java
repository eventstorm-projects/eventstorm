package eu.eventstorm.sql.apt.analyser;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.JoinColumn;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.model.PojoPropertyDescriptor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static eu.eventstorm.sql.apt.Helper.isPrimitiveType;
import static eu.eventstorm.sql.apt.Helper.propertyName;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class JoinTableAnalyser implements Function<Element, PojoDescriptor>, AutoCloseable {

    private final Logger logger;

    private final List<PojoDescriptor> descriptors;

    public JoinTableAnalyser(ProcessingEnvironment processingEnv, List<PojoDescriptor> descriptors) {
        this.logger = Logger.getLogger(processingEnv, "eu.eventstorm.sql.analyser", "JoinTableAnalyser");
        this.descriptors = descriptors;
    }

    @Override
    public PojoDescriptor apply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
            logger.error("element [" + element + "] should be an interface");
            return null;
        }

        logger.info("Analyse " + element);

        List<PojoPropertyDescriptor> ppds = new ArrayList<>();
        List<PojoPropertyDescriptor> ids = new ArrayList<>();
        List<ExecutableElement> setters = new ArrayList<>();

        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.METHOD != method.getKind()) {
                logger.error("element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                return null;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            if (executableElement.getSimpleName().toString().startsWith("set")) {

                if (executableElement.getParameters().size() == 0) {
                    logger.error("setter [" + executableElement + "] in [" + element + "] has 0 parameter !");
                }

                if (executableElement.getParameters().size() > 1) {
                    logger.error("setter [" + executableElement + "] in [" + element + "] has more than 1 parameter !");
                }

                setters.add(executableElement);
                continue;
            }

            if (executableElement.getSimpleName().toString().startsWith("get")) {

                if (executableElement.getAnnotation(PrimaryKey.class) != null) {
                    logger.error("@PrimaryKey should not used for @JoinTable (" + executableElement + ")");
                }

                JoinColumn joinColumn = executableElement.getAnnotation(JoinColumn.class);

                if (joinColumn != null) {

                    validateJoinColumn(executableElement, joinColumn);

                    ids.add(new PojoPropertyDescriptor(executableElement));
                    continue;
                }

                Column column = executableElement.getAnnotation(Column.class);
                if (column == null) {
                    logger.error("getter [" + method + "] in [" + element + "] should have @Column.");
                } else {

                    // check if column nullable.
                    if (column.nullable() && isPrimitiveType(executableElement.getReturnType().toString())) {
                        logger.error("getter [" + method + "] in [" + element + "] indicate a nullable type [" + executableElement.getReturnType() + "] -> it's not a nullable type.");
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

        return new PojoDescriptor(element, ids, null, ppds);
    }

    private void validateJoinColumn(ExecutableElement executableElement, JoinColumn joinColumn) {


    }


    private void checkGetterAndSetterParamerts(PojoPropertyDescriptor ppd, ExecutableElement setter) {
        if (!setter.getParameters().get(0).asType().toString().equals(ppd.getter().getReturnType().toString())) {
            logger.error("setter [" + setter + "] in [" + setter.getEnclosingElement() + "] type error : getter=[" + ppd.getter().getReturnType() + "] and setter=[" + setter.getParameters().get(0).asType().toString() + "]");
        }
    }

    @Override
    public void close() {
        logger.close();
    }
}