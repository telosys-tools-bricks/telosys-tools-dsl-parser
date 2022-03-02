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

import org.telosys.tools.dsl.commons.ModelInfo;
import org.telosys.tools.dsl.commons.ModelInfoLoader;
import org.telosys.tools.dsl.converter.ModelConverter;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.ParsingResult;
import org.telosys.tools.dsl.parser.model.DomainModel;
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

	/**
	 * Loads (parse) the given model folder
	 * 
	 * @param modelFolderAbsolutePath the model folder absolute path 
	 * 
     * @return the model or null if errors detected during parsing 
	 */
	public Model loadModel(String modelFolderAbsolutePath) {
		return loadModel(new File(modelFolderAbsolutePath));
	}
    
    /**
     * Loads (parse and convert) the model located in the given folder <br>
     * If errors occured this method returns null <br>
     * and the errors can be retrieved from this instance ( parsingErrorMessage and parsingErrors ) 
     *
     * @param modelFolder  the model folder containig entity files to be loaded
     * @return the generic model or null if errors detected during parsing 
     */
    public Model loadModel(File modelFolder) {
		ParsingResult parsingResult = step1ParseModel(modelFolder);
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
     * Parse all the model entity files to create the 'raw model'
     * @param modelFolder the model folder ( folder like "/aa/bb/cc/modelname" )
     * @return
     */
    private ParsingResult step1ParseModel(File modelFolder) {
    	ParserV2 dslParser = new ParserV2();
		return dslParser.parseModel(modelFolder);
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
	 * Loads model information from the given YAML file
	 * @param modelInfoFile
	 * @return
	 */
	public ModelInfo loadModelInformation(File modelInfoFile) {
		return ModelInfoLoader.loadModelInformation(modelInfoFile);
	}

}
