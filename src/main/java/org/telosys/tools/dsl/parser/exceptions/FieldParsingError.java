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

/**
 * DSL Parser Exception 
 * 
 * @author Laurent GUERIN
 *
 */
public abstract class FieldParsingError extends Exception {

    private static final long serialVersionUID = 1L;
    
	private final String entityName;
	private final String error ;
	private final String detailMessage ;

    public FieldParsingError(String entityName, String error, String detailMessage) {
        super();
        this.entityName = entityName ;
        this.error = error;
		this.detailMessage = detailMessage;
    }
    
    public String getEntityName() {
		return entityName;
	}

	public String getError() {
		return error;
	}

	@Override
    public String getMessage() {
        return detailMessage;
    }
    
}
