package eu.eventstorm.sql.apt;

import java.io.IOException;
import java.io.Writer;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.google.common.collect.ImmutableSet;

import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventstorm.sql.annotation.AutoIncrement;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;

public final class Helper {

	private static final ImmutableSet<String> KEYWORDS;
	
	static {
		KEYWORDS = ImmutableSet.<String>builder()
				.add("abstract") 	// Specifies that a class or method will be implemented later, in a subclass
				.add("assert")		// Assert describes a predicate (a true–false statement) placed in a Java program to indicate that the developer thinks that the predicate is always true at that place.
				.add("boolean")		// A data type that can hold True and False values only
				.add("break")		// A control statement for breaking out of loops
				.add("byte")		// A data type that can hold 8-bit data values
				.add("case")		// Used in switch statements to mark blocks of text
				.add("catch")	    // Catches exceptions generated by try statements
				.add("char")		// A data type that can hold unsigned 16-bit Unicode characters
				.add("class")		// Declares a new class
				.add("continue")	// Sends control back outside a loop
				.add("default")		// Specifies the default block of code in a switch statement
				.add("do")			// Starts a do-while loop
				.add("double")		// A data type that can hold 64-bit floating-point numbers
				.add("else")		// Indicates alternative branches in an if statement
				.add("enum")		// A Java keyword used to declare an enumerated type. Enumerations extend the base class.
				.add("extends")		// Indicates that a class is derived from another class or interface
				.add("final")		// Indicates that a variable holds a constant value or that a method will not be overridden
				.add("finally")		// Indicates a block of code in a try-catch structure that will always be executed
				.add("float")		// A data type that holds a 32-bit floating-point number
				.add("for")			// Used to start a for loop
				.add("if")			// Tests a true/false expression and branches accordingly
				.add("implements")	// Specifies that a class implements an interface
				.add("import")		// References other classes
				.add("instanceof")	// Indicates whether an object is an instance of a specific class or implements an interface
				.add("int")			// A data type that can hold a 32-bit signed integer
				.add("interface")	// Declares an interface
				.add("long")		// A data type that holds a 64-bit integer
				.add("native")		// Specifies that a method is implemented with native (platform-specific) code
				.add("new")			// Creates new objects
				.add("null")		// Indicates that a reference does not refer to anything
				.add("package")		// Declares a Java package
				.add("private")		// An access specifier indicating that a method or variable may be accessed only in the class it’s declared in
				.add("protected")	// An access specifier indicating that a method or variable may only be accessed in the class it’s declared in (or a subclass of the class it’s declared in or other classes in the same package)
				.add("public")		// An access specifier used for classes, interfaces, methods, and variables indicating that an item is accessible throughout the application (or where the class that defines it is accessible)
				.add("return")		// Sends control and possibly a return value back from a called method
				.add("short")		// A data type that can hold a 16-bit integer
				.add("static")		// Indicates that a variable or method is a class method (rather than being limited to one particular object)
				.add("strictfp")	// A Java keyword used to restrict the precision and rounding of floating point calculations to ensure portability.
				.add("super")		// Refers to a class’s base class (used in a method or class constructor)
				.add("switch")		// A statement that executes code based on a test value
				.add("synchronized")// Specifies critical sections or methods in multithreaded code
				.add("this")		// Refers to the current object in a method or constructor
				.add("throw")		// Creates an exception
				.add("throws")		// Indicates what exceptions may be thrown by a method
				.add("transient")	// Specifies that a variable is not part of an object’s persistent state
				.add("try")			// Starts a block of code that will be tested for exceptions
				.add("void")		// Specifies that a method does not have a return value
				.add("volatile")	// Indicates that a variable may change asynchronously
				.add("while")		// Starts a while loop
				.add("const")		//
				.add("goto")		//
				.build();
	}
	
	private static Types types;

	private Helper() {
	}
	
	public static void setTypes(Types t) {
		types = t;
	}

	public static Types getTypes() {
		return types;
	}
	
	public static String getReturnType(ExecutableElement element) {
		return element.getReturnType().toString();
//		TypeElement te = (TypeElement) types.asElement(element.getReturnType());
//
//		if (te != null) {
//			return element.getReturnType().toString();
//		} else {
//			// it's primitive
//			return element.getReturnType().toString();
//		}
	}

	public static boolean isEnum(TypeMirror typeMirror) {
		if (TypeKind.DECLARED == typeMirror.getKind()) {
			for (TypeMirror supertype : Helper.getTypes().directSupertypes(typeMirror)) {
				DeclaredType declared = (DeclaredType)supertype;
				if (Enum.class.getName().equals(declared.asElement().toString())) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}
		
	public static void writeNewLine(Writer writer) throws IOException {
		writer.write(System.getProperty("line.separator"));
	}

	public static void writeNewLine(StringBuilder builder) {
		builder.append(System.getProperty("line.separator"));
	}

	public static void writePackage(Writer writer, String pack) throws IOException {
		writer.write("package ");

		if (pack.startsWith("package")) {
			writer.write(pack.substring(7).trim());
		} else {
			writer.write(pack);
		}
		writer.write(';');
		writeNewLine(writer);
	}

	public static void writeGenerated(Writer writer, String clazz) throws IOException {
		//writeNewLine(writer);
		//writer.write("import javax.annotation.Generated;");
		//writeNewLine(writer);
		//writeNewLine(writer);
		writeNewLine(writer);
		writer.write("//javax.annotation.Generated(\"" + clazz + "\")");
		writeNewLine(writer);
	}

	public static String getSqlColumnName(PojoPropertyDescriptor ppd) {
		Column column = ppd.getter().getAnnotation(Column.class);
		if ("".equals(column.value())) {
			return ppd.name();
		} else {
			return column.value();
		}

	}

	public static String propertyName(String name) {
		StringBuilder builder = new StringBuilder(name.length());
		builder.append(Character.toLowerCase(name.charAt(0)));
		builder.append(name.substring(1));
		return builder.toString();
	}

	public static String firstToUpperCase(String name) {
		StringBuilder builder = new StringBuilder(name.length());
		builder.append(Character.toUpperCase(name.charAt(0)));
		builder.append(name.substring(1));
		return builder.toString();
	}

	public static String toSnakeCase(String name) {
		StringBuilder builder = new StringBuilder();
		builder.append(Character.toLowerCase(name.charAt(0)));
		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				builder.append('_');
			}
			builder.append(Character.toLowerCase(c));
		}
		return builder.toString();
	}

	public static String toUpperCase(String name) {
		StringBuilder builder = new StringBuilder();
		builder.append(Character.toUpperCase(name.charAt(0)));
		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				builder.append('_');
			}
			builder.append(Character.toUpperCase(c));
		}
		return builder.toString();
	}

	public static String preparedStatementSetter(String type) {

		if (isInteger(type)) {
			return "setInt";
		}

		if (isLong(type)) {
			return "setLong";
		}

		if (isShort(type)) {
			return "setShort";
		}

		if (isByte(type)) {
			return "setByte";
		}

		if (isFloat(type)) {
			return "setFloat";
		}

		if (isDouble(type)) {
			return "setDouble";
		}

		if (isBoolean(type)) {
			return "setBoolean";
		}

		if ("java.lang.String".equals(type)) {
			return "setString";
		}

		if ("java.sql.Date".equals(type)) {
			return "setDate";
		}
		
		if ("java.sql.Time".equals(type)) {
            return "setTime";
        }

		if ("java.sql.Timestamp".equals(type)) {
			return "setTimestamp";
		}

		if ("byte[]".equals(type)) {
			return "setBytes";
		}

		if ("long[]".equals(type) || "java.lang.Long[]".equals(type)) {
			return "setArray";
		}

		if ("int[]".equals(type) || "java.lang.Integer[]".equals(type)) {
			return "setArray";
		}

		throw new UnsupportedOperationException("Helper.preparedStatementSetter -> type not supported -> [" + type + "]");
	}

	public static String preparedStatementGetter(String type) {

		if (isInteger(type)) {
			return "getInt";
		}

		if (isLong(type)) {
			return "getLong";
		}

		if (isShort(type)) {
			return "getShort";
		}

		if (isByte(type)) {
			return "getByte";
		}

		if (isFloat(type)) {
			return "getFloat";
		}

		if (isDouble(type)) {
			return "getDouble";
		}

		if (isBoolean(type)) {
			return "getBoolean";
		}

		if ("java.lang.String".equals(type)) {
			return "getString";
		}

		if ("java.sql.Date".equals(type)) {
			return "getDate";
		}
		
		if ("java.sql.Time".equals(type)) {
            return "getTime";
        }

		if ("java.sql.Timestamp".equals(type)) {
			return "getTimestamp";
		}

		if ("java.sql.Blob".equals(type)) {
			return "getBlob";
		}
		
		if ("java.sql.Clob".equals(type)) {
			return "getClob";
		}
		
		if (Json.class.getName().equals(type)) {
			return "getString";
		}

		if ("byte[]".equals(type)) {
			return "getBytes";
		}

		if ("long[]".equals(type) || "java.lang.Long[]".equals(type)) {
			return "getArray";
		}

		if ("int[]".equals(type) || "java.lang.Integer[]".equals(type)) {
			return "getArray";
		}

		throw new UnsupportedOperationException("Helper.preparedStatementGetter -> type not supported -> [" + type + "]");
	}

	public static boolean isPrimitiveType(String type) {
		return ("int".equals(type) || "short".equals(type) || "long".equals(type) || "byte".equals(type) || "float".equals(type) || "double".equals(type)
		        || "boolean".equals(type));
	}

	public static String nullableType(String type) {

		if ("java.lang.Integer".equals(type)) {
			return "java.sql.Types.INTEGER";
		}

		if ("java.lang.Long".equals(type)) {
			return "java.sql.Types.BIGINT";
		}

		if ("java.lang.String".equals(type)) {
			return "java.sql.Types.VARCHAR";
		}

		if ("java.lang.Short".equals(type)) {
			return "java.sql.Types.SMALLINT";
		}

		if ("java.lang.Byte".equals(type)) {
			return "java.sql.Types.TINYINT";
		}

		if ("java.sql.Date".equals(type)) {
			return "java.sql.Types.DATE";
		}

		if ("java.sql.Timestamp".equals(type)) {
			return "java.sql.Types.TIMESTAMP";
		}

		if ("java.lang.Boolean".equals(type)) {
			return "java.sql.Types.BOOLEAN";
		}

		if ("eu.eventstorm.sql.type.Xml".equals(type)) {
			return "java.sql.Types.SQLXML";
		}
		
		if ("java.sql.Blob".equals(type)) {
			return "java.sql.Types.BLOB";
		}
		
		if ("java.sql.Clob".equals(type)) {
			return "java.sql.Types.CLOB";
		}

		throw new UnsupportedOperationException("Helper.nullableType -> type not supported -> [" + type + "]");

	}

	public static boolean hasAutoIncrementPK(PojoDescriptor desc) {
		for (PojoPropertyDescriptor ppd : desc.ids()) {
			if (ppd.getter().getAnnotation(AutoIncrement.class) != null) {
				return true;
			}
		}
		return false;
	}

	public static boolean isArray(String type) {
		// not for byte[]
		return "long[]".equals(type) || "java.lang.Long[]".equals(type) || "int[]".equals(type) || "java.lang.Integer[]".equals(type) || "short[]".equals(type)
		        || "java.lang.Short[]".equals(type);
	}


	public static int getArrayType(String type) {
		if ("long[]".equals(type) || "java.lang.Long[]".equals(type)) {
			return java.sql.Types.BIGINT;
		}

		if ("int[]".equals(type) || "java.lang.Integer[]".equals(type)) {
			return java.sql.Types.INTEGER;
		}
		return java.sql.Types.OTHER;
	}

	public static String isDialectType(String type) {

		if (Xml.class.getName().equals(type)) {
			return "fromJdbcXml";
		}

		if (Json.class.getName().equals(type)) {
			return "fromJdbcJson";
		}

		return null;
	}

	public static String toVariableName(String name) {
		if (KEYWORDS.contains(name)) {
			return name + '_'; 
		}
		return name;
	}

	public static boolean isNumber(String type) {
		return "java.lang.Integer".equals(type) || "java.lang.Long".equals(type) || "java.lang.Short".equals(type) || "java.lang.Byte".equals(type);
	}

	public static boolean isInteger(String type) {
		return ("int".equals(type) || "java.lang.Integer".equals(type));
	}

	public static boolean isLong(String type) {
		return ("long".equals(type) || "java.lang.Long".equals(type));
	}

	public static boolean isShort(String type) {
		return ("short".equals(type) || "java.lang.Short".equals(type));
	}

	public static boolean isByte(String type) {
		return ("byte".equals(type) || "java.lang.Byte".equals(type));
	}

	public static boolean isBoolean(String type) {
		return ("boolean".equals(type) || "java.lang.Boolean".equals(type));
	}

	public static boolean isFloat(String type) {
		return ("float".equals(type) || "java.lang.Float".equals(type));
	}

	public static boolean isDouble(String type) {
		return ("double".equals(type) || "java.lang.Double".equals(type));
	}
	
	public static boolean isString(String type) {
		return "java.lang.String".equals(type);
	}

}
