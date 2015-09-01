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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.*;

import java.util.List;

/**
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre
 * @version 1.0
 */
public class FieldParser {

    /**
     * Single parser for all annotations
     */
    private AnnotationParser annotationParser;

    /**
     * Logger for tracing all events
     */
    private Logger logger;

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
        this.annotationParser = new AnnotationParser();
        this.logger = LoggerFactory.getLogger(FieldParser.class);
        this.model = model;
    }

    /**
     * Parse a single field with its own informations
     * @param filename
     * @param fieldInfo
     * @return The parsed field
     */
    DomainEntityField parseField(String  filename, String fieldInfo) {
        int startDescription = fieldInfo.indexOf(':');
        if (startDescription == -1) {
            String errorMessage = "You must specify the type of the field. The separator between the name of the field and its type is ':'";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }
        String name = fieldInfo.substring(0, startDescription).trim();

        // description and field is required
        if (!name.matches("^[\\w]*$")) {
            String errorMessage = "The name of the fields must not contains special char " + name + " (file '" + filename+"')";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }
        if (name.length() == 0) {
            String errorMessage = "The name of the field is missing (file '" + filename+"')";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }

        // find end of descritpion
        int end;
        if (fieldInfo.contains("{")) {
            end = fieldInfo.indexOf('{');
        } else {
            end = fieldInfo.length();
        }

        String typeName = fieldInfo.substring(startDescription + 1, end).trim();
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
                    String errorMessage = "Invalid cardinality for " + typeName + " (file '" + filename+"')";
                    this.logger.error(errorMessage);
                    throw new EntityParserException(errorMessage + "\n Documentation : " + e);
                }
            }
        }

        // the type is required
        if (typeName.length() == 0) {
            String errorMessage = "The type of the field is missing";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }

        DomainType type;
//        // enum
//        if (this.isTypeEnum(typeName)) {
//            if (!this.model.getEnumerationNames().contains(typeName.substring(1))) {
//                String errorMessage = "The enumeration " + typeName.substring(1) + " does not exist";
//                this.logger.error(errorMessage);
//                throw new EntityParserException(errorMessage);
//            } else {
//                type = this.model.getEnumeration(typeName.substring(1));
//            }
//            // other simple type
//        } else if (DomainNeutralTypes.exists(typeName)) {
//            type = DomainNeutralTypes.getType(typeName);
//
//            // from other entity
//        } else {
//            if (!model.getEntityNames().contains(typeName)) {
//                String errorMessage = "The type of the field is incorrect "+typeName;
//                this.logger.error(errorMessage);
//                throw new EntityParserException(errorMessage);
//            } else {
//                type = model.getEntity(typeName);
//            }
//        }

        if (DomainNeutralTypes.exists(typeName)) { // Simple type ( string, int, date, etc )
            type = DomainNeutralTypes.getType(typeName);

        } else { // Entity (entity name is supposed to be known )
            if (!model.getEntityNames().contains(typeName)) {
            	// Reference to an unknown entity => ERROR
                String errorMessage = "Invalid type '" + typeName  + "' for field '" + name + "' (file '" + filename+"')";
                this.logger.error(errorMessage);
                throw new EntityParserException(errorMessage);
            } else {
            	// Reference to a valid entity : OK
                type = model.getEntity(typeName);
            }
        }

        // create with previous informations
        DomainEntityField field = new DomainEntityField(name, type, cardinality);
        List<DomainEntityFieldAnnotation> annotations = this.annotationParser.parseAnnotations(fieldInfo);
        field.setAnnotationList(annotations);

        return field;
    }

//    /**
//     * Check if the given param is an enum with the specific char
//     *
//     * @param type The type of the field
//     * @return bool - true if it's an enum
//     */
//    private boolean isTypeEnum(String type) {
//        return type.startsWith("#");
//    }

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
