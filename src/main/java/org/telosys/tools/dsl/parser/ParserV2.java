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

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.commons.ModelInfo;
import org.telosys.tools.dsl.commons.ModelInfoLoader;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainModel;

/**
 * DSL model parser (version 2) 
 * 
 * @author Laurent GUERIN
 *
 */
public class ParserV2 {

	/**
	 * Constructor
	 */
	public ParserV2() {
		super();
	}
	
//	/**
//	 * Loads model information from the given YAML file if the file exists
//	 * @param modelYamlFile
//	 * @return model
//	 */
//	private ModelInfo loadModelInformation(File modelYamlFile) {
//		if ( modelYamlFile.exists() && modelYamlFile.isFile() ) {
//			YamlFileManager yaml = new YamlFileManager();
//			return yaml.load(modelYamlFile, ModelInfo.class);
//		}
//		else {
//			return new ModelInfo() ;
//		}
//	}

	/**
	 * Parse the MODEL located in the given model folder name
	 * @param modelFolderName
	 * @return
	 */
	public ParsingResult parseModel(String modelFolderName) {
		return parseModel(new File(modelFolderName));
	}
	
	/**
	 * Parse the MODEL located in the given folder
	 * @param modelFolder model folder (e.g. "/aa/bb/cc/modelname" )
	 * @return
	 */
	public ParsingResult parseModel(File modelFolder) {

		DslModelErrors errors = new DslModelErrors();
		
		//--- check model folder validity
		try {
			checkModelFolder(modelFolder);
		} catch (DslModelError e) {
			errors.addError(e);
			return new ParsingResult(null, errors);
		}

		//--- load model info file if any
		File modelInfoFile = DslModelUtil.getModelFileFromModelFolder(modelFolder);
		ModelInfo modelInfo = ModelInfoLoader.loadModelInformation(modelInfoFile);
		
		//--- create new model
		DomainModel model = new DomainModel(modelFolder.getName(), modelInfo);

		//--- build list of entities names in the model
		List<String> entitiesFileNames = DslModelUtil.getEntityFullFileNames(modelFolder);
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
	 * Check model folder validity
	 * @param modelFolder
	 * @throws DslModelError
	 */
	private void checkModelFolder(File modelFolder) throws DslModelError {
		if (!modelFolder.exists()) {
			String error = "Model folder '" + modelFolder.toString() + "' not found";
			throw new DslModelError(error);
		}
		if (!modelFolder.isDirectory()) {
			String error = "'" + modelFolder.toString() + "' is not a directory";
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
