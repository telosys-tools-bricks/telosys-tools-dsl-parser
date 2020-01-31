package org.telosys.tools.dsl.parser;

import java.util.List;

import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainType;

public class FieldNameAndTypeParser {
	// private final DomainModel model;
	private final String entityNameFromFileName;
	private final List<String> entitiesNamesInModel;

	// public FieldNameAndTypeParser(String entityNameFromFileName, DomainModel
	// model) {
	// this.entityNameFromFileName = entityNameFromFileName;
	// this.model = model ;
	// }
	/**
	 * Constructor
	 * @param entityNameFromFileName
	 * @param entitiesNames
	 */
	public FieldNameAndTypeParser(String entityNameFromFileName, List<String> entitiesNames) {
		this.entityNameFromFileName = entityNameFromFileName;
		this.entitiesNamesInModel = entitiesNames;
	}

	private void throwParsingException(FieldParts field, String message) {
		String errorMessage = entityNameFromFileName + " : " + message + " [line " + field.getLineNumber() + "]";
		throw new DslParserException(errorMessage);
	}

	public FieldNameAndType parseFieldNameAndType(FieldParts field) {

		String fieldNameAndType = field.getNameAndTypePart();

		checkFieldNameAndType(field, fieldNameAndType);

		String[] parts = fieldNameAndType.split(":");
		String fieldName = parts[0].trim();
		String fieldTypeWithCardinality = parts[1].trim();

		// if ( name.indexOf(' ') >= 0 ) {
		// throwParsingException("Invalid field name", field.getLineNumber());
		// }
		checkFieldName(field, fieldName);
		checkFieldTypeWithCardinality(field, fieldTypeWithCardinality);

		// get field type and cardinality
		String fieldTypeName = getFieldTypeWithoutCardinality(field, fieldTypeWithCardinality);
		int fieldTypeCardinality = getFieldTypeCardinality(field, fieldTypeWithCardinality);
//		checkFieldTypeName(field, fieldTypeName);
		DomainType domainType = getFieldDomainType(field, fieldTypeName);
		// DomainType getFieldDomainType(Field field, String typeName)

		return new FieldNameAndType(fieldName, fieldTypeName, fieldTypeCardinality, domainType);
	}

	/**
	 * Check the string containing field name and field type
	 * 
	 * @param field
	 * @param fieldNameAndType
	 */
	void checkFieldNameAndType(FieldParts field, String fieldNameAndType) {
		int separatorIndex = -1;
		int cardinalityOpen = -1;
		int cardinalityClose = -1;
		for (int i = 0; i < fieldNameAndType.length(); i++) {
			char c = fieldNameAndType.charAt(i);
			switch (c) {
			case ':':
				if (separatorIndex >= 0) {
					throwParsingException(field, "multiple ':'");
				}
				separatorIndex = i;
				break;
			case '[':
				if (cardinalityOpen >= 0) {
					throwParsingException(field, "multiple '['");
				}
				cardinalityOpen = i;
				break;
			case ']':
				if (cardinalityClose >= 0) {
					throwParsingException(field, "multiple ']'");
				}
				cardinalityClose = i;
				break;
			}
		}
		checkPositions(field, separatorIndex, cardinalityOpen, cardinalityClose);
	}

	private void checkPositions(FieldParts field, int separatorIndex, int cardinalityOpen, int cardinalityClose) {
		if (separatorIndex < 0) {
			throwParsingException(field, "':' missing");
		}
		if (cardinalityOpen < 0 && cardinalityClose >= 0) {
			throwParsingException(field, "']' without '['");
		}
		if (cardinalityOpen >= 0 && cardinalityClose < 0) {
			throwParsingException(field, "'[' without ']'");
		}
		if (cardinalityOpen > cardinalityClose) {
			throwParsingException(field, "'[' and ']' inverted");
		}
		if (cardinalityOpen >= 0 && cardinalityOpen < separatorIndex) {
			throwParsingException(field, "'[' before ':'");
		}
	}

	/**
	 * Checks field name validity
	 * 
	 * @param field
	 * @param fieldName
	 */
	void checkFieldName(FieldParts field, String fieldName) {
		if (fieldName.length() == 0) {
			throwParsingException(field, "Field name is missing");
		}
		if (fieldName.indexOf(' ') >= 0) {
			throwParsingException(field, "Invalid field name '" + fieldName + "'");
		}
	}

	/**
	 * Checks field type validity
	 * 
	 * @param field
	 * @param fieldName
	 */
	void checkFieldTypeWithCardinality(FieldParts field, String fieldTypeWithCardinality) {
		if (fieldTypeWithCardinality.length() == 0) {
			throwParsingException(field, "Field type is missing");
		}
		// if (fieldType.indexOf(' ') >= 0) {
		// throwParsingException(field, "Invalid field type '" + fieldType +
		// "'");
		// }
		// TODO : check type validity : int, string, Entity, etc
	}

	/**
	 * Returns the field type without cardinality if any
	 * 
	 * @param field
	 * @param fieldTypeWithCardinality
	 * @return
	 */
	String getFieldTypeWithoutCardinality(FieldParts field, String fieldTypeWithCardinality) {
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
			throwParsingException(field, "field type is missing");
		}
		return fieldType;
	}

	/**
	 * Returns the field type cardinality ( the value defined between [ and ] )
	 * @param field
	 * @param fieldTypeWithCardinality
	 * @return
	 */
	int getFieldTypeCardinality(FieldParts field, String fieldTypeWithCardinality) {
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
					throwParsingException(field, "Invalid cardinality (integer expected)");
				}
				if (cardinality <= 0) {
					throwParsingException(field, "Invalid cardinality (1..N expected)");
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
	private DomainType getFieldDomainType(FieldParts field, String typeName) {
		if (DomainNeutralTypes.exists(typeName)) { 
			// Simple neutral type ( string, int, date, etc )
			return DomainNeutralTypes.getType(typeName);
		} else if (entitiesNamesInModel.contains(typeName)) {
			// Entity type (it is supposed to be known ) eg : 'Book', 'Car', etc
			return new DomainEntity(typeName);
		} else {
			throwParsingException(field, "invalid type '" + typeName + "'");
			return null ;
		}
	}

}
