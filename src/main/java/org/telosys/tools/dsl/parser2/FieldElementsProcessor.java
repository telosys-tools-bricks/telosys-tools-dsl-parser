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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntityType;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainTag;
import org.telosys.tools.dsl.parser.model.DomainType;

/**
 * Telosys DSL field elements processor
 *
 * @author Laurent GUERIN
 * 
 */
public class FieldElementsProcessor {
	
	private final String entityName;
	private final List<String> entitiesNamesInModel;

	/**
	 * Constructor
	 * @param entityName
	 * @param entitiesNames
	 */
	public FieldElementsProcessor(String entityName, List<String> entitiesNames) {
		super();
		this.entityName = entityName;
		this.entitiesNamesInModel = entitiesNames;		
	}

	/**
	 * Process the given field elements
	 * @param elements
	 * @return the new field (or null if it's impossible to create a new field)
	 * @throws ParsingError
	 */
	public DomainField processFieldElements(List<Element> elements, ParsingErrors errors) {
		
		// Build field with NAME and TYPE
		DomainField field;
		try {
			field = buildField(elements);
		} catch (ParserError e) {
			errors.addError(e);
			return null;
		}
		
		// Extract additional elements for annotations and tags
		List<Element> additionalElements;
		try {
			additionalElements = extractAdditionalElements(field.getName(), elements);
		} catch (ParserError e) {
			errors.addError(e);
			return field;
		}
		
		// Add annotations and tags if any
		for ( Element element : additionalElements ) {
			try {
				processAnnotationOrTag(field, element) ;
			} catch (ParserError e) {
				errors.addError(e);
			}
		}
		return field;
	}
	
	private DomainField buildField(List<Element> elements) throws ParserError {
		if ( elements.size() >= 3 ) {
			String fieldName = parseFieldName(elements.get(0));
			parseSeparator(fieldName, elements.get(1));
			DomainType fieldType = parseFieldType(fieldName, elements.get(2));
			return new DomainField(fieldName, fieldType);
		}
		else {
			// ERROR
			String fieldName = "" ;
			if ( ! elements.isEmpty() ) {
				fieldName = elements.get(0).getContent();
			}
			throw new ParserError(entityName, 0, fieldName, "invalid field definition");
		}
	}
	
	private String parseFieldName(Element element) throws ParserError {
		String fieldName = element.getContent();
		for ( char c : fieldName.toCharArray() ) {
			if ( ! ( Character.isLetterOrDigit(c) || c == '_') ) {
				throw new ParserError(entityName, element.getLineNumber(), fieldName, "invalid field name (char '" + c + "')");
			}
		}
		return fieldName; // Field name is OK 
	}
	
	private void parseSeparator(String fieldName, Element element) throws ParserError {
		String s = element.getContent();
		if ( ! ":".equals(s) ) {
			throw new ParserError(entityName, element.getLineNumber(), fieldName, "invalid separator '" + s + "' (':' expected)");
		}
	}
	
	private DomainType parseFieldType(String fieldName, Element element) throws ParserError {
		String typeName = element.getContent();
		if (DomainNeutralTypes.exists(typeName)) { 
			// Simple neutral type ( string, int, date, etc )
			return DomainNeutralTypes.getType(typeName);
		} else if (entitiesNamesInModel.contains(typeName)) {
			// Entity type (it is supposed to be known ) eg : 'Book', 'Car', etc
			return new DomainEntityType(typeName); 
		} else {
			throw new ParserError(entityName, element.getLineNumber(), fieldName, "invalid type '" + typeName + "'");
		}
	}
	
	protected List<Element> extractAdditionalElements(String fieldName, List<Element> elements) throws ParserError {
		List<Element> selection = new LinkedList<>();
		
		boolean inAnnotationsAndTags = false;
		int openingBracePosition = 0 ; 
		int closingBracePosition = 0 ;
		int position = 0;
		for ( Element element : elements ) {
			position++;
			if ( position > 3 ) { // Skip "field name", ":" and "field type"
				if ( element.equals("{") ) {
					if ( openingBracePosition != 0 ) {
						throw new ParserError(entityName, element.getLineNumber(), fieldName, "multiple '{' ");
					}
					inAnnotationsAndTags = true ;
					openingBracePosition = position ;
				}
				else if ( element.equals("}") ) {
					if ( closingBracePosition != 0 ) {
						throw new ParserError(entityName, element.getLineNumber(), fieldName, "multiple '}' ");
					}
					inAnnotationsAndTags = false ;
					closingBracePosition = position ;
				}
				else {
					if ( inAnnotationsAndTags ) {
						selection.add(element);
					}
					else {
						// ERROR
						throw new ParserError(entityName, element.getLineNumber(), fieldName, 
								"unexpected element '" + element.getContent() + "' out of {...}");
					}
				}
			}
		}
		return selection;
	}
	
	private void processAnnotationOrTag(DomainField field, Element element) throws ParserError {
		if ( element.startsWithAnnotationPrefix() ) {
			AnnotationProcessor annotationParser = new AnnotationProcessor(entityName, field.getName());
			DomainAnnotation annotation = annotationParser.parseAnnotation(element);
			field.addAnnotation(annotation);
		}
		else if ( element.startsWithTagPrefix() ) {
			TagProcessor tagParser = new TagProcessor(entityName, field.getName());
			DomainTag tag = tagParser.parseTag(element);
			field.addTag(tag);
		}
		else {
			// ERROR
			throw new ParserError(entityName, element.getLineNumber(), field.getName(), 
					"invalid element '" + element.getContent() + "'" + "(annotation or tag expected)");
		}
	}
	
}
