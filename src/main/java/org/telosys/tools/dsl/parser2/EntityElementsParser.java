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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.parser.EntityFileParsingResult;
import org.telosys.tools.dsl.parser.ParserLogger;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;

/**
 * Telosys DSL entity parser
 *
 * @author Laurent GUERIN
 * 
 */
public class EntityElementsParser {
	
	private static final int CONTINUE = 0;
	private static final int END_OF_ELEMENT = 1;
	private static final int END_OF_LINE = 2;
	private static final int COMMENT = 3;
	

	private static final char SPACE = 32;

	private final File entityFile;
	private final String entityNameFromFileName;

	private void log(String message) {
		ParserLogger.log("LOG:" + message);
	}
	private void logChar(char c) {
		ParserLogger.print( "[" + c + "]");
	}

	/**
	 * Constructor
	 * @param fileName
	 */
	public EntityElementsParser(String fileName) {
		this.entityFile = new File(fileName);
		this.entityNameFromFileName = DslModelUtil.getEntityName(entityFile);
	}

	/**
	 * Constructor
	 * @param file
	 */
	public EntityElementsParser(File file) {
		this.entityFile = file;
		this.entityNameFromFileName = DslModelUtil.getEntityName(entityFile);
	}

	/**
	 * Parse entity defined in the current file
	 * @return
	 * @throws EntityParsingError
	 */
	public List<Element> parseElements() throws EntityParsingError  {
		if ( ! entityFile.exists() ) {
			throw new EntityParsingError(entityNameFromFileName, "File not found");
		}
		log("parse() : File : " + entityFile.getAbsolutePath());

		//List<String> lines = readAllLines(entityFile.getAbsolutePath());
		return readAllElements(entityFile.getAbsolutePath());
	}
	
	/**
	 * Read all elements contained in the given file 
	 * @param filePath
	 * @return
	 * @throws EntityParsingError
	 */
	protected List<Element> readAllElements(String filePath) throws EntityParsingError {
		Path path = Paths.get(filePath); 
		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) { 
			List<Element> elements = new LinkedList<>();
			String line;
			int lineNumber = 0 ;
			while ((line = br.readLine()) != null) {
				lineNumber++;
				processLine(line, lineNumber, elements);
			}
			return elements;
		} catch (IOException e) {
			throw new EntityParsingError(entityNameFromFileName, "IOException : " + e.getMessage() );
		} catch (Exception e) {
			throw new EntityParsingError(entityNameFromFileName, "Exception : " + e.getMessage() );
		}
	}

	private void processLine(String line, int lineNumber, List<Element> elements ) {
		StringBuilder sb = new StringBuilder();
		char[] lineCharacters = line.toCharArray();
		State state = new State(lineNumber);
		for ( char c : lineCharacters ) {
			logChar(c);
			int r ;
			if ( state.inAnnotationOrTagParam() ) {
				r = processCharacterInAnnotationOrTagParam( c, sb, state );
			}
			else {
				r = processCharacterOutOfAnnotationOrTagParam( c, sb, state );
			}
			state.setPreviousChar(c);
			// result ?
			if ( r == END_OF_ELEMENT ) {
				keepElement(elements, sb, state);
				sb = new StringBuilder();
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
		// Keep current element if any
		keepElement(elements, sb, state);
	}

	private void keepElement(List<Element> elements, StringBuilder sb, State state) {
		if ( sb.length() > 0 ) { 
			elements.add(new Element(state.getLineNumber(), sb.toString()));
		}
		state.reset();
	}
	
	private int processCharacterOutOfAnnotationOrTagParam(char c, StringBuilder sb, State state ) {
		logChar(c);
		if ( c == '{' || c == '}' || c == ';' || c == ':' ) {
			sb.append(c);
			return END_OF_ELEMENT;
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
				state.setInAnnotationOrTagParam();
				sb.append(c);
				return CONTINUE;
			}
			else {
				sb.append(c);
				return END_OF_ELEMENT;
			}
		}
		else if ( c == '\'' ) {
			if ( state.inAnnotationOrTagParam() ) {
				state.toggleSingleQuote();
			}
			else {
				throw new RuntimeException("Unexpected single quote");
			}
			sb.append(c);
			return CONTINUE;
		}
		else if ( c == '\"' ) {
			if ( state.inAnnotationOrTagParam() ) {
				state.toggleDoubleQuote();
			}
			else {
				throw new RuntimeException("Unexpected double quote");
			}
			sb.append(c);
			return CONTINUE;
		}
		else if ( c == SPACE || c == '\t' ) {
			return END_OF_ELEMENT;
		}
		else if ( c == '\r' || c == '\n' ) { // not supposed to happen
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
	
	private int processCharacterInAnnotationOrTagParam(char c, StringBuilder sb, State state ) {
		if ( c == '\'' ) { // single quote char (open/close)
			if ( ! state.inDoubleQuote() ) {
				state.toggleSingleQuote();
			}
			sb.append(c);
			return CONTINUE;
		}
		else if ( c == '\"' ) { // double quote char (open/close)
			if ( ! state.inSingleQuote() ) {
				state.toggleDoubleQuote();
			}
			sb.append(c);
			return CONTINUE;
		}
		else if ( c == ')' ) { // end of param
			sb.append(c);
			if ( state.inQuote() ) {
				return CONTINUE; // ')' between ' or " : not significant (keep it and continue)
			}
			else {
				return END_OF_ELEMENT;
			}
		}
		else {
			// default processing : accumulate char in current element 
			sb.append(c); // keep all character
			return CONTINUE;
		}
	}
	
}
