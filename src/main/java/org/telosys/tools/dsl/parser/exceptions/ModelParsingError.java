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
package org.telosys.tools.dsl.parser.exceptions;

import java.io.File;
import java.util.List;

/**
 * Model Parsing Error
 * 
 * @author Laurent GUERIN
 *
 */
public class ModelParsingError extends Exception {

    private static final long serialVersionUID = 1L;
    
    
	private final File modelFile;
//	private final String error ;
	private final String detailMessage ;

//    public ModelParsingError(File modelFile, String errorMessage, List<EntityParsingError> entityErrors) {
//        super();
//        this.modelFile = modelFile ;
////        this.error = error;
//		this.detailMessage = modelFile.getName() + " : " + errorMessage ;
//		this.entityErrors = entityErrors ;
//    }

    public ModelParsingError(File modelFile, String errorMessage) {
        super();
        this.modelFile = modelFile ;
		this.detailMessage = modelFile.getName() + " : " + errorMessage ;
    }

    public File getModelFile() {
        return modelFile;
    }

    @Override
    public String getMessage() {
        return detailMessage;
    }
    
}
