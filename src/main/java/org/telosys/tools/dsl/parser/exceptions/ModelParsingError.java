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
 * Model Parsing Error
 * 
 * @author Laurent GUERIN
 *
 */
public class ModelParsingError extends ParsingError {

    private static final long serialVersionUID = 1L;
    
	private final List<EntityParsingError> errors ;

	/**
	 * Constructor with list of errors
	 * @param errors
	 */
	public ModelParsingError(List<EntityParsingError> errors) {
        super( (errors != null ? errors.size() : 0  ) + " parsing error(s)" ) ;
		this.errors = errors ;
    }

    /**
     * Constructor with a single error message
     * @param errorMessage
     */
    public ModelParsingError(String errorMessage) {
        super(errorMessage);
		this.errors = new LinkedList<>() ; // void list
    }

    public List<EntityParsingError> getEntitiesErrors() {
    	return errors ;
    }
}
