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
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.ParsingResult;
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

//	public Map<String, List<String>> getErrorsMap() {
//		return errors.getAllErrorsMap();
//	}
//

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
     * @param modelFile the ".model" file to be loaded
     * @return the generic model or null if errors detected during parsing 
     */
    public Model loadModel(File modelFile) {
//    	try {
//			DomainModel domainModel = step1ParseModel(modelFile);
//			if ( domainModel != null ) {
//				return step2ConvertModel(domainModel);
//			}
//		} catch (Exception e) {
//			// Unexpected exception
//			String msg = e.getMessage();
//			if ( msg == null ) { // eg NullPointerException
//				msg = e.toString();
//			}
//			parsingErrorMessage = "Converter error : " + msg ;
//		}
//		return null ;
		ParsingResult parsingResult = step1ParseModel(modelFile);
		if ( parsingResult.hasErrors() ) {
			this.errors = parsingResult.getErrors();
			this.parsingErrorMessage = parsingResult.getErrors().getNumberOfErrors() + " parsing error(s)";
			return null ;
		}
		else {
			// Parsing is OK => convert model
			return step2ConvertModel(parsingResult.getModel());
		}

    }
    
    /**
     * Parse the model files to create the 'raw model'
     * @param modelFile
     * @return
     */
    private ParsingResult step1ParseModel(File modelFile) {
//        Parser dslParser = new Parser();
//		try {
//			// Try to parse the DSL model
//			return dslParser.parseModel(modelFile);
//		} catch (ModelParsingError modelParsingError) {
//        	// Parsing ERRORS 
//			// Keep errors information
//        	parsingErrorMessage = modelParsingError.getMessage();
//        	errors = new DslModelErrors(modelParsingError.getEntitiesErrors());
//        	// Invalid model => return null
//            return null;
//		}
    	ParserV2 dslParser = new ParserV2();
		return dslParser.parseModel(modelFile);
    }
    
    /**
     * Convert the 'raw model' to 'DSL/generic model'
     * @param domainModel
     * @return
     */
    private Model step2ConvertModel(DomainModel domainModel) {
        ModelConverter converter = new ModelConverter(errors);
		try {
			Model model = converter.convertModel(domainModel);
			if ( errors.isEmpty() ) {
				return model ; // Model is OK
			}
			else {
				return null ; // Invalid model
			}
		} catch (Exception e) {
			// Unexpected exception
			String msg = e.getMessage();
			if ( msg == null ) { // eg NullPointerException
				msg = e.toString();
			}
			parsingErrorMessage = "Converter error : " + msg ;
			return null ;
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
