package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.ForeignKey;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.apt.SourceCode;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.model.PojoPropertyDescriptor;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.Writer;

public class ItemForeignKey extends Item {

    private final PojoDescriptor descriptor;
    private final PojoPropertyDescriptor propertyDescriptor;
    private final ForeignKey foreignKey;
    private final SourceCode sourceCode;
    private final Logger logger;

    public ItemForeignKey(Logger logger, String version, PojoDescriptor descriptor, PojoPropertyDescriptor propertyDescriptor, ForeignKey foreignKey, SourceCode sourceCode) {
        super(version);
        this.logger = logger;
        this.descriptor = descriptor;
        this.propertyDescriptor = propertyDescriptor;
        this.foreignKey = foreignKey;
        this.sourceCode = sourceCode;
    }

    @Override
    void write(Writer writer, DatabaseDialect dialect) throws IOException {
        String name;
        Column anno = propertyDescriptor.getter().getAnnotation(Column.class);

        if (anno != null) {
            name = anno.value();
        } else {
            name = propertyDescriptor.getter().getAnnotation(PrimaryKey.class).value();
        }

        PojoDescriptor target = sourceCode.getPojoDescriptor(getClass(foreignKey).toString());
        PrimaryKey targetColumn;


        if (target == null) {
            logger.info("desc : " + propertyDescriptor + "--> target : " + target + "--> class fk :" + getClass(foreignKey) + "--> FK :" + foreignKey);
            return;
        }
        if (target.ids().size() != 1) {
            logger.error("desc : " + propertyDescriptor + "--> target : " + target + "--> class fk :" + getClass(foreignKey) + "--> FK :" + foreignKey);
            throw new UnsupportedOperationException();
        } else {
            targetColumn = target.ids().get(0).getter().getAnnotation(PrimaryKey.class);
        }

        writer.append( "ALTER TABLE " +  descriptor.getTable().value() +
                " ADD CONSTRAINT " + descriptor.getTable().value() + "__fk__" +  name +
                " FOREIGN KEY (" + name + ")" +
                " REFERENCES "+ target.getTable().value() + " (" + targetColumn.value() +");\n");
    }


    private static TypeMirror getClass(ForeignKey foreignKey) {
        try {
            foreignKey.target(); // this should throw
        } catch( MirroredTypeException mte ) {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }

}
