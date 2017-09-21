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
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainModel;

/**
 * DSL model parser <br>
 * The model structure is : <br>
 * . foo.model : the model file (properties file) <br>
 * . foo_model : the model folder containing all the entities  <br>
 * . foo_model/Country.entity <br>
 * . foo_model/Employee.entity <br>
 * . etc <br>
 *
 */
public class DomainModelParser {

	/*
	 * Entities files with errors 
	 * Key   : entity absolute file name 
	 * Value : parsing error
	 */
	private final Hashtable<String,String> entitiesErrors = new Hashtable<String,String>();
	
    /**
     * Parse the given model file
     *
     * @param file the ".model" file 
     * @return
     */
    public final DomainModel parse(File file) {
    	ParserUtil.checkModelFile(file);
    	return parseModelFile(file);
    }
    
    public Hashtable<String,String> getErrors() {
    	return entitiesErrors ;
    }
    
//    private final DomainModel parseModelFile(File file, String modelName) {
    private final DomainModel parseModelFile(File file) {

    	//String modelName = ParserUtil.getModelName(file) ;
    	
//        //Properties properties = loadProperties(file);
//        Properties properties = DslModelUtil.loadModelProperties(file);
        PropertiesManager propertiesManager = new PropertiesManager(file);
        Properties properties = propertiesManager.load();
        
//        DomainModel model = new DomainModel(modelName, properties);
        DomainModel model = new DomainModel(properties);
        
//        // ENUMERATIONS ( .enum files )
//        List<String> enumerations = files.get(DOT_ENUM);
//        EnumerationParser enumParser = new EnumerationParser();
//        for (String enumeration : enumerations) {
//            model.addEnumeration(enumParser.parse(new File(enumeration)));
//        }

        // ENTITIES ( all the ".entity" files located in the model folder)
//        List<String> entitiesFileNames = getEntitiesAbsoluteFileNames(file, modelName);
        List<String> entitiesFileNames = DslModelUtil.getEntitiesAbsoluteFileNames(file);
        
        //--- Step 1 : build void entities in the model
        for (String entityFileName : entitiesFileNames) {
            //File entityFile = new File(entity);
            String entityName = DslModelUtil.getEntityName(new File(entityFileName));
            model.addEntity(new DomainEntity(entityName));
        }

        //--- Step 2 : parse each entity and populate it in the model
        int errorsCount = 0 ;
        EntityParser entityParser = new EntityParser(model);
        for (String entityFileName : entitiesFileNames) {
        	//--- Parse
        	DomainEntity domainEntity;
			try {
				domainEntity = entityParser.parse(entityFileName);
	        	//--- Populate
	            model.populateEntityFileds(domainEntity.getName(), domainEntity.getFields() );
			} catch (EntityParserException parsingException) {
				errorsCount++ ;
				File entityFile = new File(entityFileName);
				entitiesErrors.put(entityFile.getName(), parsingException.getMessage() );
			}
        }
        if ( errorsCount == 0 ) {
            return model;
        }
        else {
        	throw new EntityParserException( "Parsing error(s) : " + errorsCount + " invalid entity(ies) ") ;
        }
    }

    public List<String> getEntitiesAbsoluteFileNames(File modelFile) {
    	ParserUtil.checkModelFile(modelFile);
    	return DslModelUtil.getEntitiesAbsoluteFileNames(modelFile);
    }
}
