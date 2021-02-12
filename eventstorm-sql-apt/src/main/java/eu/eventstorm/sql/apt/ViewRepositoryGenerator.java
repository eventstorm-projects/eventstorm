package eu.eventstorm.sql.apt;

import static eu.eventstorm.sql.apt.Helper.toUpperCase;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.sql.apt.model.ViewDescriptor;
import eu.eventstorm.page.Page;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Repository;
import eu.eventstorm.sql.expression.AggregateFunctions;
import eu.eventstorm.sql.expression.Expressions;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ViewRepositoryGenerator implements Generator {

	private static Logger logger;

	ViewRepositoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(ViewRepositoryGenerator.class);
	}

    @Override
    public void generate(ProcessingEnvironment env, SourceCode code) {

        try {
             // generate Implementation class;
            code.forEachView(t -> {
                try {
                    generate(env, t);
                } catch (Exception cause) {
                    logger.error("ViewRepositoryGenerator -> Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        } finally {
            logger = null;
        }

    }

    private void generate(ProcessingEnvironment env, ViewDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.getPackage() + ".Abstract" + descriptor.simpleName() +"Repository");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeVariables(writer, descriptor);
        writeConstructor(writer, descriptor);

        writeMethods(writer, descriptor);

        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, ProcessingEnvironment env, ViewDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeNewLine(writer);

        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.ALL;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.COLUMNS;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.VIEW;");
        writeNewLine(writer);
    
        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".eq;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".and;");
        writeNewLine(writer);


        writeGenerated(writer, ViewRepositoryGenerator.class.getName());

        writer.write("public abstract class Abstract");
        writer.write(descriptor.simpleName() + "Repository");
        writer.write(" extends ");
        writer.write(Repository.class.getName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, ViewDescriptor descriptor) throws IOException {
        
    }

    private static void writeConstructor(Writer writer, ViewDescriptor descriptor) throws IOException {
        writer.write("    protected Abstract");
        writer.write(descriptor.simpleName() + "Repository(");
        writer.write(Database.class.getName());
        writer.write(" database){");
        writeNewLine(writer);
        writer.write("        super(database);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }


//    private static void generateFindById(Writer writer, ViewDescriptor descriptor) throws IOException {
//        writer.write("        this.findById = select(ALL).from(TABLE).where(");
//
//        if (descriptor.ids().size() == 1) {
//            writer.write("eq(");
//            writer.write(toUpperCase(descriptor.ids().get(0).name()));
//            writer.write(")");
//        } else {
//        	StringBuilder builder = new StringBuilder();
//        	builder.append("and(");
//        	for (ViewPropertyDescriptor id : descriptor.ids()) {
//        		builder.append("eq(");
//        		builder.append(toUpperCase(id.name()));
//        		builder.append("),");
//        	}
//        	builder.deleteCharAt(builder.length() - 1);
//        	builder.append(")");
//        	writer.write(builder.toString());
//        }
//        writer.write(").build();");
//        writeNewLine(writer);
//
//    }
//
//    private static void generateFindByIdForUpdate(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
//            return;
//        }
//
//    	if (descriptor.getJoinTable() != null) {
//    		return;
//    	}
//
//        writer.write("        this.findByIdForUpdate = select(ALL).from(TABLE).where(");
//		if (descriptor.ids().size() == 1) {
//			writer.write("eq(");
//			writer.write(toUpperCase(descriptor.ids().get(0).name()));
//			writer.write(")");
//		} else {
//			int max = descriptor.ids().size();
//			writer.write("and(");
//			for (int i = 0; i < max; i++) {
//				ViewPropertyDescriptor ppd = descriptor.ids().get(i);
//				writer.write("eq(");
//				writer.write(toUpperCase(ppd.name()));
//				writer.write(")");
//				writer.write(i < max - 1 ? ", " : "");
//			}
//			writer.write(")");
//		}
//		writer.write(").forUpdate().build();");
//		writeNewLine(writer);
//
//    }
//
//    private static void generateFindByBusinessKey(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//        if (descriptor.getJoinTable() != null) {
//    		return;
//    	}
//
//        List<ViewPropertyDescriptor> ppds = descriptor.businessKeys();
//
//        if (ppds.size() == 0) {
//            return;
//        }
//
//        writer.write("        this.findByBusinessKey = select(ALL).from(TABLE).where(");
//
//        if (ppds.size() > 1) {
//            writer.write("and(");
//        }
//        for (int i = 0; i < ppds.size(); i++) {
//            writer.write("eq(");
//            writer.write(Helper.toUpperCase(ppds.get(i).name()));
//            writer.write(')');
//            if (i + 1 < ppds.size()) {
//                writer.write(", ");
//            }
//        }
//        if (ppds.size() > 1) {
//            writer.write(')');
//        }
//        writer.write(").build();");
//        writeNewLine(writer);
//    }
//
//    private static void generateInsert(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
//            return;
//        }
//
//        if (hasAutoIncrementPK(descriptor)) {
//            writer.write("        this.insert = insert(TABLE, ");
//            writer.write(ImmutableList.class.getName());
//            writer.write(".of(), COLUMNS).build();");
//        } else {
//            writer.write("        this.insert = insert(TABLE, IDS, COLUMNS).build();");
//        }
//        writeNewLine(writer);
//
//    }
//
//    private static void generateUpdate(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
//            return;
//        }
//
//    	if (descriptor.getJoinTable() != null) {
//    		return;
//    	}
//
//        writer.write("        this.update = update(TABLE, COLUMNS, IDS).build();");
//        writeNewLine(writer);
//
//    }
//
//    private static void generateDelete(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
//            return;
//        }
//    	if (descriptor.getJoinTable() == null && descriptor.getTable() == null) {
//    		return;
//    	}
//
//    	 List<ViewPropertyDescriptor> ppds = descriptor.ids();
//
//    	 writer.write("        this.delete = delete(TABLE).where(");
//
//         if (ppds.size() > 1) {
//             writer.write("and(");
//         }
//         for (int i = 0; i < ppds.size(); i++) {
//             writer.write("eq(");
//             writer.write(Helper.toUpperCase(ppds.get(i).name()));
//             writer.write(')');
//             if (i + 1 < ppds.size()) {
//                 writer.write(", ");
//             }
//         }
//         if (ppds.size() > 1) {
//             writer.write(')');
//         }
//         writer.write(").build();");
//
//
//        writeNewLine(writer);
//
//    }


    private static void writeMethods(Writer writer, ViewDescriptor descriptor) throws IOException {
        generateMethodPage(writer, descriptor);
    }
//
//
//    private static void generateMethodFindById(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//        writeNewLine(writer);
//        writer.write("    public final ");
//        writer.write(descriptor.element().toString());
//        writer.write(" findById(");
//
//        StringBuilder builder = new StringBuilder();
//        StringBuilder ps = new StringBuilder();
//        int i = 1;
//        for (ViewPropertyDescriptor id : descriptor.ids()) {
//        	builder.append(id.getter().getReturnType().toString());
//        	builder.append(' ');
//        	builder.append(id.name());
//        	builder.append(',');
//
//        	ps.append("           ");
//        	ps.append("ps.");
//        	ps.append(preparedStatementSetter(id.getter().getReturnType().toString()));
//        	ps.append("(");
//        	ps.append(i++);
//        	ps.append(", ");
//        	ps.append(id.name());
//        	ps.append(");\n");
//        }
//        builder.deleteCharAt(builder.length() - 1);
//
//        writer.write(builder.toString());
//        writer.write(") {");
//        writeNewLine(writer);
//        writer.write("        return executeSelect(this.findById, ps -> {");
//
//        writeNewLine(writer);
//        writer.write(ps.toString());
//        writer.write("        }");
//        writer.write(", Mappers.");
//        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
//        writer.write(");");
//
//        writeNewLine(writer);
//        writer.write("    }");
//        writeNewLine(writer);
//
//    }
//
//    private static void generateMethodFindByIdForUpdate(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
//            return;
//        }
//
//    	if (descriptor.getJoinTable() != null) {
//    		return;
//    	}
//
//        writeNewLine(writer);
//		writer.write("    public final ");
//		writer.write(descriptor.element().toString());
//		writer.write(" findByIdForUpdate(");
//
//		if (descriptor.ids().size() == 1) {
//			writer.write(descriptor.ids().get(0).getter().getReturnType().toString());
//			writer.write(" id");
//		} else {
//			int max = descriptor.ids().size();
//			for (int i = 0; i < max; i++) {
//				ViewPropertyDescriptor ppd = descriptor.ids().get(i);
//				writer.write(ppd.getter().getReturnType().toString());
//				writer.write(" ");
//				writer.write(ppd.name());
//				if (i < max - 1) {
//					writer.write(", ");
//				}
//			}
//		}
//
//		writer.write(") {");
//		writeNewLine(writer);
//		writer.write("        return executeSelect(this.findByIdForUpdate, ps -> ");
//
//		if (descriptor.ids().size() == 1) {
//			ViewPropertyDescriptor ppd = descriptor.ids().get(0);
//			writer.write("ps.");
//			writer.write(preparedStatementSetter(ppd.getter().getReturnType().toString()));
//			writer.write("(1, id)");
//		} else {
//			writer.write("{ ");
//			for (int i = 0; i < descriptor.ids().size(); i++) {
//				ViewPropertyDescriptor ppd = descriptor.ids().get(i);
//				writer.write("ps.");
//				writer.write(preparedStatementSetter(ppd.getter().getReturnType().toString()));
//				writer.write("(");
//				int foo = i + 1;
//				writer.write("" + foo);
//				writer.write(", ");
//				writer.write(ppd.name());
//				writer.write("); ");
//			}
//			writer.write("}");
//		}
//		writer.write(", Mappers.");
//		writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
//		writer.write(");");
//
//		writeNewLine(writer);
//		writer.write("    }");
//		writeNewLine(writer);
//
//    }
//
//    private static boolean checkCUD(ViewDescriptor descriptor) {
//    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
//            return false;
//        }
//
//    	if (descriptor.getJoinTable() != null) {
//    		return false;
//    	}
//
//    	return true;
//    }
//
//    private static void generateMethodInsert(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//    	if (!checkCUD(descriptor)) {
//    		return;
//    	}
//
//        writeNewLine(writer);
//        writer.write("    public final void insert(");
//        writer.write(descriptor.element().toString());
//        writer.write(" pojo) {");
//
//        for (ViewPropertyDescriptor id : descriptor.ids()) {
//        	if (id.getter().getAnnotation(Sequence.class) != null) {
//                writeNewLine(writer);
//                writer.write("        // generate identifier");
//                writeNewLine(writer);
//                writer.write("        pojo.");
//                writer.write(id.setter().getSimpleName().toString());
//                writer.write("(idGenerator.next());");
//                writeNewLine(writer);
//            }
//        }
//
//        for (ViewPropertyDescriptor property : descriptor.properties()) {
//            if (property.getter().getAnnotation(CreateTimestamp.class) != null) {
//                writeNewLine(writer);
//                writer.write("        // set create timestamp");
//                writeNewLine(writer);
//                writer.write("        pojo.");
//                writer.write(property.setter().getSimpleName().toString());
//                writer.write("(new ");
//                writer.write(Timestamp.class.getName());
//                writer.write("(System.currentTimeMillis()));");
//                writeNewLine(writer);
//            }
//        }
//
//        writeNewLine(writer);
//        writer.write("        // execute insert");
//        writeNewLine(writer);
//
//        if (descriptor.ids().size() == 1 && descriptor.ids().get(0).getter().getAnnotation(AutoIncrement.class) != null) {
//            writer.write("        executeInsertAutoIncrement(this.insert, Mappers.");
//        } else {
//            writer.write("        executeInsert(this.insert, Mappers.");
//        }
//
//        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
//        writer.write(", pojo);");
//
//        writeNewLine(writer);
//        writer.write("    }");
//        writeNewLine(writer);
//
//    }
//
//    private static void generateMethodUpdate(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//    	if (!checkCUD(descriptor)) {
//    		return;
//    	}
//
//        writeNewLine(writer);
//        writer.write("    public final void update(");
//        writer.write(descriptor.element().toString());
//        writer.write(" pojo) {");
//
//        for (ViewPropertyDescriptor property : descriptor.properties()) {
//            if (property.getter().getAnnotation(UpdateTimestamp.class) != null) {
//                writeNewLine(writer);
//                writer.write("        // set update timestamp");
//                writeNewLine(writer);
//                writer.write("        pojo.");
//                writer.write(property.setter().getSimpleName().toString());
//                writer.write("(new ");
//                writer.write(Timestamp.class.getName());
//                writer.write("(System.currentTimeMillis()));");
//                writeNewLine(writer);
//            }
//        }
//
//        writeNewLine(writer);
//        writer.write("        // execute update");
//        writeNewLine(writer);
//        writer.write("        executeUpdate(this.update, Mappers.");
//        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
//        writer.write(", pojo);");
//
//        writeNewLine(writer);
//        writer.write("    }");
//        writeNewLine(writer);
//
//    }
//
//    private static void generateMethodBatch(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//    	if (!checkCUD(descriptor)) {
//    		return;
//    	}
//
//        writeNewLine(writer);
//        writer.write("    public final "+ Batch.class.getName() +"<"+ descriptor.element().toString() +"> batch() {");
//        writeNewLine(writer);
//
//        writer.write("        return batch(this.insert, Mappers.");
//
//        // todo check autoincrement ...
//        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
//        writer.write(");");
//
//        writeNewLine(writer);
//        writer.write("    }");
//        writeNewLine(writer);
//
//    }
//
//    private static void generateMethodFindByBusinessKey(Writer writer, ViewDescriptor descriptor) throws IOException {
//
//        List<ViewPropertyDescriptor> businessKeys = descriptor.businessKeys();
//        if (businessKeys.size() == 0) {
//            return;
//        }
//
//        writeNewLine(writer);
//        writer.write("    public final ");
//        writer.write(descriptor.element().toString());
//        writer.write(" findByBusinessKey(");
//
//        for (int i = 0; i < businessKeys.size(); i++) {
//            ViewPropertyDescriptor ppd = businessKeys.get(i);
//            writer.write(ppd.getter().getReturnType().toString());
//            writer.write(' ');
//            writer.write(ppd.name());
//            if (i + 1 < businessKeys.size()) {
//                writer.write(", ");
//            }
//        }
//        writer.write(") {");
//        writeNewLine(writer);
//
//        writer.write("        return executeSelect(this.findByBusinessKey, ps -> ");
//
//        if (businessKeys.size() == 1) {
//            writer.write("ps.");
//            writer.write(preparedStatementSetter(businessKeys.get(0).getter().getReturnType().toString()));
//            writer.write("(1, ");
//            writer.write(businessKeys.get(0).name());
//            writer.write(")");
//
//        } else {
//            writer.write('{');
//            writeNewLine(writer);
//            for (int i = 0; i < businessKeys.size(); i++) {
//                writer.write("            ");
//                writer.write("ps.");
//                writer.write(preparedStatementSetter(businessKeys.get(i).getter().getReturnType().toString()));
//                writer.write('(');
//                writer.write("" + (i + 1));
//                writer.write(", ");
//                writer.write(businessKeys.get(i).name());
//                writer.write(");");
//                writeNewLine(writer);
//            }
//            writeNewLine(writer);
//            writer.write("        }");
//        }
//
//        writer.write(", Mappers.");
//        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
//        writer.write(");");
//
//        writeNewLine(writer);
//        writer.write("    }");
//        writeNewLine(writer);
//    }


    private static void generateMethodPage(Writer writer, ViewDescriptor descriptor) throws IOException {

    	if (descriptor.getView().pageable() == false) {
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

        writer.write("        String sqlCount = select("+AggregateFunctions.class.getName() + ".count(*)).from(VIEW).build();");
        writeNewLine(writer);
        writer.write("        // create sql for select");
        writeNewLine(writer);
        writer.write("        String sqlPage = select(ALL).from(VIEW).offset(pageable.getPageNumber()* pageable.getPageSize()).limit(pageable.getPageSize()).build();");
        writeNewLine(writer);
        writer.write("        return executeSelectPage(sqlCount, sqlPage");
        writer.write(", Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pageable);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }


}