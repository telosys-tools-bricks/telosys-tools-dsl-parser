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
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
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

	public ParsingResult parseModel(String modelFileName) {
		return parseModel(new File(modelFileName));
	}
	
	/**
	 * Parse the MODEL identified by the ".model" file
	 * 
	 * @param file
	 *            the model file ( file with ".model" suffix )
	 * @return
	 * @throws ModelParsingError
	 */
	public ParsingResult parseModel(File file) {

		DslModelErrors errors = new DslModelErrors();
		
		//--- check model file validity
		try {
			checkModelFile(file);
		} catch (DslModelError e) {
			errors.addError(e);
			return new ParsingResult(null, errors);
		}

		//--- init a void model using the ".model" file
		PropertiesManager propertiesManager = new PropertiesManager(file);
		Properties properties = propertiesManager.load();
		DomainModel model = new DomainModel(file.getName(), properties);

		//--- build list of entities names in the model
		List<String> entitiesFileNames = DslModelUtil.getEntitiesAbsoluteFileNames(file);
		List<String> entitiesNames = new LinkedList<>();
		for (String entityFileName : entitiesFileNames) {
			entitiesNames.add(DslModelUtil.getEntityName(new File(entityFileName)));
		}

		//--- for each entity parse the file and populate it in the model
		for (String entityFileName : entitiesFileNames) {
			// --- Parse
			DomainEntity domainEntity = parseEntity(entityFileName, entitiesNames, errors);
			if ( domainEntity != null ) {
				model.setEntity(domainEntity);
			}
		}
		
		//--- search duplicated FK names in the model
		ModelFKChecker modelFKChecker = new ModelFKChecker();
		modelFKChecker.checkNoDuplicateFK(model, errors);
		
		return new ParsingResult(model, errors);
	}

	/**
	 * Check model file validity
	 * @param file
	 * @throws DslModelError
	 */
	protected void checkModelFile(File file) throws DslModelError {
		if (!file.exists()) {
			String error = "File '" + file.toString() + "' not found";
			throw new DslModelError(error);
		}
		if (!file.isFile()) {
			String error = "'" + file.toString() + "' is not a file";
			throw new DslModelError(error);
		}
		if (!file.getName().endsWith(DOT_MODEL)) {
			String error = "File '" + file.toString() + "' doesn't end with '" + DOT_MODEL + "'";
			throw new DslModelError(error);
		}
	}

	/**
	 * Parse the given ENTITY file name
	 * @param entityFileName
	 * @param entitiesNames
	 * @param errors
	 * @return
	 */
	public DomainEntity parseEntity(String entityFileName, List<String> entitiesNames, DslModelErrors errors) {
		return parseEntity(new File(entityFileName), entitiesNames, errors);
	}

	/**
	 * Parse the given ENTITY file
	 * @param entityFile
	 * @param entitiesNames
	 * @param errors
	 * @return
	 */
	public DomainEntity parseEntity(File entityFile, List<String> entitiesNames, DslModelErrors errors) {

		String entityNameFromFileName = DslModelUtil.getEntityName(entityFile);

		//--- Parse elements
		EntityElementsParser elementsParser = new EntityElementsParser();
		List<Element> elements;
		try {
			elements = elementsParser.parseEntityFile(entityFile);
		} catch (DslModelError e) {
			errors.addError(e);
			return null;
		}

		//--- Process elements
		EntityElementsProcessor elementsProcessor = new EntityElementsProcessor(entityNameFromFileName, entitiesNames);
		return elementsProcessor.processEntityElements(elements, errors);
	}
	
}
