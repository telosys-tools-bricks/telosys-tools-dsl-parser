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
package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.KeyWords;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;

/**
 * Field annotations parsing
 * 
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre, Laurent Guerin
 * @version 1.0
 */
public class AnnotationParser extends AbstractParser  {

    /**
     * Constructor
     */
    public AnnotationParser() {
    	super(LoggerFactory.getLogger(AnnotationParser.class));
    }
    
    /**
     * Throws an annotation parsing exception
     * @param entityName
     * @param fieldName
     * @param annotationString
     * @param message
     */
    private void throwAnnotationParsingError(String entityName, String fieldName, String annotationString, String message) {
    	throwParsingError(entityName + "." + fieldName, "Annotation error '" + annotationString + "' (" + message + ")");
    }
    
    /**
     * Parse field annotations located between brackets : '{ @xxx, @xxx }'
     * @param entityName
     * @param fieldName
     * @param annotations the annotations string without '{' and '}'
     * @return
     */
    List<DomainEntityFieldAnnotation> parseAnnotations(String entityName, String fieldName, String annotations) {

    	if ( annotations == null || "".equals(annotations) ) {
    		// return void list
    		return new ArrayList<DomainEntityFieldAnnotation>();
    	}

        // list of annotation found
        String[] annotationList = annotations.split(",");
        // at least 1 annotation is required, if there are brackets
        if (annotationList.length == 0 ) {
        	// example : "," or ",," (only commas)
        	// not an error => return void list
        	return new ArrayList<DomainEntityFieldAnnotation>();
        }

        List<DomainEntityFieldAnnotation> list = new ArrayList<DomainEntityFieldAnnotation>();
        // extract annotations
        for (String annotationString : annotationList) {
            DomainEntityFieldAnnotation annotation = this.parseSingleAnnotation(entityName, fieldName, annotationString.trim());
            list.add(annotation);
        }

        return list;
    }

    /**
     * Parse a single annotation 
     * @param entityName
     * @param fieldName
     * @param annotationString e.g. "@Id", "@Max(12)", etc
     * @return
     */
    private DomainEntityFieldAnnotation parseSingleAnnotation(String entityName, String fieldName, String annotationString) {
    	
        // must start with a '@'
        if (annotationString.charAt(0) != '@') {
            throwAnnotationParsingError( entityName, fieldName, annotationString, "must start with '@'");
        }

        // get the annotation name 
        String annotationName = getAnnotationName(annotationString);
        if ( ! annotationName.equals( getAnnotationWithoutParameter(annotationString)) ) {
    		throwAnnotationParsingError( entityName, fieldName, annotationString, "invalid syntax" );
        }
        // get the parameter value if any 
    	String parameterValue = null ;
    	try {
			parameterValue = getParameterValue(annotationString, '(', ')' ) ;
		} catch (Exception e) {
			// invalid syntax in the parameter 
    		throwAnnotationParsingError( entityName, fieldName, annotationString, e.getMessage() );
		}
        
        // check annotation exist
        List<String> definedAnnotations = KeyWords.getAnnotations();

        // is it a known annotation ?
        for (String annotationDefinition : definedAnnotations) { // "Id", "Max#", "Min#", ...
            if ( annotationDefinition.startsWith(annotationName) ) {
                // Annotation name found in defined annotations
                if ( annotationDefinition.endsWith("#") ) {
                	// this annotation must have a numeric parameter between ( and )
                	if ( parameterValue == null || parameterValue.length() == 0 ) {
                		throwAnnotationParsingError( entityName, fieldName, annotationString, "parameter required ");
                	}
                	// check parameter is numeric
                	try {
						new BigDecimal(parameterValue) ;
					} catch (NumberFormatException e) {
                		throwAnnotationParsingError( entityName, fieldName, annotationString, "numeric parameter required ");
					}
                	return new DomainEntityFieldAnnotation(annotationName, parameterValue);
                }
                else {
                	// annotation without parameter
                	if ( parameterValue != null ) {
                		throwAnnotationParsingError( entityName, fieldName, annotationString, "unexpected parameter");
                	}
                	return new DomainEntityFieldAnnotation(annotationName);
                }
            }
        }

        throwAnnotationParsingError( entityName, fieldName, annotationString, "unknown annotation");
        return null ; // never reached
    }

    /**
     * @param annotation e.g. "@Id", "@Max(12)", etc
     * @return "Id", "Max", etc
     */
    /* package */ String getAnnotationName (String annotation) {
    	StringBuffer sb = new StringBuffer();
    	// skip the first char (supposed to be @)
    	for ( int i = 1 ; i < annotation.length() ; i++ ) {
    		char c = annotation.charAt(i);
//        	if ( ( c >=  'a' && c <= 'z' ) || ( c >=  'A' && c <= 'Z' ) ) {
            if ( Character.isLetter(c) ) {
        		sb.append(c);
        	}
        	else {
        		break;
        	}
//        	if ( c == '(' ) {
//        		break;
//        	}
    	}
    	return sb.toString();
    }

    /**
     * @param annotation e.g. "@Id ", "@Max xx (12)", etc
     * @return "Id", "Max xx ", etc
     */
    /* package */ String getAnnotationWithoutParameter (String annotation) {
    	StringBuffer sb = new StringBuffer();
    	// skip the first char (supposed to be @)
    	for ( int i = 1 ; i < annotation.length() ; i++ ) {
    		char c = annotation.charAt(i);
            if ( c == '(' ) {
        		break;
        	}
            if ( c >= ' ' ) {
        		sb.append(c);
            }
    	}
    	return sb.toString().trim();
    }
    
    /**
     * @param s
     * @param openChar
     * @param closeChar
     * @return the parameter value or null if none
     * @throws Exception
     */
    /* package */ String getParameterValue(String s, char openChar, char closeChar) throws Exception {
        int openIndex  = s.lastIndexOf(openChar);
        int closeIndex = s.lastIndexOf(closeChar);
    	if ( openIndex < 0 && closeIndex < 0 ) {
    		// no open nor close char
    		return null ;
    	}
    	else {
    		// 1 or 2 chars found
        	if ( openIndex >= 0 && closeIndex >= 0 ) {
        		// open and close char found
        		if ( openIndex < closeIndex ) {
        			// valid order eg "(aa)"
        			return s.substring(openIndex + 1, closeIndex).trim();
        		}
        		else {
        			// unbalanced ( and ) eg ")aa("
        	        throw new Exception("unbalanced " + openChar + " and " + closeChar );
        		}
        	}
        	else {
    			// unbalanced ( and ) eg "(aa" or "aa)"
            	if ( openIndex < 0 ) {
	    	        throw new Exception(" '" + openChar + "' missing");
            	}
            	else {
	    	        throw new Exception(" '" + closeChar + "' missing");
            	}
        	}
    	}
    }
}