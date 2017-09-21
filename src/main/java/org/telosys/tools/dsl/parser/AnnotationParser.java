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
package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    	//super(LoggerFactory.getLogger(AnnotationParser.class));
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

        //--- get the annotation name 
//        String annotationName = getAnnotationName(annotationString);
//        if ( ! annotationName.equals( getAnnotationWithoutParameter(annotationString)) ) {
//    		throwAnnotationParsingError( entityName, fieldName, annotationString, "invalid syntax" );
//        }
        String annotationName = null;
		try {
			annotationName = getAnnotationName(annotationString);
		} catch (Exception e) {
			// invalid syntax in the annotation name  
    		throwAnnotationParsingError( entityName, fieldName, annotationString, e.getMessage() );
		}

        //--- get the parameter value if any 
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
        for (String annotationDefinition : definedAnnotations) { // "Id", "SizeMin%", "SizeMax%", "Max#", "Min#", ""...
            if ( annotationDefinition.startsWith(annotationName) ) {
                // Annotation name found in defined annotations
                if ( annotationDefinition.endsWith("%") ) {
                	// this annotation must have an integer parameter between ( and )
//                	if ( parameterValue == null || parameterValue.length() == 0 ) {
//                		throwAnnotationParsingError( entityName, fieldName, annotationString, "parameter required ");
//                	}
                	// Integer parameter required
                	Integer numberValue = null ;
                	try {
                		numberValue = getParameterValueAsInteger(parameterValue);
						//new BigDecimal(parameterValue) ;
					} catch (Exception e) {
                		throwAnnotationParsingError( entityName, fieldName, annotationString, "integer parameter required ");
					}
                	return new DomainEntityFieldAnnotation(annotationName, numberValue);
                }
                else if ( annotationDefinition.endsWith("#") ) {
                	// Decimal parameter required
                	BigDecimal numberValue = null ;
                	try {
                		numberValue = getParameterValueAsBigDecimal(parameterValue);
					} catch (Exception e) {
                		throwAnnotationParsingError( entityName, fieldName, annotationString, "numeric parameter required ");
					}
                	return new DomainEntityFieldAnnotation(annotationName, numberValue);
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
     * Returns the annotation name <br>
     * 
     * @param annotation e.g. "@Id", "@Max(12)", etc
     * @return "Id", "Max", etc
     * @throws Exception
     */
    /* package */ String getAnnotationName (String annotation) throws Exception {
    	boolean blankCharFound = false ;
    	StringBuffer sb = new StringBuffer();
    	// skip the first char (supposed to be @)
    	for ( int i = 1 ; i < annotation.length() ; i++ ) {
    		char c = annotation.charAt(i);
            if ( Character.isLetter(c) ) {
            	if ( blankCharFound ) { 
            		// Case letter after a blank char : "Id xxx" or "aaa bbb" 
            		throw new Exception("Invalid annotation name '" + annotation + "'");
            	}
        		sb.append(c);
        	}
            else if ( Character.isWhitespace(c) ) {
            	blankCharFound = true ;
            }
        	else if ( c == '(' ) {
        		break;
        	}
        	else {
        		// Unexpected ending character 
        		throw new Exception("Invalid annotation syntaxe '" + annotation + "'");
        	}
    	}
    	return sb.toString();
    }

//    /**
//     * @param annotation e.g. "@Id ", "@Max xx (12)", etc
//     * @return "Id", "Max xx ", etc
//     */
//    /* package */ String getAnnotationWithoutParameter (String annotation) {
//    	StringBuffer sb = new StringBuffer();
//    	// skip the first char (supposed to be @)
//    	for ( int i = 1 ; i < annotation.length() ; i++ ) {
//    		char c = annotation.charAt(i);
//            if ( c == '(' ) {
//        		break;
//        	}
//            if ( c >= ' ' ) {
//        		sb.append(c);
//            }
//    	}
//    	return sb.toString().trim();
//    }
    
    /**
     * Returns the parameter value if any.<br>
     * The value located between the given open/close char.<br>
     * The returned value is trimed.<br>
     *  
     * @param annotation e.g. "@Id", "@Max(12)", etc
     * @param openChar  typically '('
     * @param closeChar typically ')'
     * @return the parameter value or null if none
     * @throws Exception
     */
    /* package */ String getParameterValue(String annotation, char openChar, char closeChar) throws Exception {
        int openIndex  = annotation.lastIndexOf(openChar);
        int closeIndex = annotation.lastIndexOf(closeChar);
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
        			// get string between ( and )
        			String paramValue = annotation.substring(openIndex + 1, closeIndex); 
        			// trim
        			return paramValue.trim();
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

    private void checkParameterExistence(String parameterValue) throws Exception {
    	if ( parameterValue == null || parameterValue.length() == 0 ) {
    		throw new Exception("Parameter required");
    	}
    }
    
    /* package */ Integer getParameterValueAsInteger(String parameterValue) throws Exception {
		// Integer value
    	checkParameterExistence(parameterValue);
		try {
			return new Integer(parameterValue) ;
		} catch (NumberFormatException e) {
			throw new Exception("Invalid integer parameter '" + parameterValue + "'");
		}
    }

    /* package */ BigDecimal getParameterValueAsBigDecimal(String parameterValue) throws Exception {
		// Decimal value
    	checkParameterExistence(parameterValue);
    	try {
			return new BigDecimal(parameterValue) ;
		} catch (NumberFormatException e) {
			throw new Exception("Invalid decimal parameter '" + parameterValue + "'");
		}
    }
}