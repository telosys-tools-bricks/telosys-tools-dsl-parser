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

import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;

/**
 * Telosys DSL entity parser
 *
 * @author Laurent GUERIN
 * 
 */
public class EntityFileParser {

	private static final char SPACE = 32;

	private final File entityFile;
	private final String entityNameFromFileName;

	private final List<FieldPartsBuilder> fieldsParsed = new LinkedList<>();
	private FieldPartsBuilder currentField = null ;

	private boolean inFields      = false ;
	private boolean fieldsClosed  = false ;
	private boolean inAnnotations = false ;
	
	private String entityNameParsed = null ;

	private void log(String message) {
		ParserLogger.log("LOG:" + message);
	}
	private void logChar(char c) {
		ParserLogger.print( "[" + c + "]");
	}
	private void log(char c) {
		ParserLogger.print(String.valueOf(c));
	}

	/**
	 * Constructor
	 * @param fileName
	 */
	public EntityFileParser(String fileName) {
		this.entityFile = new File(fileName);
		this.entityNameFromFileName = DslModelUtil.getEntityName(entityFile);
	}

	/**
	 * Constructor
	 * @param file
	 */
	public EntityFileParser(File file) {
		this.entityFile = file;
		this.entityNameFromFileName = DslModelUtil.getEntityName(entityFile);
	}
	
	/**
	 * Read all lines contained in the given file 
	 * @param filePath
	 * @return
	 * @throws EntityParsingError
	 */
	protected List<String> readAllLines(String filePath) throws EntityParsingError {
		Path path = Paths.get(filePath); 
		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) { 
			List<String> lines = new LinkedList<>();
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		} catch (IOException e) {
			throw new EntityParsingError(entityNameFromFileName, "IOException : " + e.getMessage() );
		} catch (Exception e) {
			throw new EntityParsingError(entityNameFromFileName, "Exception : " + e.getMessage() );
		}
	}

	/**
	 * Parse entity defined in the current file
	 * @return
	 * @throws EntityParsingError
	 */
	public EntityFileParsingResult parse() throws EntityParsingError  {
		if ( ! entityFile.exists() ) {
			throw new EntityParsingError(entityNameFromFileName, "File not found");
		}
		log("parse() : File : " + entityFile.getAbsolutePath());

		List<String> lines = readAllLines(entityFile.getAbsolutePath());
		int lineNumber = 0 ;
		for ( String line : lines ) {
			lineNumber++;
			// populate "fieldsParsed"
			processLine(line, lineNumber);
		}
		
//		parseFile() ; 
		
		List<FieldParts> fieldsParts = new LinkedList<>();
		for ( FieldPartsBuilder fb : fieldsParsed ) {
			fieldsParts.add(fb.getFieldParts());
		}
		return new EntityFileParsingResult(this.entityNameFromFileName, this.entityNameParsed, fieldsParts);
	}
	
//	protected void parseFile() throws EntityParsingError {
//		if (!entityFile.exists()) {
//			throw new EntityParsingError(entityNameFromFileName, "File not found");
//		}
//		log("parse() : File : " + entityFile.getAbsolutePath());
//		
//		int lineNumber = 0;
//		try (BufferedReader br = new BufferedReader(new FileReader(entityFile))) {
//			String line;
//			while ((line = br.readLine()) != null) {
//				lineNumber++;
//				processLine(line, lineNumber);
//				// read next line
//			}
//		} catch (IOException e) {
//			throw new EntityParsingError(entityNameFromFileName, "IOException");
//		}
//	}
	
	/**
	 * Process any type of line 
	 * @param line
	 * @param lineNumber
	 * @throws EntityParsingError
	 */
	private void processLine(String line, int lineNumber) throws EntityParsingError {
		log("\n-------------------------------------------");
		log("#" + lineNumber + " : " + line);
		
		List<String> entityHeader = new LinkedList<>();
		if ( inFields ) {
			// we are "in fields" ( between { and } )
			processLineFieldLevel(line, lineNumber);
		}
		else {
			if ( ! fieldsClosed ) {
				// we are not yet "in fields" or "after fields" => at ENTITY LEVEL 
				String headerLine = processLineEntityLevel(line, lineNumber);
				if ( headerLine != null ) {
					String s = headerLine.trim();
					if ( s.length() > 0 ) {
						entityHeader.add(s);
						this.entityNameParsed = s ; // last line
					}
				}
			}
			else {
				// if "after fields" => ERROR
				processLineAfterFields(line, lineNumber);
			}
		}
	}

	/**
	 * Process the given line at ENTITY LEVEL
	 * @param line
	 * @param lineNumber
	 * @return the word found in the given line (or null if none)
	 */
	protected String processLineEntityLevel(String line, int lineNumber) throws EntityParsingError {
		StringBuilder sb = new StringBuilder();
		boolean firstWordIsDone = false ;
		char previous = 0;
		for (char c : line.toCharArray()) {
			logChar(c);
			if (c > SPACE) {
				switch (c) {

				case '{':
					inFields = true ;
					break;
				
				case '}':
					inFields = false ;
					break;
				
				case '/':
					if (previous == '/') {
						// comment 
						return currentValue(sb);
					}
					break;
				
				default:
					if ( inFields ) {
						throw new EntityParsingError(entityNameFromFileName, "Unexpected char(s) after '{' ", lineNumber);
					}
					else {
						// keep this char to compose Entity Name (if not already done)
						if ( ! firstWordIsDone ) {
							// TODO : check invalid characters ?
							sb.append(c);
						}
						else {
							throw new EntityParsingError(entityNameFromFileName, "Unexpected char(s) after entity name ", lineNumber);
						}
					}
				}
				previous = c;
			}
			else if ( c == SPACE || c == '\t' ) {
				if ( sb.length() > 0 ) { 
					// Word composition in progress...
					firstWordIsDone = true ; // Word finished !
				}
			}
		}
		return currentValue(sb);
	}
	
	/**
	 * Process a line located after the fields definition <br>
	 * This line is supposed to be void or blank or just a comment
	 * @param line
	 * @param lineNumber
	 * @throws EntityParsingError
	 */
	protected void processLineAfterFields(String line, int lineNumber) throws EntityParsingError {
		// remove comment if any 
		String[] parts = line.split("//");
		if ( parts.length > 0 ) {
			// at least 1 char 
			String s = parts[0];
			if ( s.trim().length() > 0 ) {
				throw new EntityParsingError(entityNameFromFileName, "Unexpected line after fields definition", lineNumber);
			}
		}
	}
	
	
	private void resetCurrentField(int lineNumber) {
		if ( currentField == null ) {
			currentField = new FieldPartsBuilder(lineNumber);
		}
		else {
			if ( currentField.isVoid() ) {
				currentField = new FieldPartsBuilder(lineNumber);
			}
		}
	}

	/**
	 * Process the given line for a current level = FIELD LEVEL
	 * @param line
	 * @param lineNumber
	 * @return
	 */
	protected void processLineFieldLevel(String line, int lineNumber) throws EntityParsingError {
		
		boolean inSingleQuote = false ;
		boolean inDoubleQuote = false ;
		
		char previous = 0;
		resetCurrentField(lineNumber);
		log("processLineFieldLevel : #" + lineNumber + " : '" + line + "'");
		log("processLineFieldLevel : #" + lineNumber + " : currentField : " + currentField );
		
		// parse all chararcters in the given line
		for (char c : line.toCharArray()) {
			logChar(c);
			if (c >= SPACE) { // if not a void char
				
				switch (c) {
				case SPACE :   // space = element separator 
					if ( inAnnotations ) {
						if ( inSingleQuote || inDoubleQuote ) {
							keepChar(c);
						}
					}
					else if ( inFields ) {
						if ( ! currentField.isVoid() ) {
							keepChar(c);
						}
					}
					break;

				case ';':   // end of field 
					if ( inAnnotations ) {
						if ( inSingleQuote || inDoubleQuote ) {
							keepChar(c);
						}
						else {
							throw new EntityParsingError(entityNameFromFileName, "Unexpected ';'", lineNumber);
						}
					}
					else if ( inFields ) {
						endOfCurrentField(lineNumber) ;
					}
					else {
						throw new EntityParsingError(entityNameFromFileName, "Unexpected ';'", lineNumber);
					}
					break;
					
				case '{':
					if ( inAnnotations ) {
						if ( inSingleQuote || inDoubleQuote ) {
							keepChar(c);
						}
						else {
							throw new EntityParsingError(entityNameFromFileName, "Unexpected '{'", lineNumber);
						}
					}
					else if ( inFields ) {
						inAnnotations = true ;
						currentField.setInAnnotations(true);
					}
					else {
						throw new EntityParsingError(entityNameFromFileName, "Unexpected '{'", lineNumber);
					}
					
					break;
					
				case '}':
					if ( inAnnotations ) {
						if ( inSingleQuote || inDoubleQuote ) {
							keepChar(c);
						}
						else {
							inAnnotations = false ; // End of annotations
							currentField.setInAnnotations(false);
						}
					}
					else if ( inFields ) {
						if ( currentField.isVoid() ) {
							// no field definition in progress
							inFields = false ; // End of fields
							fieldsClosed = true ; // We were in fields, so it's the end
						}
						else {
							// field definition in progress => no ';' before '}' 
							throw new EntityParsingError(entityNameFromFileName, "';' (end of field) expected before '}'", lineNumber);
						}
					}
					else {
						throw new EntityParsingError(entityNameFromFileName, "Unexpected '}'", lineNumber);
					}
					break;
					
				case '/':
					if ( inSingleQuote || inDoubleQuote ) {
						keepChar(c);
					}
					else {
						if (previous == '/') {
							// comment => end of current line
							return ;
						}
					}
					break;
					
				case '\'': // single quote char (open/close)
					if ( inAnnotations ) {
						if ( ! inDoubleQuote ) {
							inSingleQuote = ! inSingleQuote ; // toggle quote flag
						}
						keepChar(c);
					}
					else {
						throw new EntityParsingError(entityNameFromFileName, "Unexpected single quote", lineNumber);
					}
					break;
					
				case '"': // double quote char (open/close)
					if ( inAnnotations ) {
						if ( ! inSingleQuote ) {
							inDoubleQuote = ! inDoubleQuote ; // toggle quote flag
						}
						keepChar(c);
					}
					else {
						throw new EntityParsingError(entityNameFromFileName, "Unexpected double quote", lineNumber);
					}
					break;

				default:
					keepChar(c);
				}
				previous = c;
			}
		}
	}
	
	private void keepChar(char c) {
		log( '+' );
		currentField.append(c);
	}

	private void endOfCurrentField(int lineNumber) {
		currentField.finished();
		log("\n\n=== FINISHED : " + currentField);
		if ( ! currentField.isVoid() ) {
			fieldsParsed.add(currentField);
		}
		currentField = new FieldPartsBuilder(lineNumber) ; // no current field
		inAnnotations = false ;
	}
	
	private String currentValue(StringBuilder sb) {
		String s = sb.toString();
		return ( s.length() > 0 ? s : null );
	}
}
