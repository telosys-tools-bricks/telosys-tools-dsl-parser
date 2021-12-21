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

import java.util.LinkedList;
import java.util.List;

/**
 * Entity Parsing Error
 * 
 * @author Laurent GUERIN
 *
 */
public class EntityParsingError extends ParsingError {

    private static final long serialVersionUID = 1L;
    
	// Fields errors
	private final List<ParsingError> errors ;

    /**
     * Entity error due to entity level error 
     * @param entityName
     * @param errorMessage
     */
    public EntityParsingError(String entityName, String errorMessage) {
        super(entityName, errorMessage);
    	// Fields errors
		this.errors = new LinkedList<>() ; // no fields errors 
    }

    /**
     * Entity error due to entity level error
     * @param entityName
     * @param errorMessage
     * @param lineNumber
     */
    public EntityParsingError(String entityName, String errorMessage, int lineNumber) {
        super(entityName, lineNumber, errorMessage);
    	// Fields errors
		this.errors = new LinkedList<>() ; // no fields errors 
    }
    
    /**
     * Entity error due to 1 to N invalid fields
     * @param entityName
     * @param fieldsErrors
     */
    public EntityParsingError(String entityName, List<ParsingError> fieldsErrors) {
        super(entityName, fieldsErrors.size() + " field error(s)" );
        // Only fields errors
   		this.errors = fieldsErrors ;
    }

    public List<ParsingError> getErrors() {
    	return errors ;
    }
    
    public int getErrorsCount() {
    	if ( errors != null && ! errors.isEmpty() ) {
    		return errors.size(); // 1 .. N 
    	}
    	else {
    		return 1 ; // this single entity error
    	}
    }
}
