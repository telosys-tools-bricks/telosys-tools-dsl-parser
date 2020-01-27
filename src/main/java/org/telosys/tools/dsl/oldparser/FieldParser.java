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
package org.telosys.tools.dsl.oldparser;

import java.util.List;

import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;
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
     * Single parser for field annotations
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
    	//super(LoggerFactory.getLogger(FieldParser.class));
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
    	throwParsingError(entityNameFromFileName, "Field error '" + fieldDescription + "' (" + message + ")");
    }
    
    /**
     * Parse a single field with its own informations
     * @param entityNameFromFileName
     * @param fieldInfo field definition including annotations, eg 'id:integer', 'id:Country', 'name : integer { @Max(3) }'
     * @return The parsed field
     */
    protected DomainEntityField parseField(String  entityNameFromFileName, String fieldInfo) {
    	
    	checkSyntax(entityNameFromFileName, fieldInfo);
    	
        String fieldName = getFieldName(entityNameFromFileName, fieldInfo);
        
        String fieldType = getFieldType(entityNameFromFileName, fieldInfo);

        int fieldCardinality = getCardinality(entityNameFromFileName, fieldInfo);
        
        DomainType domainType = getFieldDomainType(entityNameFromFileName, fieldInfo, fieldType) ;
        
        String annotationsString = getAnnotations(entityNameFromFileName, fieldInfo);
        
        List<DomainAnnotationOrTag> annotationsList = 
        		this.annotationParser.parseAnnotations(entityNameFromFileName, fieldName, annotationsString);

        // create with previous informations
        DomainEntityField field = new DomainEntityField(fieldName, domainType, fieldCardinality);
        field.setAnnotationList(annotationsList);

        return field;
    }

    /**
     * Checks field name validity
     * @param entityNameFromFileName
     * @param fieldInfo
     * @param fieldName
     */
    private void checkFieldName(String entityNameFromFileName, String fieldInfo, String fieldName) {
        if (fieldName.length() == 0) {
        	throwFieldParsingError(entityNameFromFileName, fieldInfo, "field name is missing");
        }
        if (!fieldName.matches("^[\\w]*$")) {
        	throwFieldParsingError(entityNameFromFileName, fieldInfo, "field name must not contains special char");
        }
    }
    
    /**
     * Checks the global syntax of the field definition : <br>
     * Position and coherence with ':', '[]' and '{}'
     * @param entityNameFromFileName
     * @param fieldInfo
     */
    /* package */ void checkSyntax(String entityNameFromFileName, String fieldInfo) {
    	if ( fieldInfo.trim().length() == 0 ) {
			throwFieldParsingError(entityNameFromFileName, fieldInfo, "field description is void");
    	}
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
        checkOpenClose( entityNameFromFileName, fieldInfo, cardinalityOpen, cardinalityClose, '[', ']' ); 
        checkOpenClose( entityNameFromFileName, fieldInfo, annotationsOpen, annotationsClose, '{', '}' ); 
        if ( cardinalityOpen >= 0 && cardinalityOpen < colonIndex ) {
			throwFieldParsingError(entityNameFromFileName, fieldInfo, "'[' before ':'");
        }
        if ( annotationsOpen >= 0 && annotationsOpen < colonIndex ) {
			throwFieldParsingError(entityNameFromFileName, fieldInfo, "'{' before ':'");
        }
    }
    
    private void checkOpenClose(String entityNameFromFileName, String fieldInfo, int openIndex, int closeIndex, char openChar, char closeChar ) {
        if (   ( openIndex < 0 ) &&  ! ( closeIndex < 0 ) ) {
    		throwFieldParsingError(entityNameFromFileName, fieldInfo, "'" + closeChar + "' without '" + openChar + "'" );
        }
        if ( ! ( openIndex < 0 ) &&    ( closeIndex < 0 ) ) {
    		throwFieldParsingError(entityNameFromFileName, fieldInfo, "'" + openChar + "' without '" + closeChar + "'" );
        }
        if ( openIndex > closeIndex ) {
    		throwFieldParsingError(entityNameFromFileName, fieldInfo, "'" + openChar + "' and '" + closeChar + "' inverted" );
        }
    }
    
    /**
     * Returns the field name ( located before ':' )
     * @param entityNameFromFileName
     * @param fieldInfo
     * @return
     */
    /* package */ String getFieldName(String entityNameFromFileName, String fieldInfo) {
        // search colon (':') position
        int colonPosition = fieldInfo.indexOf(':');
        String fieldName = fieldInfo.substring(0, colonPosition).trim();
        checkFieldName(entityNameFromFileName, fieldInfo, fieldName);
        return fieldName ;
    }
    
    /**
     * Returns the field type (without its cardinality), e.g. : "string", "Book", etc
     * @param entityNameFromFileName
     * @param fieldInfo
     * @return
     */
    /* package */ String getFieldType(String entityNameFromFileName, String fieldInfo) {
    	StringBuffer sb = new StringBuffer();
        int start = fieldInfo.indexOf(':') + 1;
        int end = fieldInfo.length() - 1 ;
        for ( int i = start ; i <= end ; i++ ) {
        	char c = fieldInfo.charAt(i);
    		if ( c == '[' || c == '{' ) {
    			break;
    		}
    		else {
        		sb.append(c);
    		}
        }
        String fieldType = sb.toString().trim();
        // the type is required
        if (fieldType.length() == 0) {
            throwFieldParsingError(entityNameFromFileName, fieldInfo, "field type is missing");
        }
        return fieldType ;
    }
    
    /**
     * Returns the field cardinality ( the value defined between [ and ] ) 
     * @param entityNameFromFileName
     * @param fieldInfo
     * @return
     */
    /* package */ int getCardinality(String entityNameFromFileName, String fieldInfo) {
    	if ( fieldInfo.contains("[") && fieldInfo.contains("]") ) {
            int startArray = fieldInfo.lastIndexOf('[');
            int endArray = fieldInfo.lastIndexOf(']');
            // * cardinality
            String figure = fieldInfo.substring(startArray + 1, endArray).trim();
            if (figure.length() == 0 ) {
            	// Void : "[]" => undefined cardinality
                return -1;
            } else {
                // specific cardinality : "[something]" => try to cast 
            	int cardinality = 0 ;
                try {
                    cardinality = Integer.parseInt(figure.trim());
                } catch (Exception e) {
                    throwFieldParsingError(entityNameFromFileName, fieldInfo, "invalid cardinality");
                }
                if ( cardinality <= 0 ) {
                    throwFieldParsingError(entityNameFromFileName, fieldInfo, "invalid cardinality");
                }
                return cardinality ;
            }    		
    	}
    	else {
    		return 1 ;
    	}
    }
    
    /**
     * Returns the field annotations string without '{' and '}' (or a void string if none)
     * @param entityNameFromFileName
     * @param fieldInfo
     * @return
     */
    /* package */ String getAnnotations(String entityNameFromFileName, String fieldInfo) {
        // get index of first and last open brackets
        int openIndex = fieldInfo.indexOf('{');
        int closeIndex = fieldInfo.lastIndexOf('}');
		if ( openIndex >= 0 ) { // field has annotations
			return fieldInfo.substring(openIndex+1, closeIndex).trim();
		}
		return "";
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
    
}
