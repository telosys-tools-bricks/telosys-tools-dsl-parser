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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.DslModelError;

/**
 * Telosys DSL : entity file parser returning a list of grammar elements 
 *
 * @author Laurent GUERIN
 * 
 */
public class EntityElementsParser {
	
	private static final int CONTINUE = 0;
	private static final int END_OF_ELEMENT = 1;
	private static final int END_OF_LINE = 2;
	private static final int COMMENT = 3;
	private static final int SEPARATOR = 4;
	

	private static final char SPACE = 32;

	private void log(String message) {
		//ParserLogger.log("LOG:" + message);
	}
	private void logChar(char c) {
		//ParserLogger.print( "[" + c + "]");
	}

	/**
	 * Constructor
	 */
	public EntityElementsParser() {
		super();
	}
	
	/**
	 * Parse entity defined in the current file
	 * @return
	 * @throws EntityParsingError
	 */
	public List<Element> parseEntityFile(File entityFile) throws DslModelError  {
		if ( ! entityFile.exists() ) {
			throw new DslModelError(entityFile.getName(), "File not found");
		}
		log("parse() : File : " + entityFile.getAbsolutePath());

		return parseAllElements(entityFile.getAbsolutePath());
	}
	
	/**
	 * Read all elements contained in the given file 
	 * @param filePath
	 * @return
	 * @throws EntityParsingError
	 */
	protected List<Element> parseAllElements(String filePath) throws DslModelError {
		String entityNameFromFileName = DslModelUtil.getEntityName(new File(filePath));
		Path path = Paths.get(filePath); 
		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) { 
			List<Element> elements = new LinkedList<>();
			String line;
			int lineNumber = 0 ;
			while ((line = br.readLine()) != null) {
				lineNumber++;
				processLine(entityNameFromFileName, line, lineNumber, elements);
			}
			return elements;
		} catch (IOException e) {
			throw new DslModelError(entityNameFromFileName, "IOException : " + e.getMessage() );
		}
	}

	protected void processLine(String entityName, String line, int lineNumber, List<Element> elements ) throws DslModelError {
		StringBuilder sb = new StringBuilder();
		char[] lineCharacters = line.toCharArray();
		State state = new State(lineNumber);
		for ( char c : lineCharacters ) {
			logChar(c);
			int r ;
			if ( state.inAnnotationOrTagParam() ) {
				if ( state.inQuote() ) {
					r = processCharacterInQuote( c, sb, state );
				}
				else {
					r = processCharacterInAnnotationOrTagParam( c, sb, state );
				}
			}
			else {
				r = processCharacterOutOfAnnotationOrTagParam(entityName, c, sb, state );
			}
			state.setPreviousChar(c);
			// result ?
			if ( r == END_OF_ELEMENT ) {
				keepElement(elements, sb, state);
				sb = new StringBuilder();
			}
			else if ( r == SEPARATOR ) {
				keepElement(elements, sb, state); // SEPARATOR => end of current element
				sb = new StringBuilder();
				keepSeparatorElement(elements, c, state); // Each SEPARATOR is an element => keep it 
			}
			else if ( r == END_OF_LINE ) {
				keepElement(elements, sb, state);
				return;
			}
			else if ( r == COMMENT ) {
				// do not keep the beginning of the comment
				return;
			}
			// ELSE => CONTINUE
		}
		// End Of Line => Keep current element if any
		keepElement(elements, sb, state);
	}

	private void keepSeparatorElement(List<Element> elements, char c, State state) {
		elements.add(new Element(state.getLineNumber(), String.valueOf(c)));
		state.reset();
	}
	private void keepElement(List<Element> elements, StringBuilder sb, State state) {
		if ( sb.length() > 0 ) { 
			Element element = new Element(state.getLineNumber(), sb.toString());
			elements.add(element);
			state.setPreviousElement(element);
		}
		state.reset();
	}
	
	private int processCharacterOutOfAnnotationOrTagParam(String entityName, char c, StringBuilder sb, State state ) throws DslModelError {
		logChar(c);
		if ( c == '{' || c == '}' || c == ';' || c == ':' ) {
			return SEPARATOR;
		}
		else if ( c == '@' ) {
			state.setInAnnotation();
			sb.append(c);
			return CONTINUE;
		}
		else if ( c == '#' ) {
			state.setInTag();
			sb.append(c);
			return CONTINUE;
		}
		else if ( c == '(' ) {
			if ( state.inAnnotationOrTag() ) {
				// Example : @Xxx(  or #Xxx( 
				state.setInAnnotationOrTagParam();
				sb.append(c);
				return CONTINUE;
			}
			else {
				// Not supposed to be here. Example : @Xxx (  or #Xxx ( 
				throw newUnexpectedCharacter(c, entityName, state);
			}
		}
		else if ( c == '\"' ) {
			// Invalid char here
			throw newUnexpectedCharacter(c, entityName, state);
		}
		else if ( c == SPACE || c == '\t' ) {
			return END_OF_ELEMENT;
		}
		else if ( c == '\r' || c == '\n' ) { 
			// not supposed to happen
			return END_OF_LINE;
		}
		else if ( c == '/' && state.previousChar() == '/' ) {
			// comment "//" => ignore rest of line 
			return COMMENT;
		}
		else {
			// default processing : accumulate char in current element 
			sb.append(c);
			return CONTINUE;
		}
	}
	
	private DslModelError newUnexpectedCharacter(char invalidChar, String entityName, State state) {
		String previousElement = state.previousElement().getContent();
		String message = "Unexpected character [" + invalidChar + "]";
		if ( ! StrUtil.nullOrVoid(previousElement) ) {
			message = message + " after element [" + previousElement + "]";
		}
		else if ( state.previousChar() != 0 ) {
			message = message + " after character [" + state.previousChar() + "]";
		}
		return new DslModelError(entityName, state.getLineNumber(), message);
	}
	
	private int processCharacterInAnnotationOrTagParam(char c, StringBuilder sb, State state ) {
		if ( state.inQuote() ) {
			// Not supposed to happen
			throw new IllegalStateException("processCharacterInAnnotationOrTagParam : in quote");
		}
		if ( c == '\"' ) { // double quote char (open/close)
			state.openQuote();
			sb.append(c);
			return CONTINUE;
		}
		else if ( c == ')' ) { // end of parameter = end of annotation or tag element
			sb.append(c);
			return END_OF_ELEMENT;
		}
		else {
			// default processing : accumulate char in current element 
			sb.append(c); // keep all character
			return CONTINUE;
		}
	}
	
	private int processCharacterInQuote(char c, StringBuilder sb, State state ) {
		if ( ! state.inQuote() ) {
			// Not supposed to happen
			throw new IllegalStateException("processCharacterInQuote : not in quote");
		}
		if ( c == '\"' && state.previousChar() != '\\' ) { // closing double quote char
			// Real closing quote 
			state.closeQuote();
			// in other cases the character should be treated as a literal 
			// => not a closing quote
		}
		// accumulate char in current element (in all cases) 
		sb.append(c); // keep all character
		return CONTINUE;
	}	
}
