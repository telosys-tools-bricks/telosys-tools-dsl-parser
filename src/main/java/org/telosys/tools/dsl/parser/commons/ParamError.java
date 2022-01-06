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
package org.telosys.tools.dsl.parser.commons;

/**
 * DSL Parser Exception 
 * 
 * @author Laurent GUERIN
 *
 */
public class ParamError extends Exception {

    private static final long serialVersionUID = 1L;
    
//    // Entity name (if known)
//	private final String entityName;
//
//    // Field name (if known)
//	private final String fieldName;
//
//	private final String annotationOrTagName;
//	
//    // Standard exception message
//	private final String errorMessage ;
//
//    /**
//	 * ENTITY level error 
//     */
//    public ParamError(String entityName, String annotationOrTagName, String errorMessage) {
//    	this(entityName, null, annotationOrTagName, errorMessage);
//    }
//    
//    /**
//	 * FIELD level error 
//     */
//    public ParamError(String entityName, String fieldName, String annotationOrTagName, String errorMessage) {
//    	this.entityName = entityName ;
//    	this.fieldName = fieldName ;
//    	this.annotationOrTagName = annotationOrTagName;
//    	this.errorMessage = errorMessage ;
//    }
    
    public ParamError(String errorMessage) {
    	super(errorMessage);
    }
}

