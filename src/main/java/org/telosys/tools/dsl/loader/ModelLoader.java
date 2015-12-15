/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.dsl.loader;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.generic.converter.Converter;
import org.telosys.tools.dsl.parser.DomainModelParser;
import org.telosys.tools.dsl.parser.ParserUtil;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainModelInfo;
import org.telosys.tools.generic.model.Model;

public class ModelLoader {

    static Logger logger = LoggerFactory.getLogger(ModelLoader.class);

    /**
     * Loads (parse) the given model file
     * 
     * @param modelFileAbsolutePath the ".model" absolute file name 
     * @return
     */
    public Model loadModel(String modelFileAbsolutePath) {
    	return loadModel( new File(modelFileAbsolutePath) );
    }
    
    
    /**
     * Loads (parse) the given model file
     *
     * @param modelFile the ".model" file 
     * @return
     */
    public Model loadModel(File modelFile) {
    	
        //--- 1) Parse the model 
        DomainModelParser domainModelParser = new DomainModelParser();
        logger.info("\nParse model : " + modelFile.getAbsolutePath() );
        DomainModel domainModel = domainModelParser.parse(modelFile);
        logger.info("\n"+domainModel.toString());

        //--- 2) Convert the "domain model" to "generic model" 
        Converter converter = new Converter();
        Model model = converter.convertToGenericModel(domainModel);
        logger.info(model.toString());

        return model;
    }

    /**
     * Loads the model information from the model file 
     * 
     * @param modelFile the ".model" file 
     * @return
     */
    public DomainModelInfo loadModelInformation(File modelFile) {
    	Properties properties = ParserUtil.loadModelProperties(modelFile);
    	return new DomainModelInfo(properties);
    }
}
