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

import java.util.List;

import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntityType;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainType;

public class FieldNameAndTypeParser {

	private final String entityNameFromFileName;
	private final List<String> entitiesNamesInModel;

	/**
	 * Constructor
	 * @param entityNameFromFileName
	 * @param entitiesNames
	 */
	public FieldNameAndTypeParser(String entityNameFromFileName, List<String> entitiesNames) {
		this.entityNameFromFileName = entityNameFromFileName;
		this.entitiesNamesInModel = entitiesNames;
	}

	public FieldNameAndType parseFieldNameAndType(FieldParts field) throws FieldParsingError {

		String fieldNameAndType = field.getNameAndTypePart();

		checkFieldNameAndType(fieldNameAndType);

		String[] parts = fieldNameAndType.split(":");
		String fieldName = parts[0].trim();
		String fieldTypeWithCardinality = parts[1].trim();

		checkFieldName(fieldNameAndType, fieldName);
		checkFieldTypeWithCardinality(fieldNameAndType, fieldTypeWithCardinality);

		// get field type and cardinality
		String fieldTypeName = getFieldTypeWithoutCardinality(fieldNameAndType, fieldTypeWithCardinality);
		int fieldTypeCardinality = getFieldTypeCardinality(fieldNameAndType, fieldTypeWithCardinality);
		DomainType domainType = getFieldDomainType(fieldNameAndType, fieldTypeName);

		return new FieldNameAndType(fieldName, fieldTypeName, fieldTypeCardinality, domainType);
	}

	/**
	 * Check the string containing field name and field type
	 * 
	 * @param field 
	 * @param fieldNameAndType
	 */
	void checkFieldNameAndType(String fieldNameAndType) throws FieldParsingError {
		int separatorIndex = -1;
		int cardinalityOpen = -1;
		int cardinalityClose = -1;

		for (int i = 0; i < fieldNameAndType.length(); i++) {
			char c = fieldNameAndType.charAt(i);
			switch (c) {
			case ':':
				if (separatorIndex >= 0) {
					throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "multiple ':'");
				}
				separatorIndex = i;
				break;
			case '[':
				if (cardinalityOpen >= 0) {
					throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "multiple '['");
				}
				cardinalityOpen = i;
				break;
			case ']':
				if (cardinalityClose >= 0) {
					throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "multiple ']'");
				}
				cardinalityClose = i;
				break;
			}
		}
		checkPositions(fieldNameAndType, separatorIndex, cardinalityOpen, cardinalityClose);
	}

	private void checkPositions(String fieldNameAndType, int separatorIndex, 
			int cardinalityOpen, int cardinalityClose) throws FieldParsingError {
		if (separatorIndex < 0) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "':' missing");
		}
		if (cardinalityOpen < 0 && cardinalityClose >= 0) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "']' without '['");
		}
		if (cardinalityOpen >= 0 && cardinalityClose < 0) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "'[' without ']'");
		}
		if (cardinalityOpen > cardinalityClose) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "'[' and ']' inverted");
		}
		if (cardinalityOpen >= 0 && cardinalityOpen < separatorIndex) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "'[' before ':'");
		}
	}

	/**
	 * Checks field name validity
	 * 
	 * @param field
	 * @param fieldName
	 */
	void checkFieldName(String fieldNameAndType, String fieldName) throws FieldParsingError {
		if (fieldName.length() == 0) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "field name is missing");
		}
		if (fieldName.indexOf(' ') >= 0) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "invalid field name '" + fieldName + "'");
		}
	}

	/**
	 * Check field type presence
	 * @param fieldNameAndType
	 * @param fieldTypeWithCardinality
	 * @throws FieldParsingError
	 */
	void checkFieldTypeWithCardinality(String fieldNameAndType, String fieldTypeWithCardinality) throws FieldParsingError {
		if (fieldTypeWithCardinality.length() == 0) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "field type is missing");
		}
	}

	/**
	 * Returns the field type without cardinality if any
	 * @param fieldNameAndType
	 * @param fieldTypeWithCardinality
	 * @return
	 * @throws FieldParsingError
	 */
	String getFieldTypeWithoutCardinality(String fieldNameAndType, String fieldTypeWithCardinality) throws FieldParsingError {
		StringBuilder sb = new StringBuilder();
		int start = fieldTypeWithCardinality.indexOf(':') + 1;
		int end = fieldTypeWithCardinality.length() - 1;
		for (int i = start; i <= end; i++) {
			char c = fieldTypeWithCardinality.charAt(i);
			if (c == '[') {
				break;
			} else {
				sb.append(c);
			}
		}
		String fieldType = sb.toString().trim();
		if (fieldType.length() == 0) {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "field type is missing");
		}
		return fieldType;
	}

	/**
	 * Returns the field type cardinality ( the value defined between [ and ] )
	 * @param fieldNameAndType
	 * @param fieldTypeWithCardinality
	 * @return
	 * @throws FieldParsingError
	 */
	int getFieldTypeCardinality(String fieldNameAndType, String fieldTypeWithCardinality) throws FieldParsingError {
		if (fieldTypeWithCardinality.contains("[") && fieldTypeWithCardinality.contains("]")) {
			int startArray = fieldTypeWithCardinality.lastIndexOf('[');
			int endArray = fieldTypeWithCardinality.lastIndexOf(']');
			// * cardinality
			String figure = fieldTypeWithCardinality.substring(startArray + 1, endArray).trim();
			if (figure.length() == 0) {
				// Void : "[]" => undefined cardinality
				return -1;
			} else {
				// specific cardinality : "[something]" => try to cast
				int cardinality = 0;
				try {
					cardinality = Integer.parseInt(figure.trim());
				} catch (Exception e) {
					throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "invalid cardinality (integer expected)");
				}
				if (cardinality <= 0) {
					throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "invalid cardinality (1..N expected)");
				}
				return cardinality;
			}
		} else {
			return 1;
		}
	}

	/**
	 * Returns the DomainType for the given field type name
	 * @param field
	 * @param typeName eg 'string', 'date', 'Book', 'Country', etc
	 * @return
	 */
	private DomainType getFieldDomainType(String fieldNameAndType, String typeName) throws FieldParsingError {
		if (DomainNeutralTypes.exists(typeName)) { 
			// Simple neutral type ( string, int, date, etc )
			return DomainNeutralTypes.getType(typeName);
		} else if (entitiesNamesInModel.contains(typeName)) {
			// Entity type (it is supposed to be known ) eg : 'Book', 'Car', etc
//			return new DomainEntity(typeName); 
			return new DomainEntityType(typeName); 
		} else {
			throw new FieldParsingError(entityNameFromFileName, fieldNameAndType, "invalid type '" + typeName + "'");
		}
	}

}
