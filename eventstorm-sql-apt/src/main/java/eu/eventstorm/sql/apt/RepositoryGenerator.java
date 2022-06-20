package eu.eventstorm.sql.apt;

import static eu.eventstorm.sql.apt.Helper.hasAutoIncrementPK;
import static eu.eventstorm.sql.apt.Helper.preparedStatementSetter;
import static eu.eventstorm.sql.apt.Helper.toUpperCase;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.annotation.ColumnFormat;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Repository;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.annotation.AutoIncrement;
import eu.eventstorm.sql.annotation.CreateTimestamp;
import eu.eventstorm.sql.annotation.JoinColumn;
import eu.eventstorm.sql.annotation.JoinTable;
import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.annotation.UpdateTimestamp;
import eu.eventstorm.sql.expression.AggregateFunctions;
import eu.eventstorm.sql.expression.Expressions;
import eu.eventstorm.sql.id.SequenceGenerator4Integer;
import eu.eventstorm.sql.id.SequenceGenerator4Long;
import eu.eventstorm.sql.jdbc.Batch;
import eu.eventstorm.page.Page;
import eu.eventstorm.page.PageRequest;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class RepositoryGenerator implements Generator {

	private static Logger logger;
    private static SourceCode sourceCode;

	RepositoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(PojoImplementationGenerator.class);
	}

    @Override
    public void generate(ProcessingEnvironment env, SourceCode code) {

        try {
            sourceCode = code;
             // generate Implementation class;
            code.forEach(t -> {
                try {
                    generate(env, t);
                } catch (Exception cause) {
                    logger.error("PojoImplementationGenerator -> IOException for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        } finally {
            sourceCode = null;
            logger = null;
        }

    }

    private void generate(ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.getPackage() + ".Abstract" + descriptor.simpleName() +"Repository");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeVariables(writer, descriptor);
        writeConstructor(writer, descriptor);

        writeMethods(writer, descriptor);

        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeNewLine(writer);

        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.ALL;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.IDS;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.COLUMNS;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.TABLE;");
        writeNewLine(writer);
        writer.write("import " + SqlQuery.class.getName() + ";");
        writeNewLine(writer);
        
        for (PojoPropertyDescriptor id : descriptor.ids()) {
            writer.write("import static ");
            writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.");
            writer.write(toUpperCase(id.name()));
            writer.write(";");
            writeNewLine(writer);
        }

        for (PojoPropertyDescriptor bk : descriptor.businessKeys()) {
            writer.write("import static ");
            writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.");
            writer.write(toUpperCase(bk.name()));
            writer.write(";");
            writeNewLine(writer);
        }

        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".eq;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".and;");
        writeNewLine(writer);


        writeGenerated(writer, RepositoryGenerator.class.getName());

        writer.write("public abstract class Abstract");
        writer.write(descriptor.simpleName() + "Repository");
        writer.write(" extends ");
        writer.write(Repository.class.getName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        
        if (descriptor.ids().size() > 0) {
        	 writer.write("    private final SqlQuery findById;");
             writeNewLine(writer);
        }

        if (descriptor.businessKeys().size() > 0) {
            writer.write("    private final SqlQuery findByBusinessKey;");
            writeNewLine(writer);
        }

        if (descriptor.getTable() != null && !descriptor.getTable().immutable()) {
        	if (descriptor.ids().size() > 0) {
        		writer.write("    private final SqlQuery findByIdForUpdate;");
                writeNewLine(writer);
                writer.write("    private final SqlQuery delete;");
                writeNewLine(writer);
                writer.write("    private final SqlQuery update;");
                writeNewLine(writer);
        	}
            writer.write("    private final SqlQuery insert;");
            writeNewLine(writer);
        }

        JoinTable joinTable = descriptor.element().getAnnotation(JoinTable.class);
        if (joinTable != null) {
            writer.write("    private final SqlQuery insert;");
            writeNewLine(writer);
            writer.write("    private final SqlQuery delete;");
            writeNewLine(writer);
        }


        for (PojoPropertyDescriptor id : descriptor.ids()) {
        	if (id.getter().getAnnotation(Sequence.class) != null) {
                writeNewLine(writer);
                writer.write("    private final ");
                String sequenceClass;
                if ("int".equals(id.getter().getReturnType().toString())) {
                	sequenceClass = SequenceGenerator4Integer.class.getName();
                } else if ("long".equals(id.getter().getReturnType().toString())) {
                	sequenceClass = SequenceGenerator4Long.class.getName();
                } else {
                	throw new IllegalStateException();
                }
                writer.write(sequenceClass);
                writer.write(" idGenerator = new ");
                writer.write(sequenceClass);
                writer.write("(database(), ");
                writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.SEQUENCE");
                writer.write(");");
                writeNewLine(writer);
                
                writer.write("    private final java.util.function.BiConsumer<" + descriptor.fullyQualidiedClassName() + ",");

                if ("int".equals(id.getter().getReturnType().toString())) {
                    writer.write("Integer");
                } else if ("long".equals(id.getter().getReturnType().toString())) {
                    writer.write("Long");
                } else {
                    throw new IllegalStateException();
                }
                
                writer.write("> identifierSetter = (pojo,id) -> ");
                writer.write(" pojo.");
                writer.write(id.setter().getSimpleName().toString());
                writer.write("(id) ;");
                writeNewLine(writer);

            }
        }

        writeNewLine(writer);

    }

    private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
        writer.write("    protected Abstract");
        writer.write(descriptor.simpleName() + "Repository(");
        writer.write(Database.class.getName());
        writer.write(" database){");
        writeNewLine(writer);
        writer.write("        super(database);");
        writeNewLine(writer);

        
        generateFindById(writer, descriptor);
        generateFindByIdForUpdate(writer, descriptor);
        generateFindByBusinessKey(writer, descriptor);
        generateInsert(writer, descriptor);
        generateUpdate(writer, descriptor);
        generateDelete(writer, descriptor);


        writer.write("    }");
        writeNewLine(writer);
    }


    private static void generateFindById(Writer writer, PojoDescriptor descriptor) throws IOException {
    	
    	if (descriptor.ids().size() == 0) {
    		return;
    	}
    	
        writer.write("        this.findById = select(ALL).from(TABLE).where(");

        if (descriptor.ids().size() == 1) {
            writer.write("eq(");
            writer.write(toUpperCase(descriptor.ids().get(0).name()));
            writer.write(")");
        } else {
        	StringBuilder builder = new StringBuilder();
        	builder.append("and(");
        	for (PojoPropertyDescriptor id : descriptor.ids()) {
        		builder.append("eq(");
        		builder.append(toUpperCase(id.name()));
        		builder.append("),");
        	}
        	builder.deleteCharAt(builder.length() - 1);
        	builder.append(")");
        	writer.write(builder.toString());
        }
        writer.write(").build();");
        writeNewLine(writer);

    }

    private static void generateFindByIdForUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
            return;
        }

    	if (descriptor.getJoinTable() != null) {
    		return;
    	}

    	if (descriptor.ids().size() == 0) {
    		return;
    	}
    	
        writer.write("        this.findByIdForUpdate = select(ALL).from(TABLE).where(");
		if (descriptor.ids().size() == 1) {
			writer.write("eq(");
			writer.write(toUpperCase(descriptor.ids().get(0).name()));
			writer.write(")");
		} else {
			int max = descriptor.ids().size();
			writer.write("and(");
			for (int i = 0; i < max; i++) {
				PojoPropertyDescriptor ppd = descriptor.ids().get(i);
				writer.write("eq(");
				writer.write(toUpperCase(ppd.name()));
				writer.write(")");
				writer.write(i < max - 1 ? ", " : "");
			}
			writer.write(")");
		}
		writer.write(").forUpdate().build();");
		writeNewLine(writer);

    }

    private static void generateFindByBusinessKey(Writer writer, PojoDescriptor descriptor) throws IOException {

        if (descriptor.getJoinTable() != null) {
    		return;
    	}

        List<PojoPropertyDescriptor> ppds = descriptor.businessKeys();

        if (ppds.size() == 0) {
            return;
        }

        writer.write("        this.findByBusinessKey = select(ALL).from(TABLE).where(");

        if (ppds.size() > 1) {
            writer.write("and(");
        }
        for (int i = 0; i < ppds.size(); i++) {
            writer.write("eq(");
            writer.write(Helper.toUpperCase(ppds.get(i).name()));
            writer.write(')');
            if (i + 1 < ppds.size()) {
                writer.write(", ");
            }
        }
        if (ppds.size() > 1) {
            writer.write(')');
        }
        writer.write(").build();");
        writeNewLine(writer);
    }

    private static void generateInsert(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
            return;
        }

        if (hasAutoIncrementPK(descriptor)) {
            writer.write("        this.insert = insert(TABLE, ");
            writer.write(ImmutableList.class.getName());
            writer.write(".of(), COLUMNS).build();");
        } else {
            writer.write("        this.insert = insert(TABLE, IDS, COLUMNS).build();");
        }
        writeNewLine(writer);

    }

    private static void generateUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (!checkCUD(descriptor)) {
    		return;
    	}
    	
    	if (descriptor.ids().size() == 0) {
    		return;
    	}

        writer.write("        this.update = update(TABLE, COLUMNS, IDS).build();");
        writeNewLine(writer);

    }

    private static void generateDelete(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
            return;
        }
    	
    	if (descriptor.ids().size() == 0) {
    		return;
    	}
    	
    	 List<PojoPropertyDescriptor> ppds = descriptor.ids();

    	 writer.write("        this.delete = delete(TABLE).where(");

         if (ppds.size() > 1) {
             writer.write("and(");
         }
         for (int i = 0; i < ppds.size(); i++) {
             writer.write("eq(");
             writer.write(Helper.toUpperCase(ppds.get(i).name()));
             writer.write(')');
             if (i + 1 < ppds.size()) {
                 writer.write(", ");
             }
         }
         if (ppds.size() > 1) {
             writer.write(')');
         }
         writer.write(").build();");


        writeNewLine(writer);

    }


    private static void writeMethods(Writer writer, PojoDescriptor descriptor) throws IOException {
        generateMethodFindById(writer, descriptor);
        generateMethodFindByIdForUpdate(writer, descriptor);
        generateMethodInsert(writer, descriptor);
        generateMethodUpdate(writer, descriptor);
        generateMethodBatch(writer, descriptor);
        generateMethodFindByBusinessKey(writer, descriptor);
        generateMethodDelete(writer, descriptor);
        //generateMethodPage(writer, descriptor);
        generateMethodLink(writer, descriptor);
        generateMethodUnlink(writer, descriptor);
    }


    private static void generateMethodFindById(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.ids().size() == 0) {
    		return;
    	}
    	
        writeNewLine(writer);
        writer.write("    public final ");
        writer.write(descriptor.element().toString());
        writer.write(" findById(");

        StringBuilder builder = new StringBuilder();
        StringBuilder ps = new StringBuilder();
        int i = 1;
        for (PojoPropertyDescriptor id : descriptor.ids()) {

            PrimaryKey primaryKey = id.getter().getAnnotation(PrimaryKey.class);

        	builder.append(id.getter().getReturnType().toString());
        	builder.append(' ');
        	builder.append(id.name());
        	builder.append(',');

            ps.append("           ");
            if (ColumnFormat.UUID.equals(primaryKey.format())) {
                ps.append("dialect().setPreparedStatement(ps, " + i++ + ",");
                ps.append(id.name());
                ps.append(");\n");
            } else {
                ps.append("ps.");
                ps.append(preparedStatementSetter(id.getter().getReturnType().toString()));
                ps.append("(");
                ps.append(i++);
                ps.append(", ");
                ps.append(id.name());
                ps.append(");\n");
            }



        }
        builder.deleteCharAt(builder.length() - 1);

        writer.write(builder.toString());
        writer.write(") {");
        writeNewLine(writer);
        writer.write("        return executeSelect(this.findById, ps -> {");

        writeNewLine(writer);
        writer.write(ps.toString());
        writer.write("        }");
        writer.write(", Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodFindByIdForUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (!checkCUD(descriptor)) {
    		return;
    	}
    	
    	if (descriptor.ids().size() == 0) {
    		return;
    	}

        writeNewLine(writer);
		writer.write("    public final ");
		writer.write(descriptor.element().toString());
		writer.write(" findByIdForUpdate(");

		if (descriptor.ids().size() == 1) {
			writer.write(descriptor.ids().get(0).getter().getReturnType().toString());
			writer.write(" id");
		} else {
			int max = descriptor.ids().size();
			for (int i = 0; i < max; i++) {
				PojoPropertyDescriptor ppd = descriptor.ids().get(i);
				writer.write(ppd.getter().getReturnType().toString());
				writer.write(" ");
				writer.write(ppd.name());
				if (i < max - 1) {
					writer.write(", ");
				}
			}
		}

		writer.write(") {");
		writeNewLine(writer);
		writer.write("        return executeSelect(this.findByIdForUpdate, ps -> ");

		if (descriptor.ids().size() == 1) {
			PojoPropertyDescriptor ppd = descriptor.ids().get(0);
			writer.write("ps.");
			writer.write(preparedStatementSetter(ppd.getter().getReturnType().toString()));
			writer.write("(1, id)");
		} else {
			writer.write("{ ");
			for (int i = 0; i < descriptor.ids().size(); i++) {
				PojoPropertyDescriptor ppd = descriptor.ids().get(i);
				writer.write("ps.");
				writer.write(preparedStatementSetter(ppd.getter().getReturnType().toString()));
				writer.write("(");
				int foo = i + 1;
				writer.write("" + foo);
				writer.write(", ");
				writer.write(ppd.name());
				writer.write("); ");
			}
			writer.write("}");
		}
		writer.write(", Mappers.");
		writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
		writer.write(");");

		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);

    }

    private static boolean checkCUD(PojoDescriptor descriptor) {
    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
            return false;
        }

    	if (descriptor.getJoinTable() != null) {
    		return false;
    	}
    	
    	return true;
    }

    private static void generateMethodInsert(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (!checkCUD(descriptor)) {
    		return;
    	}

        writeNewLine(writer);
        writer.write("    public final void insert(");
        writer.write(descriptor.element().toString());
        writer.write(" pojo) {");

        for (PojoPropertyDescriptor id : descriptor.ids()) {
        	if (id.getter().getAnnotation(Sequence.class) != null) {
                writeNewLine(writer);
                writer.write("        // generate identifier");
                writeNewLine(writer);
                writer.write("        pojo.");
                writer.write(id.setter().getSimpleName().toString());
                writer.write("(idGenerator.next());");
                writeNewLine(writer);
            }
        }

        for (PojoPropertyDescriptor property : descriptor.properties()) {
            if (property.getter().getAnnotation(CreateTimestamp.class) != null) {
                writeNewLine(writer);
                writer.write("        // set create timestamp");
                writeNewLine(writer);
                writer.write("        pojo.");
                writer.write(property.setter().getSimpleName().toString());
                writer.write("(new ");
                writer.write(Timestamp.class.getName());
                writer.write("(System.currentTimeMillis()));");
                writeNewLine(writer);
            }
        }

        writeNewLine(writer);
        writer.write("        // execute insert");
        writeNewLine(writer);

        if (descriptor.ids().size() == 1 && descriptor.ids().get(0).getter().getAnnotation(AutoIncrement.class) != null) {
            writer.write("        executeInsertAutoIncrement(this.insert, Mappers.");
        } else {
            writer.write("        executeInsert(this.insert, Mappers.");
        }

        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }
    
    private static void generateMethodDelete(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (!checkCUD(descriptor)) {
    		return;
    	}
    	
    	if (descriptor.ids().size() == 0) {
    		return;
    	}

        writeNewLine(writer);
        writer.write("    public final void delete(");
        
        StringBuilder builder = new StringBuilder();
        for (PojoPropertyDescriptor desc : descriptor.ids()) {
        	builder.append(Helper.getReturnType(desc.getter())).append(" ").append(desc.name()).append(", ");
        }
        builder.deleteCharAt(builder.length() -1);
        builder.deleteCharAt(builder.length() -1);
        writer.write(builder.toString());
        writer.write(") {");

        writeNewLine(writer);
        writer.write("        executeDelete(this.delete, ps -> {");
        writeNewLine(writer);
        int i = 1;
        for (PojoPropertyDescriptor desc : descriptor.ids()) {
            if (ColumnFormat.UUID.equals(desc.getter().getAnnotation(PrimaryKey.class).format())) {
                writer.write("            dialect().setPreparedStatement(ps, " + i++ + ",");
                writer.write(desc.name());
                writer.write(");");
            } else {
                writer.write("            ps."  + Helper.preparedStatementSetter(Helper.getReturnType(desc.getter())) + "(" + (i++) +"," + desc.name() + ");");
            }
        	 writeNewLine(writer);
        }
        writer.write("        });");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (!checkCUD(descriptor)) {
    		return;
    	}
    	
    	if (descriptor.ids().size() == 0) {
    		return;
    	}

        writeNewLine(writer);
        writer.write("    public final void update(");
        writer.write(descriptor.element().toString());
        writer.write(" pojo) {");

        for (PojoPropertyDescriptor property : descriptor.properties()) {
            if (property.getter().getAnnotation(UpdateTimestamp.class) != null) {
                writeNewLine(writer);
                writer.write("        // set update timestamp");
                writeNewLine(writer);
                writer.write("        pojo.");
                writer.write(property.setter().getSimpleName().toString());
                writer.write("(new ");
                writer.write(Timestamp.class.getName());
                writer.write("(System.currentTimeMillis()));");
                writeNewLine(writer);
            }
        }

        writeNewLine(writer);
        writer.write("        // execute update");
        writeNewLine(writer);
        writer.write("        executeUpdate(this.update, Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodBatch(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (!checkCUD(descriptor)) {
    		return;
    	}

        writeNewLine(writer);
        writer.write("    public final "+ Batch.class.getName() +"<"+ descriptor.element().toString() +"> batch() {");
        writeNewLine(writer);

        writer.write("        return batch(this.insert, Mappers.");
        // todo check autoincrement ...
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        
        for (PojoPropertyDescriptor id : descriptor.ids()) {
            if (id.getter().getAnnotation(Sequence.class) != null) {
                writer.write(", idGenerator, identifierSetter");
                break;
            }
        }
        

        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodFindByBusinessKey(Writer writer, PojoDescriptor descriptor) throws IOException {

        List<PojoPropertyDescriptor> businessKeys = descriptor.businessKeys();
        if (businessKeys.size() == 0) {
            return;
        }

        writeNewLine(writer);
        writer.write("    public final ");
        writer.write(descriptor.element().toString());
        writer.write(" findByBusinessKey(");

        for (int i = 0; i < businessKeys.size(); i++) {
            PojoPropertyDescriptor ppd = businessKeys.get(i);
            writer.write(ppd.getter().getReturnType().toString());
            writer.write(' ');
            writer.write(ppd.name());
            if (i + 1 < businessKeys.size()) {
                writer.write(", ");
            }
        }
        writer.write(") {");
        writeNewLine(writer);

        writer.write("        return executeSelect(this.findByBusinessKey, ps -> ");

        if (businessKeys.size() == 1) {
            writer.write("ps.");
            writer.write(preparedStatementSetter(businessKeys.get(0).getter().getReturnType().toString()));
            writer.write("(1, ");
            writer.write(businessKeys.get(0).name());
            writer.write(")");

        } else {
            writer.write('{');
            writeNewLine(writer);
            for (int i = 0; i < businessKeys.size(); i++) {
                writer.write("            ");
                writer.write("ps.");
                writer.write(preparedStatementSetter(businessKeys.get(i).getter().getReturnType().toString()));
                writer.write('(');
                writer.write("" + (i + 1));
                writer.write(", ");
                writer.write(businessKeys.get(i).name());
                writer.write(");");
                writeNewLine(writer);
            }
            writeNewLine(writer);
            writer.write("        }");
        }

        writer.write(", Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }


    private static void generateMethodPage(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getJoinTable() != null) {
    		return;
    	}

    	Table table = descriptor.getTable();

        if (!table.pageable()) {
            return;
        }

        writeNewLine(writer);
        writer.write("    public final ");
        writer.write(Page.class.getName());
        writer.write("<");
        writer.write(descriptor.element().toString());
        writer.write(">");
        writer.write(" page(");
        writer.write(PageRequest.class.getName());
        writer.write(" pageable) {");
        writeNewLine(writer);
        writer.write("        // create sql for count");
        writeNewLine(writer);

        writer.write("        String sqlCount = select("+AggregateFunctions.class.getName() + ".count(ID)).from(TABLE).build();");
        writeNewLine(writer);
        writer.write("        // create sql for select");
        writeNewLine(writer);
        writer.write("        String sqlPage = select(ALL).from(TABLE).offset(pageable.getPageNumber()* pageable.getPageSize()).limit(pageable.getPageSize()).build();");
        writeNewLine(writer);
        writer.write("        return executeSelectPage(sqlCount, sqlPage");
        writer.write(", Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pageable);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodLink(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getJoinTable() == null) {
    		return;
    	}

    	writeNewLine(writer);
        writer.write("    public final void link(");
        StringBuilder builder = new StringBuilder();
        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            JoinColumn jc = ppd.getter().getAnnotation(JoinColumn.class);
            PojoDescriptor target = sourceCode.getPojoDescriptor(getTarget(jc).toString());
            builder.append(target.fullyQualidiedClassName());
            builder.append(" ");
            builder.append(target.simpleName().toLowerCase());
            builder.append(", ");
        }

        if (builder.length() > 2) {
        	builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }


        writer.write(builder.toString());
        writer.write(") {");

        writeNewLine(writer);
        writer.write("        // execute insert");
        writeNewLine(writer);
        writer.write("        " + descriptor.fullyQualidiedClassName() + " pojo = new " + descriptor.fullyQualidiedClassName() + "Impl();");
        writeNewLine(writer);

        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            PojoDescriptor target = sourceCode.getPojoDescriptor(getTarget(ppd.getter().getAnnotation(JoinColumn.class)).toString());
            for (PojoPropertyDescriptor targetId : target.ids()) {
            	StringBuilder b = new StringBuilder();
            	b.append("pojo.");

            	b.append(ppd.setter().getSimpleName().toString()).append("(");
            	b.append(target.simpleName().toLowerCase() + "."+ targetId.getter().toString());
            	b.append(");");
            	writer.write("        " + b.toString());
            	writeNewLine(writer);
            }

        }


        writer.write("        executeInsert(this.insert, Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodUnlink(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getJoinTable() == null) {
    		return;
    	}

    	writeNewLine(writer);
        writer.write("    public final void unlink(");
        StringBuilder builder = new StringBuilder();
        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            JoinColumn jc = ppd.getter().getAnnotation(JoinColumn.class);
            PojoDescriptor target = sourceCode.getPojoDescriptor(getTarget(jc).toString());
            builder.append(target.fullyQualidiedClassName());
            builder.append(" ");
            builder.append(target.simpleName().toLowerCase());
            builder.append(", ");
        }

        if (builder.length() > 2) {
        	builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }


        writer.write(builder.toString());
        writer.write(") {");

        writeNewLine(writer);
        writer.write("        // execute delete");
        writeNewLine(writer);
        writer.write("        executeDelete(this.delete, ps -> {");
        writeNewLine(writer);
        int index = 1;
        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            PojoDescriptor target = sourceCode.getPojoDescriptor(getTarget(ppd.getter().getAnnotation(JoinColumn.class)).toString());
            for (PojoPropertyDescriptor targetId : target.ids()) {
            	writer.write("           ps.");
            	writer.write(Helper.preparedStatementSetter(targetId.getter().getReturnType().toString()));
            	writer.write("(" + index++);
            	writer.write(", ");
            	writer.write(target.simpleName().toLowerCase());
            	writer.write(".");
            	writer.write(targetId.getter().toString());
            	writer.write(");");
            	//StringBuilder b = new StringBuilder();
            	//b.append("pojo.");

            	//b.append(ppd.setter().getSimpleName().toString()).append("(");
            	//b.append(target.simpleName().toLowerCase() + "."+ targetId.getter().toString());
            	//b.append(");");
            	//writer.write("        " + b.toString());
            	writeNewLine(writer);
            }

        }
        writer.write("        });");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

	private static TypeMirror getTarget(JoinColumn annotation) {
		try {
			annotation.target(); // this should throw
		} catch (MirroredTypeException mte) {
			return mte.getTypeMirror();
		}
		 // can this ever happen ??
		throw new IllegalStateException();
	}

}