package org.telosys.tools.dsl.parser;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.FieldNameAndTypeError;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainTag;

public class Parser {

	private static final String DOT_MODEL = ".model";

	public Parser() {
		super();
	}

	/**
	 * Parse the MODEL identified by the ".model" file
	 * 
	 * @param file
	 *            the model file ( file with ".model" suffix )
	 * @return
	 * @throws ModelParsingError
	 */
	public final DomainModel parseModel(File file) throws ModelParsingError {

		// --- Step 0 : check model file validity
		checkModelFile(file);

		// --- Step 1 : init a void model using the ".model" file
		PropertiesManager propertiesManager = new PropertiesManager(file);
		Properties properties = propertiesManager.load();
		DomainModel model = new DomainModel(properties);

		List<String> entitiesFileNames = DslModelUtil.getEntitiesAbsoluteFileNames(file);
		List<String> entitiesNames = new LinkedList<>();

		// --- Step 2 : build VOID entities (1 instance for each entity defined
		// in the model)
		for (String entityFileName : entitiesFileNames) {
			String entityName = DslModelUtil.getEntityName(new File(entityFileName));
			entitiesNames.add(entityName);
			model.addEntity(new DomainEntity(entityName));
		}

		// --- Step 3 : parse each entity and populate it in the model
		int errorsCount = 0;
		for (String entityFileName : entitiesFileNames) {
			// --- Parse
			DomainEntity domainEntity;
			try {
				domainEntity = parseEntity(entityFileName, entitiesNames);
				// --- Replace VOID ENTITY by REAL ENTITY
				model.setEntity(domainEntity);
			} catch (EntityParsingError entityParsingError) {
				errorsCount++;
				// File entityFile = new File(entityFileName);
				// entitiesErrors.put(entityFile.getName(),
				// parsingException.getMessage() );
				// entityErrors.add(entityParsingError);
				model.addError(entityParsingError);
			}
		}
		if (errorsCount == 0) {
			return model;
		} else {
			throw new ModelParsingError(file, "Parsing error(s) : " + errorsCount + " invalid entity(ies) ");
		}
	}

	/**
	 * Check model file validity
	 * 
	 * @param file
	 * @throws ModelParsingError
	 */
	protected void checkModelFile(File file) throws ModelParsingError {
		if (!file.exists()) {
			String error = "File '" + file.toString() + "' not found";
			throw new ModelParsingError(file, error);
		}
		if (!file.isFile()) {
			String error = "'" + file.toString() + "' is not a file";
			throw new ModelParsingError(file, error);
		}
		if (!file.getName().endsWith(DOT_MODEL)) {
			String error = "File '" + file.toString() + "' doesn't end with '" + DOT_MODEL + "'";
			throw new ModelParsingError(file, error);
		}
	}

	/**
	 * Parse the given ENTITY file name
	 * 
	 * @param entityFileName
	 * @param entitiesNames
	 * @return
	 * @throws EntityParsingError
	 */
	public final DomainEntity parseEntity(String entityFileName, List<String> entitiesNames) throws EntityParsingError {
		File entityFile = new File(entityFileName);
		return parseEntity(entityFile, entitiesNames);
	}

	/**
	 * Parse the given ENTITY file
	 * 
	 * @param file
	 * @param entitiesNames
	 * @return
	 * @throws EntityParsingError
	 */
	public final DomainEntity parseEntity(File file, List<String> entitiesNames) throws EntityParsingError {

		EntityFileParser entityFileParser = new EntityFileParser(file);
		EntityFileParsingResult result = entityFileParser.parse();
		String entityNameFromFileName = result.getEntityNameFromFileName();
		String entityNameParsed = result.getEntityNameParsed();
		// check entity name
		if (!entityNameFromFileName.equals(entityNameParsed)) {
//			// try to convert to UTF-8
//			String entityNameFromFileNameUTF8 = toUTF8(entityNameFromFileName);
//			if (!entityNameFromFileNameUTF8.equals(entityNameParsed)) {
//				String defaultCharset = Charset.defaultCharset().toString();
//				throw new EntityParsingError(entityNameFromFileName, "Entity name '" + entityNameParsed
//						+ "' different from file name" + " (default charset = " + defaultCharset + ")");
//			}
			if ( composedOfStandardAsciiCharacters(entityNameFromFileName) ) {
				throw new EntityParsingError(entityNameFromFileName, "Entity name '" + entityNameParsed
						+ "' different from file name '" + entityNameFromFileName +"' ");
			}
		}
		// check number of fields defined
		if (result.getFields().isEmpty()) {
			throw new EntityParsingError(entityNameFromFileName, "no field defined");
		}
		ParserLogger.log("\n----------");
		DomainEntity domainEntity = new DomainEntity(entityNameFromFileName);
		for (FieldParts field : result.getFields()) {
			DomainField domainField = null;
			// Try to parse field
			try {
				domainField = parseField(entityNameFromFileName, field, entitiesNames);
			} catch (FieldParsingError e) {
				domainEntity.addError(e); // Cannot parse field name and type
				domainField = null;
			}
			// If field parsing OK
			if (domainField != null) {
				// Add field if not already defined
				if (domainEntity.hasField(domainField)) {
					// Already defined => Error
					domainEntity.addError(new FieldNameAndTypeError(entityNameFromFileName, domainField.getName(),
							"field defined more than once"));
				} else {
					domainEntity.addField(domainField);
				}
			}
		}
		return domainEntity;
	}

	private String toUTF8(String name) {
//		try {
//			byte[] bytes = name.getBytes(Charset.defaultCharset());
//			return new String(bytes, StandardCharsets.UTF_8);
//		} catch (UnsupportedEncodingException e) {
//			return name; // no change !
//		}
		byte[] bytes = name.getBytes(Charset.defaultCharset());
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	private boolean composedOfStandardAsciiCharacters(String s) {
		byte[] bytes = s.getBytes();
		for ( byte b : bytes ) {
			if( b < 32 || b > 126 ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Parse the given RAW FIELD
	 * 
	 * @param entityNameFromFileName
	 * @param field
	 */
	protected final DomainField parseField(String entityNameFromFileName, FieldParts field, List<String> entitiesNames)
			throws FieldNameAndTypeError, AnnotationOrTagError {

		// 1) Parse the field NAME and TYPE
		FieldNameAndTypeParser parser = new FieldNameAndTypeParser(entityNameFromFileName, entitiesNames);
		FieldNameAndType fieldNameAndType = parser.parseFieldNameAndType(field); // throws
																					// FieldNameAndTypeError
		ParserLogger.log("Field : name '" + fieldNameAndType.getName() + "' type '" + fieldNameAndType.getType()
				+ "' cardinality = " + fieldNameAndType.getCardinality());

		String fieldName = fieldNameAndType.getName();

		// --- New "DomainField" instance
		DomainField domainField = new DomainField(field.getLineNumber(), fieldName, fieldNameAndType.getDomainType(),
				fieldNameAndType.getCardinality());

		// 2) Parse field ANNOTATIONS and TAGS
		FieldAnnotationsAndTagsParser fieldAnnotationsAndTagsParser = new FieldAnnotationsAndTagsParser(
				entityNameFromFileName);
		FieldAnnotationsAndTags fieldAnnotationsAndTags = fieldAnnotationsAndTagsParser.parse(fieldName, field);
		// List<DomainAnnotationOrTag> annotationsAndTagsList =
		// fieldAnnotationsAndTags.getAnnotations();
		// System.out.println("\n--- ANNOTATIONS and TAGS : " +
		// annotationsAndTagsList.size() );
		// for ( DomainAnnotationOrTag annotationOrTag : annotationsAndTagsList
		// ) {
		// if ( annotationOrTag instanceof DomainTag ) {
		// System.out.println(" . TAG : " + annotationOrTag );
		// //--- Add a new TAG
		// domainField.addTag((DomainTag)annotationOrTag);
		// }
		// else if ( annotationOrTag instanceof DomainAnnotation ) {
		// System.out.println(" . ANNOTATION : " + annotationOrTag );
		// //--- Add a new ANNOTATION
		// domainField.addAnnotation((DomainAnnotation)annotationOrTag);
		// }
		// }

		// Errors found
		for (AnnotationOrTagError error : fieldAnnotationsAndTags.getErrors()) {
			domainField.addError(error);
		}

		// Annotations found
		for (DomainAnnotation annotation : fieldAnnotationsAndTags.getAnnotations()) {
			if (domainField.hasAnnotation(annotation)) {
				// Already defined => Error
				domainField.addError(new AnnotationOrTagError(entityNameFromFileName, fieldName, annotation.getName(),
						"annotation defined more than once"));
			} else {
				domainField.addAnnotation(annotation);
			}
		}

		// Tags found
		for (DomainTag tag : fieldAnnotationsAndTags.getTags()) {
			if (domainField.hasTag(tag)) {
				// Already defined => Error
				domainField.addError(new AnnotationOrTagError(entityNameFromFileName, fieldName, tag.getName(),
						"tag defined more than once"));
			} else {
				domainField.addTag(tag);
			}
		}

		ParserLogger.log("--- ");
		return domainField;
	}
}
