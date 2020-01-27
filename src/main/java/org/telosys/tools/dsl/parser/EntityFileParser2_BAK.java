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
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;

/**
 * Entry point for Telosys DSL entity parser
 *
 * @author Laurent Guerin
 * 
 */
public class EntityFileParser2_BAK { // extends AbstractParser {

//	private static final char TAB   = '\t' ;
	private static final char SPACE = 32;

	// private final String fileName ;
	private final File entityFile;
	private final String entityNameFromFileName;
	// private final DomainModel model ;

	private final List<FieldBuilder> fieldsParsed = new LinkedList<>();
//	private int   position ;
	private FieldBuilder currentField = null ;

	private boolean inFields      = false ;
	private boolean inAnnotations = false ;
	
	
	private void log(String message) {
		System.out.println("LOG:" + message);
	}

	private void throwParsingException(String message) {
		String errorMessage = entityNameFromFileName + " : " + message;
		throw new DslParserException(errorMessage);
	}
	private void throwParsingException(String message, int lineNumber) {
		String errorMessage = entityNameFromFileName + " : " + message + " [line " + lineNumber + "]";
		throw new DslParserException(errorMessage);
	}
	// private void throwParsingException(String message, Exception cause) {
	// String errorMessage = entityName + " : " + message ;
	// throw new DslParserException(errorMessage, cause);
	// }

	public EntityFileParser2_BAK(String fileName) {

		// this.fileName = fileName ;
		// this.model = model ;
		this.entityFile = new File(fileName);
		this.entityNameFromFileName = DslModelUtil.getEntityName(entityFile);
	}

	/**
	 * Parse entity from the given file
	 * 
	 * @param file
	 * @return
	 */
	public DomainEntity parse() {

//		if (!entityFile.exists()) {
//			throwParsingException("File not found");
//		}
//		log("parse() : File : " + entityFile.getAbsolutePath());
		DomainEntity domainEntity = new DomainEntity(entityNameFromFileName);

//		position = Const.IN_ENTITY;
//		int lineNumber = 0;
//		try (BufferedReader br = new BufferedReader(new FileReader(entityFile))) {
//			String line;
//			while ((line = br.readLine()) != null) {
//				lineNumber++;
//				processLine(line, lineNumber);
//				// read next line
//			}
//		} catch (IOException e) {
//			throwParsingException("IOException");
//		}
		List<FieldBuilder> fields = parseFields() ; // rename parseFile()
		// TODO : parseAllFields(domainEntity);
		return domainEntity;
	}
	
	protected List<FieldBuilder> parseFields() {
		if (!entityFile.exists()) {
			throwParsingException("File not found");
		}
		log("parse() : File : " + entityFile.getAbsolutePath());
		
//		position = Const.IN_ENTITY;
		
		int lineNumber = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(entityFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				lineNumber++;
				processLine(line, lineNumber);
				// read next line
			}
		} catch (IOException e) {
			throwParsingException("IOException");
		}
		return this.fieldsParsed ;
	}
	
	protected void processLine(String line, int lineNumber) {
//		Field currentField = null ; // no current field at the beginning
		System.out.println("\n-------------------------------------------");
//		System.out.println("#" + lineNumber + "("+position+") : " + line);
		System.out.println("#" + lineNumber + " : " + line);
//		System.out.println("position = " + position);
//		switch ( position ) {
//		case Const.IN_ENTITY:
//			String s = processLineEntityLevel(line, lineNumber);
//			if ( s != null ) {
//				//String entityNameInText = s ;
//				System.out.println("\n=== ENTITY : " + s);
//			}
//			break;
//		case Const.IN_FIELDS:
//		case Const.IN_ANNOTATIONS:
////			currentField = processLineFieldLevel(line, lineNumber);
//			processLineFieldLevel(line, lineNumber);
//			System.out.println("processLineFieldLevel return : Field = " + currentField );
//			break;
//		default:
//			break;
//		}
		
		if ( inFields ) {
			processLineFieldLevel(line, lineNumber);
			System.out.println("processLineFieldLevel return : Field = " + currentField );
		}
		else {
			String s = processLineEntityLevel(line, lineNumber);
			if ( s != null ) {
				System.out.println("\n=== ENTITY : " + s);
			}
		}
	}
	/**
	 * Process the given line for a current level = ENTITY LEVEL
	 * @param line
	 * @param lineNumber
	 * @return the entity name if found or null if none
	 */
	protected String processLineEntityLevel(String line, int lineNumber) {
		StringBuilder sb = new StringBuilder();
		char previous = 0;
		for (char c : line.toCharArray()) {
			System.out.print( "[" + c+ "]");
			if (c > SPACE) {
				switch (c) {
				case '{':
//					position++;
					inFields = true ;
					break;
				case '}':
//					position--;
					inFields = false ;
					break;
				case '/':
					if (previous == '/') {
						// comment 
						return currentValue(sb);
					}
					break;
				default:
					//System.out.print("append("+c+")");
					sb.append(c);
				}
				previous = c;
			}
//			if ( position != Const.IN_ENTITY ) {
//				break;
//			}
		}
		return currentValue(sb);
	}
	
//	private Field getCurrentField(Field currentField, Field originalField, int lineNumber) {
//		if (currentField != null) {
//			return currentField;
//		}
//		else if ( originalField != null ) {
//			return originalField;
//		}
//		else {
//			return new Field(lineNumber);
//		}
//	}
//	private Field getCurrentField(Field originalField, int lineNumber) {
//		if ( originalField != null ) {
//			return originalField;
//		}
//		else {
//			return new Field(lineNumber);
//		}
//	}
	
	private void resetCurrentField(int lineNumber) {
		if ( currentField == null ) {
			currentField = new FieldBuilder(lineNumber);
		}
		else {
			if ( currentField.isVoid() ) {
				currentField = new FieldBuilder(lineNumber);
			}
		}
	}
//	private void endOfCurrentField() {
//		currentField.finished();
//		System.out.println("\n\n=== FINISHED : " + currentField);
//		currentField = null ; // no current field
//		position = Const.IN_FIELDS;
//	}

	/**
	 * Process the given line for a current level = FIELD LEVEL
	 * @param line
	 * @param lineNumber
	 * @return
	 */
//	protected Field processLineFieldLevel(String line, int lineNumber) {
	protected void processLineFieldLevel(String line, int lineNumber) {
		
		boolean inSingleQuote = false ;
		boolean inDoubleQuote = false ;
		
		char previous = 0;
//		Field currentField = null ;
//		Field currentField = getCurrentField(originalField, lineNumber);
		resetCurrentField(lineNumber);
		System.out.println("processLineFieldLevel : #" + lineNumber + " : '" + line + "'");
		System.out.println("processLineFieldLevel : #" + lineNumber + " : Field : " + currentField );

		
		// parse all chararcters in the given line
		for (char c : line.toCharArray()) {
//			if ( c == TAB ) {
//				c = SPACE ;
//			}
			System.out.print( "[" + c + "]");
			if (c >= SPACE) { // if not a void char
				
				switch (c) {
				case SPACE :   // end of field 
//					if ( inSingleQuote || inDoubleQuote ) {
//						System.out.print( "+" );
//						currentField.append(c);
//					}
					if ( inSingleQuote || inDoubleQuote ) {
						keepChar(c);
					}
					break;

				case ';':   // end of field 
//					if ( position == Const.IN_FIELDS ) {
//						currentField.finished();
//						// TODO : check field
//						// if invalid => ERROR unexpected ';'
//						System.out.println("\n\n=== FINISHED : " + currentField);
//						fieldsParsed.add(currentField);
////						currentField = null ; // no current field
//						currentField = new Field(lineNumber) ; // no current field
//						position = Const.IN_FIELDS;
//					}
//					else {
//						System.out.print( "+" );
//						currentField.append(c);
//					}

					if ( inAnnotations ) {
						if ( inSingleQuote || inDoubleQuote ) {
							keepChar(c);
						}
						else {
							throwParsingException("Unexpected ';'", lineNumber) ;
						}
					}
					else if ( inFields ) {
						endOfCurrentField() ;
					}
					else {
						throwParsingException("Unexpected ';'", lineNumber) ;
					}
					break;
					
				case '{':
//					if ( inSingleQuote || inDoubleQuote ) {
//						System.out.print( "+" );
//						currentField.append(c);
//					}
//					else {
//						if ( position == Const.IN_FIELDS ) {
//							position++;
//							currentField.setPosition(position);
//						}
//						else {
//							System.out.print( "+" );
//							currentField.append(c);
//						}
//					}

					if ( inAnnotations ) {
						if ( inSingleQuote || inDoubleQuote ) {
							keepChar(c);
						}
						else {
							throwParsingException("Unexpected '{'", lineNumber) ;
						}
					}
					else if ( inFields ) {
						inAnnotations = true ;
						currentField.setInAnnotations(true);
					}
					else {
						throwParsingException("Unexpected '{'", lineNumber) ;
					}
					
					break;
					
				case '}':
//					if ( inSingleQuote || inDoubleQuote ) {
//						System.out.print( "+" );
//						currentField.append(c);
//					}
//					else {
//						if ( position == Const.IN_ANNOTATIONS ) {
//							position--;
//							currentField.setPosition(position);
//						}
//						else {
//							System.out.print( "+" );
//							currentField.append(c);
//						}
//					}
					
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
						inFields = false ; // End of fields
					}
					else {
						throwParsingException("Unexpected '}'", lineNumber) ;
					}
					break;
					
				case '/':
					if ( inSingleQuote || inDoubleQuote ) {
//						System.out.print( "+" );
//						currentField.append(c);
						keepChar(c);
					}
					else {
//						if ( position != Const.IN_ANNOTATIONS ) {
							if (previous == '/') {
								// comment => end of current line
								return ;
							}
//						}
					}
					break;
					
				case '\'': // single quote char (open/close)
//					if ( position == Const.IN_ANNOTATIONS ) {
					if ( inAnnotations ) {
						if ( ! inDoubleQuote ) {
							inSingleQuote = ! inSingleQuote ; // toggle quote flag
						}
//						currentField.append(c);
						keepChar(c);
					}
					else {
						throw new DslParserException("Unexpected single quote");
					}
					break;
					
				case '"': // double quote char (open/close)
////					if ( position == Const.IN_ANNOTATIONS ) {
//					if ( inAnnotations ) {
//						if ( ! inSingleQuote ) {
//							// toggle quote flag
//							inDoubleQuote = ! inDoubleQuote ; // toggle quote flag
//						}
////						if ( inDoubleQuote ) {
////							inDoubleQuote = false ; // close single quote
////						}
////						else {
////							inDoubleQuote = true ; // open single quote
////						}
////						currentField.append(c);
//						keepChar(c);
//					}
//					else {
//						throw new DslParserException("Unexpected double quote");
//					}

					if ( inAnnotations ) {
						if ( ! inSingleQuote ) {
							inDoubleQuote = ! inDoubleQuote ; // toggle quote flag
						}
						keepChar(c);
					}
					else {
						throw new DslParserException("Unexpected double quote");
					}
					
					break;

				default:
//					System.out.print( "+" );
//					currentField.append(c);
					keepChar(c);
				}
				previous = c;
			}
//			if ( position != IN_ENTITY ) {
//				break;
//			}
		}
//		return currentField;
	}
	
	private void keepChar(char c) {
		System.out.print( "+" );
		currentField.append(c);
	}
	private void keepCharOnlyIfInQuote(char c) {
		System.out.print( "+" );
		currentField.append(c);
	}

	private void endOfCurrentField() {
		currentField.finished();
		// TODO : check field
		// if invalid => ERROR unexpected ';'
		System.out.println("\n\n=== FINISHED : " + currentField);
		fieldsParsed.add(currentField);
	//	currentField = null ; // no current field
		currentField = new FieldBuilder(-1) ; // no current field
		inAnnotations = false ;
//		position = Const.IN_FIELDS;
	}
	
	private String currentValue(StringBuilder sb) {
		String s = sb.toString();
		return ( s.length() > 0 ? s : null );
	}
}
