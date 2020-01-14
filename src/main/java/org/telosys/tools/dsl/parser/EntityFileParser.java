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

import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;

/**
 * Entry point for Telosys DSL entity parser
 *
 * @author Laurent Guerin
 * 
 */
public class EntityFileParser { // extends AbstractParser {

	private static final char SPACE = 32;
	private static final int IN_ENTITY = 1;
	private static final int IN_FIELDS = 2;
	private static final int IN_ANNOTATIONS = 3;

	// private final String fileName ;
	private final File entityFile;
	private final String entityName;
	// private final DomainModel model ;

	private int position ;

	private void log(String message) {
		System.out.println("LOG:" + message);
	}

	private void throwParsingException(String message) {
		String errorMessage = entityName + " : " + message;
		throw new DslParserException(errorMessage);
	}
	// private void throwParsingException(String message, Exception cause) {
	// String errorMessage = entityName + " : " + message ;
	// throw new DslParserException(errorMessage, cause);
	// }

	public EntityFileParser(String fileName) {

		// this.fileName = fileName ;
		// this.model = model ;
		this.entityFile = new File(fileName);
		this.entityName = DslModelUtil.getEntityName(entityFile);
	}

	/**
	 * Parse entity from the given file
	 * 
	 * @param file
	 * @return
	 */
	protected DomainEntity parse() {

		if (!entityFile.exists()) {
			throwParsingException("File not found");
		}
		log("parse() : File : " + entityFile.getAbsolutePath());
		DomainEntity domainEntity = new DomainEntity(entityName);

		position = IN_ENTITY;
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

		return domainEntity;
	}
	protected void processLine(String line, int lineNumber) {
		System.out.println(lineNumber + " : " + line);
		switch ( position ) {
		case IN_ENTITY:
			String entityName = processLineEntity(line, lineNumber);
			System.out.println("entityName : '" + entityName + "'");
			break;
		case IN_FIELDS:
			break;
		case IN_ANNOTATIONS:
			break;
		default:
			break;
		}
	}
	protected String processLineEntity(String line, int lineNumber) {
		StringBuilder sb = new StringBuilder();
		char previous = 0;
		for (char c : line.toCharArray()) {
//			System.out.print(c);
			if (c > SPACE) {
				switch (c) {
				case '{':
					position++;
					break;
				case '}':
					position--;
					break;
				case '/':
					if (previous == '/') {
						// comment 
						return null;
					}
					break;
				default:
					sb.append(c);
				}
				previous = c;
			}
			if ( position != IN_ENTITY ) {
				break;
			}
		}
		String s = sb.toString();
		return ( s.length() > 0 ? s : null );
	}

}
