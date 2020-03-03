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
package org.telosys.tools.dsl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.dsl.converter.Converter;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainModelInfo;
import org.telosys.tools.generic.model.Model;

public class DslModelManager {

	private String parsingErrorMessage = null;

	private Map<String, String> parsingErrors = null;

	/**
	 * Constructor
	 */
	public DslModelManager() {
		super();
		parsingErrorMessage = "";
		parsingErrors = new HashMap<>();
	}

	public Map<String, String> getParsingErrors() {
		return parsingErrors;
	}

	public String getErrorMessage() {
		return parsingErrorMessage;
	}

	/**
	 * Loads (parse) the given model file
	 * 
	 * @param modelFileAbsolutePath the ".model" absolute file name
	 * 
     * @return the model or null if errors detected during parsing 
	 */
	public Model loadModel(String modelFileAbsolutePath) {
		return loadModel(new File(modelFileAbsolutePath));
	}

	/**
     * Loads (parse and convert) the given model file <br>
     * If errors occured this method returns null <br>
     * and the errors can be retrieved from this instance ( parsingErrorMessage and parsingErrors ) 
     *
     * @param modelFile the ".model" file 
     * 
     * @return the generic model or null if errors detected during parsing 
     */
    public Model loadModel(File modelFile) {
    	
        //--- 1) Parse the model 
        Parser dslParser = new Parser();
        DomainModel domainModel = null ;
        ModelParsingError modelParsingError = null ;
		try {
			domainModel = dslParser.parseModel(modelFile);
		} catch (ModelParsingError e) {
			modelParsingError = e ;
		}
		
        if ( modelParsingError != null ) {
        	//--- 2) Keep error information
        	parsingErrorMessage = modelParsingError.getMessage();
        	buildErrorsMap(modelParsingError.getEntitiesErrors());
            return null;
        }
        else {
//        	if ( domainModel.hasError() ) {
//            	parsingErrorMessage = "Parsing error(s) : " + domainModel.getErrors().size() + " invalid entity(ies) ";
//
//            	buildErrorsMap(domainModel.getErrors()); 
//                return null;
//        	}
//        	else {
                //--- 2) Convert the "domain model" to "generic model" 
                Converter converter = new Converter();
    			try {
    				return converter.convertToGenericModel(domainModel);
    			} catch (Exception e) {
    				parsingErrorMessage = "Converter error : " + e.getMessage() ;
//    				parsingErrors = new Hashtable<>();
//    				parsingErrors.put("", parsingErrorMessage );
    				return null ;
    			}
//        	}
        }
    }

    private void buildErrorsMap(List<EntityParsingError> errors) {
    	for ( EntityParsingError err : errors ) {
    		parsingErrors.put(err.getEntityName(), err.getMessage());
    	}
    }
    
	/**
	 * Loads the model information from the given file
	 * 
	 * @param modelFile  the ".model" file
	 * @return
	 */
	public DomainModelInfo loadModelInformation(File modelFile) {
		PropertiesManager propertiesManager = new PropertiesManager(modelFile);
		Properties properties = propertiesManager.load();

		return new DomainModelInfo(properties);
	}

	/**
	 * Saves the model information in the given file
	 * 
	 * @param modelFile  the ".model" file
	 * @param domainModelInfo
	 */
	public void saveModelInformation(File modelFile, DomainModelInfo domainModelInfo) {
		PropertiesManager propertiesManager = new PropertiesManager(modelFile);
		propertiesManager.save(domainModelInfo.getProperties());
	}
}
