/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainFK;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainTag;

public class Parser {

	private static final String DOT_MODEL = ".model";

	/**
	 * Constructor
	 */
	public Parser() {
		super();
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
	 * Parse the MODEL identified by the ".model" file
	 * 
	 * @param file
	 *            the model file ( file with ".model" suffix )
	 * @return
	 * @throws ModelParsingError
	 */
	public DomainModel parseModel(File file) throws ModelParsingError {

		//--- Step 0 : check model file validity
		checkModelFile(file);

		//--- Step 1 : init a void model using the ".model" file
		PropertiesManager propertiesManager = new PropertiesManager(file);
		Properties properties = propertiesManager.load();
		DomainModel model = new DomainModel(file.getName(), properties);

		List<String> entitiesFileNames = DslModelUtil.getEntitiesAbsoluteFileNames(file);
		List<String> entitiesNames = new LinkedList<>();

		//--- Step 2 : build VOID entities (1 instance for each entity defined
		// in the model)
		for (String entityFileName : entitiesFileNames) {
			String entityName = DslModelUtil.getEntityName(new File(entityFileName));
			entitiesNames.add(entityName);
			model.addEntity(new DomainEntity(entityName));
		}

		//--- Step 3 : parse each entity and populate it in the model
		List<EntityParsingError> errors = new LinkedList<>();
		for (String entityFileName : entitiesFileNames) {
			// --- Parse
			DomainEntity domainEntity;
			try {
				domainEntity = parseEntity(entityFileName, entitiesNames);
				if ( domainEntity.hasError() ) {
					//int n = domainEntity.getErrors().size() ;
					//errors.add(new EntityParsingError(domainEntity.getName(), n + " error(s)", domainEntity.getErrors() ) );
					errors.add(new EntityParsingError(domainEntity.getName(), domainEntity.getErrors() ) );
				}
				//--- Replace VOID ENTITY by REAL ENTITY
				model.setEntity(domainEntity);
			} catch (EntityParsingError entityParsingError) {
				errors.add(entityParsingError);
			}
		}
		
		//--- Step 4 : search duplicated FK names in the model
		ParserFKChecker parserFKChecker = new ParserFKChecker();
		parserFKChecker.checkNoDuplicateFK(model, errors);
		
		if ( ! errors.isEmpty()) {
			String msg = errors.size() + " parsing error(s)" ;
			throw new ModelParsingError(file, msg, errors);
		} else {
			return model;
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
	public DomainEntity parseEntity(String entityFileName, List<String> entitiesNames) throws EntityParsingError {
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
	public DomainEntity parseEntity(File file, List<String> entitiesNames) throws EntityParsingError {

		EntityFileParser entityFileParser = new EntityFileParser(file);
		EntityFileParsingResult result = entityFileParser.parse();
		String entityNameFromFileName = result.getEntityNameFromFileName();
		String entityNameParsed = result.getEntityNameParsed();
		// check entity name
		if (!entityNameFromFileName.equals(entityNameParsed)) {
			// if the file name contains NON ASCII characters the difference can be due to charset conversion 
			if ( composedOfStandardAsciiCharacters(entityNameFromFileName) ) {
				// ASCII only => really different names
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
//			DomainField domainField = null;
//			// Try to parse field
//			try {
//				domainField = parseField(entityNameFromFileName, field, entitiesNames);
//			} catch (FieldParsingError e) {
//				domainEntity.addError(e); // Cannot parse field name and type
//				domainField = null;
//			}
//			// If field parsing OK
//			if (domainField != null) {
//				// Add field if not already defined
//				if (domainEntity.hasField(domainField)) {
//					// Already defined => Error
//					domainEntity.addError(new FieldNameAndTypeError(entityNameFromFileName, domainField.getName(),
//							"field defined more than once"));
//				} else {
//					domainEntity.addField(domainField);
//				}
//				// If fiels has errors : store these errors at entity level 
//				if ( domainField.hasErrors() ) {
//					for ( AnnotationOrTagError fieldError : domainField.getErrors() ) {
//						domainEntity.addError(fieldError); 
//					}
//				}
//			}
			parseField(domainEntity, field, entitiesNames );
		}
		if ( domainEntity.hasError() ) {
//			String msg = domainEntity.getErrors().size() + " error(s)" ;
//			throw new EntityParsingError(domainEntity.getName(), msg, domainEntity.getErrors() );
			throw new EntityParsingError(domainEntity.getName(), domainEntity.getErrors() );
		}
		return domainEntity;
	}
	
	protected void parseField(DomainEntity domainEntity, FieldParts fieldParts, List<String> entitiesNames ) {
		
		// Try to parse field
		DomainField domainField = null;
		try {
			domainField = parseField(domainEntity.getName(), fieldParts, entitiesNames);
		} catch (FieldParsingError e) {
			domainEntity.addError(e); // Cannot parse field name and type
			return;
		}
		
		// Add field if not already defined
		if (domainEntity.hasField(domainField)) {
			// Already defined => Error
			domainEntity.addError(new FieldParsingError(domainEntity.getName(), domainField.getName(),
					"field defined more than once"));
			return;
		} else {
			domainEntity.addField(domainField);
		}

		// If fiels has errors : store these errors at entity level 
		if ( domainField.hasErrors() ) {
			for ( AnnotationOrTagError fieldError : domainField.getErrors() ) {
				domainEntity.addError(fieldError); 
			}
		}
	}
	
	/**
	 * Parse the given RAW FIELD
	 * @param entityName
	 * @param fieldParts
	 * @param allEntitiesNames
	 * @return
	 * @throws FieldParsingError
	 */
	protected DomainField parseField(String entityName, FieldParts fieldParts, List<String> allEntitiesNames)
			throws FieldParsingError {

		// 1) Parse the field NAME and TYPE
		FieldNameAndTypeParser parser = new FieldNameAndTypeParser(entityName, allEntitiesNames);
		FieldNameAndType fieldNameAndType = parser.parseFieldNameAndType(fieldParts); // throws
																					// FieldNameAndTypeError
		ParserLogger.log("Field : name '" + fieldNameAndType.getName() + "' type '" + fieldNameAndType.getType()
				+ "' cardinality = " + fieldNameAndType.getCardinality());

		String fieldName = fieldNameAndType.getName();

		// --- New "DomainField" instance
		DomainField domainField = new DomainField(fieldParts.getLineNumber(), fieldName, fieldNameAndType.getDomainType(),
				fieldNameAndType.getCardinality());

		// 2) Parse field ANNOTATIONS and TAGS
		FieldAnnotationsAndTagsParser fieldAnnotationsAndTagsParser = new FieldAnnotationsAndTagsParser(
				entityName);
		FieldAnnotationsAndTags fieldAnnotationsAndTags = fieldAnnotationsAndTagsParser.parse(fieldName, fieldParts);

		// Errors found
		for (AnnotationOrTagError error : fieldAnnotationsAndTags.getErrors()) {
			domainField.addError(error);
		}

		// Process all ANNOTATIONS found for this field
		for ( DomainAnnotation annotation : fieldAnnotationsAndTags.getAnnotations() ) {
			if ( AnnotationName.FK.equals( annotation.getName() ) ) { // v 3.3.0
				// Special processing for "@FK" annotation (can be used 1..N times in a field )
				try {
					FieldFKAnnotationParser fkParser = new FieldFKAnnotationParser(entityName, fieldName);
					DomainFK fk = fkParser.parse(annotation);
					domainField.addFKDeclaration(fk);
				} catch (AnnotationOrTagError err) {
					domainField.addError(err);
				}
			}
			else {
				// Standard processing for other annotations
				if (domainField.hasAnnotation(annotation)) {
					// Already defined => Error
					domainField.addError(new AnnotationOrTagError(entityName, fieldName, "@"+annotation.getName(),
							"annotation defined more than once"));
				} else {
					domainField.addAnnotation(annotation);
				}
			}
		}

		// Process all TAGS found for this field
		for ( DomainTag tag : fieldAnnotationsAndTags.getTags() ) {
			if (domainField.hasTag(tag)) {
				// Already defined => Error
				domainField.addError(new AnnotationOrTagError(entityName, fieldName, "#"+tag.getName(),
						"tag defined more than once"));
			} else {
				domainField.addTag(tag);
			}
		}

		ParserLogger.log("--- ");
		return domainField;
	}
}
