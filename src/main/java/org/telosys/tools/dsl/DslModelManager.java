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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.dsl.converter.ModelConverter;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainModelInfo;
import org.telosys.tools.generic.model.Model;

/**
 * DSL Model manager 
 * 
 * @author Laurent Guerin
 *
 */
public class DslModelManager {

	private String parsingErrorMessage = null;

	/**
	 * All errors detected during model loading
	 */
	private DslModelErrors errors ;

	/**
	 * Constructor
	 */
	public DslModelManager() {
		super();
		parsingErrorMessage = "";
		errors = new DslModelErrors();
	}

	/**
	 * Returns main error message (or void if no error)
	 * @return
	 */
	public String getErrorMessage() {
		return parsingErrorMessage;
	}

	/**
	 * Returns object containing all errors 
	 * @return
	 */
	public DslModelErrors getErrors() {
		return errors;
	}

	public Map<String, List<String>> getErrorsMap() {
		return errors.getAllErrorsMap();
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
//        ParsingError modelParsingError = null ;
		try {
			domainModel = dslParser.parseModel(modelFile);
		} catch (ModelParsingError e) {
//		} catch (ParsingError e) {
			modelParsingError = e ;
		}
		
        if ( modelParsingError != null ) {
        	//--- 2) Parsing ERRORS => Keep errors information
        	parsingErrorMessage = modelParsingError.getMessage();
        	errors = new DslModelErrors(modelParsingError.getEntitiesErrors());
            return null;
        }
        else {
            //--- 2) Parsing OK : Convert the "domain model" to "generic model" 
            ModelConverter converter = new ModelConverter();
			try {
				return converter.convertModel(domainModel);
			} catch (Exception e) {
				String msg = e.getMessage();
				if ( msg == null ) { // eg NullPointerException
					msg = e.toString();
				}
				parsingErrorMessage = "Converter error : " + msg ;
				return null ;
			}
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
