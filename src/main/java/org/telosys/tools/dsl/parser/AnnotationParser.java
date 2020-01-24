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
import java.util.List;

import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.KeyWords;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;

/**
 * Field annotations parsing
 * 
 * @author Laurent Guerin
 */
public class AnnotationParser { // extends AbstractParser  {

    private static final char OPENING_PARENTHESIS = '(' ;
    private static final char CLOSING_PARENTHESIS = ')' ;
    
    private final String entityName ;
    private final String fieldName ;

    /**
     * Constructor
     * @param entityName
     * @param fieldName
     */
    public AnnotationParser(String entityName, String fieldName) {
		super();
		this.entityName = entityName;
		this.fieldName = fieldName;
	}
    
//    /**
//     * Throws an annotation parsing exception
//     * @param entityName
//     * @param fieldName
//     * @param annotationString
//     * @param message
//     */
//    private void throwAnnotationParsingError(String entityName, String fieldName, String annotationString, String message) {
////    	throwParsingError(entityName + "." + fieldName, "Annotation error '" + annotationString + "' (" + message + ")");
//        String errorMessage = entityName + "." + fieldName + " : Annotation error '" + annotationString + "' (" + message + ")" ;
//        throw new DslParserException(errorMessage);
//    }

    /**
     * Throws an annotation parsing exception
     * @param annotationString
     * @param message
     */
    private void throwAnnotationParsingError(String annotationString, String message) {
        String errorMessage = entityName + "." + fieldName + " : '" + annotationString + "' (" + message + ")" ;
        throw new DslParserException(errorMessage);
    }

//    /**
//     * Parse field annotations located between brackets : '{ @xxx, @xxx }'
//     * @param entityName
//     * @param fieldName
//     * @param annotations the annotations string without '{' and '}'
//     * @return
//     */
//    protected List<DomainEntityFieldAnnotation> parseAnnotations(String entityName, String fieldName, String annotations) {
//
//    	if ( annotations == null || "".equals(annotations) ) {
//    		// return void list
//    		return new ArrayList<>();
//    	}
//
//        // list of annotation found
//        String[] annotationList = annotations.split(",");
//        // at least 1 annotation is required, if there are brackets
//        if (annotationList.length == 0 ) {
//        	// example : "," or ",," (only commas)
//        	// not an error => return void list
//        	return new ArrayList<>();
//        }
//
//        List<DomainEntityFieldAnnotation> list = new ArrayList<>();
//        // extract annotations
//        for (String annotationString : annotationList) {
//            DomainEntityFieldAnnotation annotation = this.parseSingleAnnotation(entityName, fieldName, annotationString.trim());
//            list.add(annotation);
//        }
//
//        return list;
//    }

    /**
     * Parse a single annotation 
     * @param entityName
     * @param fieldName
     * @param annotationString e.g. "@Id", "@Max(12)", etc
     * @return
     */
    public DomainEntityFieldAnnotation parseAnnotation(String annotationString) {
    	
        // must start with a '@'
        if (annotationString.charAt(0) != '@') {
            throwAnnotationParsingError(annotationString, "must start with '@'");
        }
        return parseAnnotationOrTag(annotationString);
    }
    
    public DomainEntityFieldAnnotation parseAnnotationOrTag(String annotationOrTag) {
        //--- get the annotation name 
        String annotationName = null;
		try {
			annotationName = getName(annotationOrTag);
		} catch (Exception e) {
			// invalid syntax in the annotation name  
    		throwAnnotationParsingError(annotationOrTag, e.getMessage() );
		}

        //--- get the parameter value if any 
    	String parameterValue = null ;
    	try {
			parameterValue = getParameterValue(annotationOrTag ) ;
		} catch (Exception e) {
			// invalid syntax in the parameter 
    		throwAnnotationParsingError(annotationOrTag, e.getMessage() );
		}
        
        // check annotation exist
        List<String> definedAnnotations = KeyWords.getAnnotations();

        // is it a known annotation ?
        for (String annotationDefinition : definedAnnotations) { // "Id", "SizeMin%", "SizeMax%", "Max#", "Min#", ""...
            if ( annotationDefinition.startsWith(annotationName) ) {
                // Annotation name found in defined annotations
                if ( annotationDefinition.endsWith("%") ) { // INTEGER parameter required
                	// this annotation must have an integer parameter between ( and )
                	Integer numberValue = null ;
                	try {
                		numberValue = getParameterValueAsInteger(annotationOrTag, parameterValue);
					} catch (Exception e) {
                		throwAnnotationParsingError(annotationOrTag, "integer parameter required ");
					}
                	return new DomainEntityFieldAnnotation(annotationName, numberValue);
                }
                else if ( annotationDefinition.endsWith("#") ) { // DECIMAL parameter required
                	BigDecimal numberValue = null ;
                	try {
                		numberValue = getParameterValueAsBigDecimal(annotationOrTag, parameterValue);
					} catch (Exception e) {
                		throwAnnotationParsingError(annotationOrTag, "numeric parameter required ");
					}
                	return new DomainEntityFieldAnnotation(annotationName, numberValue);
                }
                else if ( annotationDefinition.endsWith("$") ) { // STRING parameter required
                	String value = null ;
                	try {
                		value = getParameterValueAsString(annotationOrTag, parameterValue);
					} catch (Exception e) {
                		throwAnnotationParsingError(annotationOrTag, "string parameter required ");
					}
                	return new DomainEntityFieldAnnotation(annotationName, value);
                }
                else {
                	// annotation without parameter
                	if ( parameterValue != null ) {
                		throwAnnotationParsingError(annotationOrTag, "unexpected parameter");
                	}
                	return new DomainEntityFieldAnnotation(annotationName);
                }
            }
        }

        throwAnnotationParsingError( annotationOrTag, "unknown annotation");
        return null ; // never reached
    }

    /**
     * Returns the annotation name <br>
     * 
     * @param annotationOrTag e.g. "@Id", "@Max(12)", etc
     * @return "Id", "Max", etc
     * @throws Exception
     */
    protected String getName (String annotationOrTag) { // throws Exception {
    	boolean blankCharFound = false ;
    	StringBuilder sb = new StringBuilder();
    	// skip the first char (supposed to be @)
    	for ( int i = 1 ; i < annotationOrTag.length() ; i++ ) {
    		char c = annotationOrTag.charAt(i);
            if ( Character.isLetter(c) ) {
            	if ( blankCharFound ) { 
            		// Case letter after a blank char : "Id xxx" or "aaa bbb" 
//            		throw new Exception("Invalid annotation name '" + annotationOrTag + "'");
            		throwAnnotationParsingError(annotationOrTag, "Invalid syntax");
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
//        		throw new Exception("Invalid annotation syntaxe '" + annotationOrTag + "'");
        		throwAnnotationParsingError(annotationOrTag, "Invalid syntax");
        	}
    	}
    	return sb.toString();
    }

    /**
     * Returns the parameter value if any (the value located between the '(' and ')'.<br>
     * The returned value is trimed.<br>
     * @param annotationOrTag  e.g. "@Id", "@Max(12)", etc
     * @return the parameter value or null if none
     * @throws Exception
     */
    protected String getParameterValue(String annotationOrTag) { // throws Exception {
        int openIndex  = annotationOrTag.lastIndexOf(OPENING_PARENTHESIS);
        int closeIndex = annotationOrTag.lastIndexOf(CLOSING_PARENTHESIS);
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
        			String paramValue = annotationOrTag.substring(openIndex + 1, closeIndex); 
        			// trim
        			return paramValue.trim();
        		}
        		else {
        			// unbalanced ( and ) eg ")aa("
//        	        throw new Exception("unbalanced " + OPENING_PARENTHESIS + " and " + CLOSING_PARENTHESIS );
            		throwAnnotationParsingError(annotationOrTag, "unbalanced ( and )");
        		}
        	}
        	else {
    			// unbalanced ( and ) eg "(aa" or "aa)"
            	if ( openIndex < 0 ) {
//	    	        throw new Exception(" '" + OPENING_PARENTHESIS + "' missing");
            		throwAnnotationParsingError(annotationOrTag, "'(' missing");
            	}
            	else {
//	    	        throw new Exception(" '" + CLOSING_PARENTHESIS + "' missing");
            		throwAnnotationParsingError(annotationOrTag, "')' missing");
            	}
        	}
    	}
    	return null ;
    }

    private void checkParameterExistence(String annotationOrTag, String parameterValue) { // throws Exception {
    	if ( parameterValue == null || parameterValue.length() == 0 ) {
//    		throw new Exception("Parameter required");
    		throwAnnotationParsingError(annotationOrTag, "Parameter required");
    	}
    }
    
    protected Integer getParameterValueAsInteger(String annotationOrTag, String parameterValue) { // throws Exception {
		// Integer value
    	checkParameterExistence(annotationOrTag, parameterValue);
		try {
			return new Integer(parameterValue) ;
		} catch (NumberFormatException e) {
//			throw new Exception("Invalid integer parameter '" + parameterValue + "'");
    		throwAnnotationParsingError(annotationOrTag, "Invalid integer parameter '" + parameterValue + "'");
    		return 0; 
		}
    }

    protected BigDecimal getParameterValueAsBigDecimal(String annotationOrTag, String parameterValue) throws Exception {
		// Decimal value
    	checkParameterExistence(annotationOrTag, parameterValue);
    	try {
			return new BigDecimal(parameterValue) ;
		} catch (NumberFormatException e) {
//			throw new Exception("Invalid decimal parameter '" + parameterValue + "'");
    		throwAnnotationParsingError(annotationOrTag, "Invalid decimal parameter '" + parameterValue + "'");
    		return null ;
		}
    }
    
    protected String getParameterValueAsString(String annotationOrTag, String parameterValue) throws Exception {
    	checkParameterExistence(annotationOrTag, parameterValue);
    	String s = parameterValue.trim(); // removes all void chars ( blank, tab, cr, lf, ...)
    	if ( s.startsWith("\"") && s.endsWith("\"") ) {
    		return unquote(s, '"');
    	}
    	else if ( s.startsWith("'") && s.endsWith("'") ) {
    		return unquote(s, '\'');
    	}
    	else {
    		return s;
    	}
    }

    /**
     * Remove quote char at the first and last position if any 
     * @param s
     * @param quoteChar
     * @return
     */
    protected static String unquote(String s, char quoteChar ) {
    	if ( s == null ) {
    		return s ;
    	}
    	if ( s.length() == 0  ) {
    		return s ;
    	}
    	int last = s.length()-1;
    	if ( s.charAt(0) == quoteChar && s.charAt(last) == quoteChar ) {
    		return s.substring(1, last);
    	}
    	else {
    		return s ;
    	}
    }
    
}