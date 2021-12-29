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
package org.telosys.tools.dsl.parser2;

import org.telosys.tools.dsl.parser.commons.ParamValue;
import org.telosys.tools.dsl.parser.commons.ParamValueOrigin;

/**
 * Annotation and tag parsing (shared methods)
 * 
 * @author Laurent GUERIN
 */
public abstract class AnnotationAndTagProcessor {

	private static final char OPENING_PARENTHESIS = '(';
	private static final char CLOSING_PARENTHESIS = ')';

	private final String entityName;
	private final String fieldName;
	
	/**
	 * Constructor for parsing at FIELD level
	 * 
	 * @param entityName
	 * @param fieldName
	 */
	protected AnnotationAndTagProcessor(String entityName, String fieldName) {
		super();
		this.entityName = entityName;
		this.fieldName = fieldName;
	}
	
	/**
	 * Constructor for parsing at ENTITY level (no fieldname)
	 * 
	 * @param entityName
	 */
	protected AnnotationAndTagProcessor(String entityName) {
		super();
		this.entityName = entityName;
		this.fieldName = null;
	}
	

	public String getEntityName() {
		return entityName;
	}

	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Creates a new error 
	 * @param lineNumber
	 * @param message
	 * @return
	 */
	protected ParserError newError(int lineNumber, String message) {
		if ( this.fieldName != null ) {
			return new ParserError(entityName, lineNumber, fieldName, message);
		}
		else {
			return new ParserError(entityName, lineNumber, message);
		}
	}

	/**
	 * Returns the annotation name <br>
	 * 
	 * @param annotationOrTag
	 *            e.g. "@Id", "@Max(12)", "#MyTag", "#Abc(aa)", etc
	 * @return "Id", "Max", "MyTag", etc
	 * @throws Exception
	 */
	protected String getName(Element element) throws ParserError {
		String annotationOrTag = element.getContent();
		boolean blankCharFound = false;
		StringBuilder sb = new StringBuilder();
		// skip the first char (supposed to be '@' or '#' )
		for (int i = 1; i < annotationOrTag.length(); i++) {
			char c = annotationOrTag.charAt(i);
			if (Character.isLetter(c)) {
				if (blankCharFound) {
					// Case letter after a blank char : "Id xxx" or "aaa bbb"
					throw newError(element.getLineNumber(), annotationOrTag + " : invalid name");
				}
				sb.append(c);
			} else if (Character.isWhitespace(c)) {
				blankCharFound = true;
			} else if (c == '(') {
				break;
			} else {
				// Unexpected ending character
				throw newError(element.getLineNumber(),  annotationOrTag + " : invalid name");
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the parameter value if any <br>
	 * Returns the raw string located between the '(' and ')'.<br>
	 * The returned value is trimed.<br>
	 * 
	 * @param annotationOrTag
	 *            e.g. "@Id", "@Max(12)", etc
	 * @return the parameter value or null if none
	 * @throws Exception
	 */
	protected String getParameterValue(Element element) throws ParserError {
		String annotationOrTag = element.getContent();
		int openIndex = annotationOrTag.indexOf(OPENING_PARENTHESIS); //  first occurrence of '('
		int closeIndex = annotationOrTag.lastIndexOf(CLOSING_PARENTHESIS); // last occurrence of ')'
		if (openIndex < 0 && closeIndex < 0) {
			// no open nor close char
			return null;
		} else {
			// 1 or 2 chars found
			if (openIndex >= 0 && closeIndex >= 0) {
				// open and close char found
				if (openIndex < closeIndex) {
					// valid order eg "(aa)"
					// get string between ( and )
					String paramValue = annotationOrTag.substring(openIndex + 1, closeIndex);
					// trim
					return paramValue.trim();
				} else {
					// unbalanced ( and ) eg ")aa("
					throw newError(element.getLineNumber(), annotationOrTag + " : unbalanced ( and )");
				}
			} else {
				// unbalanced ( and ) eg "(aa" or "aa)"
				if (openIndex < 0) {
					throw newError(element.getLineNumber(), annotationOrTag + " : '(' missing");
				} else {
					throw newError(element.getLineNumber(), annotationOrTag + " : ')' missing");
				}
			}
		}
	}

	protected ParamValue buildTagParamValue(String tagName, String parameterValue) {
		if ( fieldName != null ) {
			// Tag defined at FIELD level
			return new ParamValue(entityName, fieldName, tagName, parameterValue, 
					ParamValueOrigin.FIELD_TAG);
		}
		else {
			// Tag defined at ENTITY level
			return new ParamValue(entityName, "", tagName, parameterValue, 
					ParamValueOrigin.ENTITY_TAG);
		}
	}
}