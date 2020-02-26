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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;

/**
 * Utility class designed to split a string containing annotations and tags 
 * 
 * @author Laurent GUERIN
 *
 */
public class FieldAnnotationsAndTagsSplitter {

	private final String entityName ;
	private final String fieldName ;
	
	
	List<String> elements = new LinkedList<>();
	
	private StringBuilder currentElement ;
	boolean inElement = false ;
	boolean inSingleQuote = false ;
	boolean inDoubleQuote = false ;
	boolean inParentheses = false ;
	
	public FieldAnnotationsAndTagsSplitter(String entityName, String fieldName) {
		super();
		this.entityName = entityName ;
		this.fieldName = fieldName ;
	}
	
	private static final boolean LOG = false;
	
	private void logChar(char c) {
		if (LOG) {
			ParserLogger.print( "[" + c + "]");
		}
	}
	private void log(char c) {
		if (LOG) {
			ParserLogger.print(String.valueOf(c));
		}
	}
	
	private boolean inParentheses() {
		return inElement && inParentheses ;
	}
	private boolean inQuote() {
		return inElement && inParentheses && ( inSingleQuote || inDoubleQuote ) ;
	}
	
	public List<String> split(String s) throws AnnotationOrTagError {
		
		for (char c : s.toCharArray() ) {
			logChar(c);
			if ( c == '@' || c == '#' ) {
				if ( inParentheses() ) {
					if ( inQuote() ) {
						keepChar(c);
					}
					else {
						throw new AnnotationOrTagError(entityName, fieldName, s, "unexpected '" + c + "' in parentheses");
					}
				}
				else {
					newCurrentElement();
					keepChar(c);
				}
			}
			else if ( c == '(' ) {
				if ( ! inQuote() ) {
					if ( ! inParentheses() ) {
						inParentheses = true ;
					}
					else {
						throw new AnnotationOrTagError(entityName, fieldName, s, "unexpected opening parenthesis");
					}
				}
				keepChar(c);
			}
			else if ( c == ')' ) {
				if ( ! inQuote() ) {
					if ( inParentheses() ) {
						inParentheses = false ;
						inElement = false; // it's the end of current element
					}
					else {
						throw new AnnotationOrTagError(entityName, fieldName, s, "unexpected closing parenthesis");
					}
				}
				keepChar(c);
			}
			else if ( c == '\'' ) {
				if ( inParentheses() ) {
					if ( ! inDoubleQuote ) {
						inSingleQuote = ! inSingleQuote ; // toggle quote flag
					}
					keepChar(c);
				}
				else {
					throw new AnnotationOrTagError(entityName, fieldName, s, "unexpected single quote");
				}
				
			}
			else if ( c == '\"' ) {
				if ( inParentheses() ) {
					if ( ! inSingleQuote ) {
						inDoubleQuote = ! inDoubleQuote ; // toggle quote flag
					}
					keepChar(c);
				}
				else {
					throw new AnnotationOrTagError(entityName, fieldName, s, "unexpected double quote");
				}
			}
			else if ( c == ',' ) {
				if ( inQuote() ) {
					keepChar(c);
				}
				// else ignore this char
			}
			else {
				keepChar(c);
//				if ( inElement ) {
//					keepChar(c);
//				}
//				else {
//					// 
//				}
			}
		}
		// end 
		keepCurrentElement();
		return elements;
	}
	
	private void newCurrentElement() throws AnnotationOrTagError {
		keepCurrentElement();
		currentElement = new StringBuilder();
		// reset all flags
		inElement = true ;
		inSingleQuote = false ;
		inDoubleQuote = false ;
		inParentheses = false ;
	}
	
	private void keepCurrentElement() throws AnnotationOrTagError {
		if ( currentElement != null ) {
			if ( inQuote() ) {
				throw new AnnotationOrTagError(entityName, fieldName, currentElement.toString(), "unexpected end of element (quote not closed)");
			}
			if ( inParentheses() ) {
				throw new AnnotationOrTagError(entityName, fieldName, currentElement.toString(), "unexpected end of element (parenthesis not closed)");
			}
			String element = currentElement.toString().trim();
			elements.add(element);
		}
	}
	
	private void keepChar(char c) {
		log( '+' );
		currentElement.append(c);
	}

}
