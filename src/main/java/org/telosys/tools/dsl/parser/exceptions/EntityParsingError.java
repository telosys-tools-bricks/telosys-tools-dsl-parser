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
public class EntityParsingError extends Exception {

    private static final long serialVersionUID = 1L;
    
	private final String entityName;
	private final int lineNumber;
	private final String message ;
    private final List<FieldParsingError> fieldsErrors ;

    public EntityParsingError(String entityName, String error) {
        super();
        this.entityName = entityName ;
        this.lineNumber = 0 ;
		this.message = "entity '" + entityName + "' : " + error ;
		this.fieldsErrors = new LinkedList<>() ;
    }

    public EntityParsingError(String entityName, String error, int lineNumber) {
        super();
        this.entityName = entityName ;
        this.lineNumber = lineNumber ;
		this.message = entityName + " : " + error + "(line " + lineNumber + ")";
		this.fieldsErrors = new LinkedList<>() ;
    }
    
    public EntityParsingError(String entityName, String error, List<FieldParsingError> fieldsErrors) {
        super();
        this.entityName = entityName ;
        this.lineNumber = 0 ;
		this.message = "entity '" + entityName + "' : " + error ;
		this.fieldsErrors = fieldsErrors ;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
    public String getEntityName() {
    	return entityName;
    }

    public int getLineNumber() {
    	return lineNumber;
    }
    
    public List<FieldParsingError> getFieldsErrors() {
    	return fieldsErrors ;
    }
    
    public int getErrorsCount() {
    	return fieldsErrors.size();
    }
}
