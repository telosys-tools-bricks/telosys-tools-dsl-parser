/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
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

import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainType;

/**
 * Field description parsing
 * 
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre, Laurent Guerin
 * @version 1.0
 */
public class FieldParser  extends AbstractParser  {

    /**
     * Single parser for all annotations
     */
    private AnnotationParser annotationParser;

    /**
     * Curent Model
     */
    private DomainModel model;

    /**
     * Constructor with context as a DomainModel
     *
     * @param model Context of the current field to parse
     */
    public FieldParser(DomainModel model) {
    	super(LoggerFactory.getLogger(EntityParser.class));
        this.annotationParser = new AnnotationParser();
        this.model = model;
    }

    /**
     * Throws a field parsing exception
     * @param entityNameFromFileName
     * @param fieldDescription
     * @param message
     */
    private void throwFieldParsingError(String entityNameFromFileName, String fieldDescription, String message) {
    	throwParsingError(entityNameFromFileName, "Field error '"+fieldDescription+"' (" + message+")");
    }
    
    /**
     * Parse a single field with its own informations
     * @param entityNameFromFileName
     * @param fieldInfo field definition including annotations, eg 'id:integer', 'id:Country', 'name : integer { @Max(3) }'
     * @return The parsed field
     */
    DomainEntityField parseField(String  entityNameFromFileName, String fieldInfo) {
    	// search colon (':') position
        int colonPosition = fieldInfo.indexOf(':');
        if (colonPosition == -1) {
        	throwFieldParsingError(entityNameFromFileName, fieldInfo, "':' not found");
        }
        
        // field name ( before ':' )
        String fieldName = fieldInfo.substring(0, colonPosition).trim();
        
        // find end of field type
        int end = fieldInfo.length();
        if (fieldInfo.contains("{")) {
            end = fieldInfo.indexOf('{');
        }
        String fieldType = getFieldType(entityNameFromFileName, fieldInfo);
        
        checkFieldName(entityNameFromFileName, fieldInfo, fieldName);
        

        String typeName = fieldInfo.substring(colonPosition + 1, end).trim();
        int cardinality = 1;

        // if multiple cardinality is allowed
        if (isTypeArray(typeName)) {
            int startArray = typeName.lastIndexOf('[');
            int endArray = typeName.lastIndexOf(']');
            // * cardinality
            String figure = typeName.substring(startArray + 1, endArray).trim();
            if (figure.equals("")) {
                cardinality = -1;
                typeName = typeName.substring(0, startArray).trim();
                // specific cardinality
            } else {
                try {
                    cardinality = Integer.parseInt(figure.trim());
                    typeName = typeName.substring(0, startArray).trim();
                } catch (Exception e) {
                    throwFieldParsingError(entityNameFromFileName, fieldInfo, "invalid cardinality");
                }
                if ( cardinality <= 0 ) {
                    throwFieldParsingError(entityNameFromFileName, fieldInfo, "invalid cardinality");
                }
            }
        }

        // the type is required
        if (typeName.length() == 0) {
            throwFieldParsingError(entityNameFromFileName, fieldInfo, "field type is missing");
        }

//        DomainType type = null ;
//
//        if (DomainNeutralTypes.exists(typeName)) { // Simple type ( string, int, date, etc )
//            type = DomainNeutralTypes.getType(typeName);
//
//        } else { // Entity (entity name is supposed to be known )
//            if (!model.getEntityNames().contains(typeName)) {
//            	// Reference to an unknown entity => ERROR
//                throwFieldParsingError(entityNameFromFileName, fieldInfo, "invalid type '" + typeName  + "'" );
//            } else {
//            	// Reference to a valid entity : OK
//                type = model.getEntity(typeName);
//            }
//        }
        DomainType domainType = getFieldDomainType(entityNameFromFileName, fieldInfo, typeName) ;
        
        // create with previous informations
        DomainEntityField field = new DomainEntityField(fieldName, domainType, cardinality);
        List<DomainEntityFieldAnnotation> annotations = this.annotationParser.parseAnnotations(entityNameFromFileName, fieldInfo);
        field.setAnnotationList(annotations);

        return field;
    }

    private void checkFieldName(String entityNameFromFileName, String fieldInfo, String fieldName) {
        if (fieldName.length() == 0) {
        	throwFieldParsingError(entityNameFromFileName, fieldInfo, "field name is missing");
        }
        if (!fieldName.matches("^[\\w]*$")) {
        	throwFieldParsingError(entityNameFromFileName, fieldInfo, "field name must not contains special char");
        }
    }

    /* private */ void checkSyntax(String entityNameFromFileName, String fieldInfo) {
    	int colonIndex = -1 ;
    	int cardinalityOpen = -1 ;
    	int cardinalityClose = -1 ;
    	int annotationsOpen = -1 ;
    	int annotationsClose = -1 ;
        for ( int i = 0 ; i < fieldInfo.length() ; i++ ) {
        	char c = fieldInfo.charAt(i);
        	switch (c) {
        	case ':' :
        		if ( colonIndex >= 0 ) {
        			throwFieldParsingError(entityNameFromFileName, fieldInfo, "multiple ':'");
        		}
        		colonIndex = i ;
        		break;
        	case '[' :
        		if ( cardinalityOpen >= 0 ) {
        			throwFieldParsingError(entityNameFromFileName, fieldInfo, "multiple '['");
        		}
        		cardinalityOpen = i ;
        		break;
        	case ']' :
        		if ( cardinalityClose >= 0 ) {
        			throwFieldParsingError(entityNameFromFileName, fieldInfo, "multiple ']'");
        		}
        		cardinalityClose = i ;
        		break;
        	case '{' :
        		if ( annotationsOpen >= 0 ) {
        			throwFieldParsingError(entityNameFromFileName, fieldInfo, "multiple '{'");
        		}
        		annotationsOpen = i ;
        		break;
        	case '}' :
        		if ( annotationsClose >= 0 ) {
        			throwFieldParsingError(entityNameFromFileName, fieldInfo, "multiple '}'");
        		}
        		annotationsClose = i ;
        		break;
        	}
        }
        if ( colonIndex < 0 ) {
			throwFieldParsingError(entityNameFromFileName, fieldInfo, "':' missing");
        }
        if (   ( cardinalityOpen < 0 ) &&  ! ( cardinalityClose < 0 ) ) {
			throwFieldParsingError(entityNameFromFileName, fieldInfo, "']' without '['");
        }
        if ( ! ( cardinalityOpen < 0 ) &&    ( cardinalityClose < 0 ) ) {
			throwFieldParsingError(entityNameFromFileName, fieldInfo, "'[' without ']'");
        }
        if ( cardinalityOpen > cardinalityClose ) {
			throwFieldParsingError(entityNameFromFileName, fieldInfo, "'[' and ']' inversion");
        }
    }
    
    /* private */ String getFieldType(String entityNameFromFileName, String fieldInfo) {
    	boolean inCardinality = false ;
    	StringBuffer sb = new StringBuffer();
        int start = fieldInfo.indexOf(':') + 1;
        int end = fieldInfo.length() - 1 ;
        for ( int i = start ; i <= end ; i++ ) {
        	char c = fieldInfo.charAt(i);
        	if ( Character.isLetterOrDigit(c) || c == '[' || c == ']' || c == ' ' || c == '\t' ) {
        		if ( c == '[') {
        			inCardinality = true ;
        		}
        		if ( c == ']') {
        			inCardinality = false ;
        		}
        		sb.append(c);
        	}
        	else {
        		if ( inCardinality ) {
        			sb.append(c);
        		}
        		else {
        			break ;
        		}
        	}
        }
        return sb.toString().trim();
    }
    
    /**
     * Returns the DomainType for the given field type name
     * @param entityNameFromFileName
     * @param fieldInfo
     * @param typeName eg 'string', 'date', 'Book', 'Country', etc
     * @return
     */
    private DomainType getFieldDomainType(String entityNameFromFileName, String fieldInfo, String typeName) {
    	DomainType type = null ;
        if (DomainNeutralTypes.exists(typeName)) { // Simple type ( string, int, date, etc )
            type = DomainNeutralTypes.getType(typeName);

        } else { // Entity name (it is supposed to be known ) eg : 'Book', 'Car', etc
            if (!model.getEntityNames().contains(typeName)) {
            	// Reference to an unknown entity => ERROR
                throwFieldParsingError(entityNameFromFileName, fieldInfo, "invalid type '" + typeName  + "'" );
            } else {
            	// Reference to a valid entity : OK
                type = model.getEntity(typeName);
            }
        }
        return type ;
    }
    
    /**
     * Check if the given param is an array of oject
     *
     * @param type The type of the field
     * @return bool - true if it's an array
     */
    private boolean isTypeArray(String type) {
        return type.contains("[") && type.contains("]");
    }
}
