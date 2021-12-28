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
package org.telosys.tools.dsl.parser2;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.parser.ParserFKChecker;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainModel;

public class ParserV2 {

	private static final String DOT_MODEL = ".model";

	/**
	 * Constructor
	 */
	public ParserV2() {
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
	public ParsingResult parseModel(File file) { // throws ModelParsingError {

		ParsingErrors errors = new ParsingErrors();
		
		//--- Step 0 : check model file validity
		try {
			checkModelFile(file);
		} catch (ParsingError e) {
			errors.addError(e);
			return new ParsingResult(null, errors);
		}

		//--- Step 1 : init a void model using the ".model" file
		PropertiesManager propertiesManager = new PropertiesManager(file);
		Properties properties = propertiesManager.load();
		DomainModel model = new DomainModel(file.getName(), properties);

		List<String> entitiesFileNames = DslModelUtil.getEntitiesAbsoluteFileNames(file);
		List<String> entitiesNames = new LinkedList<>();

//		//--- Step 2 : build VOID entities (1 instance for each entity defined
//		// in the model)
//		for (String entityFileName : entitiesFileNames) {
//			String entityName = DslModelUtil.getEntityName(new File(entityFileName));
//			entitiesNames.add(entityName);
//			model.addEntity(new DomainEntity(entityName)); // Useful ???
//		}

		//--- build list of entities names
		// in the model)
		for (String entityFileName : entitiesFileNames) {
			entitiesNames.add(DslModelUtil.getEntityName(new File(entityFileName)));
		}

		//--- Step 3 : for each entity parse the file and populate it in the model
//		List<EntityParsingError> errors = new LinkedList<>();
//		List<ParsingError> errors = new LinkedList<>();
		for (String entityFileName : entitiesFileNames) {
			// --- Parse
			DomainEntity domainEntity = parseEntity(entityFileName, entitiesNames, errors);
//			try {
//				domainEntity = parseEntity(entityFileName, entitiesNames, errors);
//				if ( domainEntity.hasError() ) {
//					errors.addError(new EntityParsingError(domainEntity.getName(), domainEntity.getErrors() ) );
//				}
//				//--- Replace VOID ENTITY by REAL ENTITY
//				model.setEntity(domainEntity);
				//--- Replace VOID ENTITY by REAL ENTITY
			if ( domainEntity != null ) {
				model.setEntity(domainEntity);
			}
//			} catch (ParsingError entityParsingError) {
//				// Cannot parse entity 
//				errors.addError(entityParsingError);
//			}
		}
		
		//--- Step 4 : search duplicated FK names in the model
		ModelFKChecker modelFKChecker = new ModelFKChecker();
		modelFKChecker.checkNoDuplicateFK(model, errors);
		
		return new ParsingResult(model, errors);
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
			throw new ModelParsingError(error);
		}
		if (!file.isFile()) {
			String error = "'" + file.toString() + "' is not a file";
			throw new ModelParsingError(error);
		}
		if (!file.getName().endsWith(DOT_MODEL)) {
			String error = "File '" + file.toString() + "' doesn't end with '" + DOT_MODEL + "'";
			throw new ModelParsingError(error);
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
	protected DomainEntity parseEntity(String entityFileName, List<String> entitiesNames, ParsingErrors errors) { //throws ParsingError {
		return parseEntity(new File(entityFileName), entitiesNames, errors);
	}

	/**
	 * Parse the given ENTITY file
	 * 
	 * @param entityFile
	 * @param entitiesNames
	 * @return the new entity (or null if cannot be parsed)
	 * @throws EntityParsingError
	 */
	protected DomainEntity parseEntity(File entityFile, List<String> entitiesNames, ParsingErrors errors) { //throws ParsingError {

		String entityNameFromFileName = DslModelUtil.getEntityName(entityFile);

		//--- Parse elements
		EntityElementsParser elementsParser = new EntityElementsParser(entityFile);
		List<Element> elements;
		try {
			elements = elementsParser.parseElements();
		} catch (ParsingError e) {
			errors.addError(e);
			return null;
		}

		//--- Process elements
		EntityElementsProcessor elementsProcessor = new EntityElementsProcessor(entityNameFromFileName, entitiesNames);
		return elementsProcessor.processEntityElements(elements, errors);
		
	}
	
}
