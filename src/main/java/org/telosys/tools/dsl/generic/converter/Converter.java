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
package org.telosys.tools.dsl.generic.converter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.telosys.tools.commons.ConsoleLogger;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.dsl.generic.model.GenericAttribute;
import org.telosys.tools.dsl.generic.model.GenericEntity;
import org.telosys.tools.dsl.generic.model.GenericLink;
import org.telosys.tools.dsl.generic.model.GenericModel;
import org.telosys.tools.dsl.generic.model.GenericModelVersion;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainType;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.ModelType;
import org.telosys.tools.generic.model.Optional;

public class Converter {
	
	private final static boolean log = true ;
	private final static TelosysToolsLogger logger = new ConsoleLogger();
	private void log(String msg) {
		if ( log ) {
			logger.log(this, msg);
		}
	}

	private static String EMPTY_STRING = ""; 
	private int linkIdCounter = 0 ;

	/**
	 * Converts the DSL model to the Generic model
	 * @param domainModel DSL model
	 * @return Generic model
	 */
	public Model convertToGenericModel(DomainModel domainModel) {
		GenericModel genericModel = new GenericModel();
		genericModel.setType( ModelType.DOMAIN_SPECIFIC_LANGUAGE );
		genericModel.setName( voidIfNull(domainModel.getName()) );
		genericModel.setVersion( GenericModelVersion.VERSION );
		genericModel.setDescription( voidIfNull(domainModel.getDescription() ) );

		// convert all entities
		convertEntities(domainModel, genericModel);
		
		return genericModel;
	}

	private void check(boolean expr, String errorMessage ) {
		if ( ! expr ) {
			throw new IllegalStateException(errorMessage);
		}
	}
	
	/**
	 * Define all entities and attributes
	 * @param domainModel DSL model
	 * @param genericModel Generic model
	 */
	private void convertEntities(DomainModel domainModel, GenericModel genericModel) {
		log("convertEntities()...");
		if(domainModel.getEntities() == null) {
			return;
		}

		// Define all the existing entities
		for(DomainEntity domainEntity : domainModel.getEntities()) {
//			GenericEntity genericEntity = new GenericEntity();
////			genericEntity.setClassName(notNullOrVoidValue(domainEntity.getName(), EMPTY_STRING));
////			genericEntity.setFullName(notNullOrVoidValue(domainEntity.getName(), EMPTY_STRING));			
//			genericEntity.setClassName(voidIfNull(domainEntity.getName()));
//			genericEntity.setFullName(voidIfNull(domainEntity.getName()));
			
			GenericEntity genericEntity = convertEntity( domainEntity );
			genericModel.getEntities().add(genericEntity);
		}
		
		// Links resolution
		for(DomainEntity domainEntity : domainModel.getEntities()) {
			GenericEntity genericEntity = (GenericEntity) genericModel.getEntityByClassName(domainEntity.getName());
			convertAttributes(domainEntity, genericEntity, genericModel);
		}
	}
	
	private GenericEntity convertEntity( DomainEntity domainEntity ) {
		log("convertEntity("+ domainEntity.getName() +")...");
		GenericEntity genericEntity = new GenericEntity();
		genericEntity.setClassName(notNull(domainEntity.getName()));
		genericEntity.setFullName(notNull(domainEntity.getName()));
		
		//--- NB : Database Table must be set in order to be able do "getEntityByTableName()"
		genericEntity.setDatabaseTable(notNull(domainEntity.getName())); // Same as "className" (unique)
		genericEntity.setDatabaseType("TABLE"); // Type is "TABLE" by default
		genericEntity.setDatabaseCatalog("");
		genericEntity.setDatabaseSchema("");
		genericEntity.setDatabaseForeignKeys(new LinkedList<ForeignKey>()); // Void list (No Foreign keys)
		
		return genericEntity ;
	}
	
	/**
	 * Define all attributes - this method needs all entities defined in the generic model for links resolution
	 * @param domainEntity DSL entity
	 * @param genericEntity Generic entity
	 * @param genericModel Generic model
	 */
	private void convertAttributes(DomainEntity domainEntity, GenericEntity genericEntity, GenericModel genericModel) {
		log("convertEntityAttributes()...");
		if(domainEntity.getFields() == null) {
			return;
		}
		for ( DomainEntityField domainEntityField : domainEntity.getFields() ) {

            DomainType domainFieldType = domainEntityField.getType();
            if (domainFieldType.isNeutralType() ) {
        		log("convertEntityAttributes() : " + domainEntityField.getName() + " : neutral type");
            	// Simple type attribute
            	GenericAttribute genericAttribute = convertAttributeNeutralType( domainEntityField );
                // Add the new attribute to the entity 
                if ( genericAttribute != null ) {
                    genericEntity.getAttributes().add(genericAttribute);
                }
            }
            else if (domainFieldType.isEntity() ) {
        		log("convertEntityAttributes() : " + domainEntityField.getName() + " : entity type (link)");
            	// Link type attribute (reference to 1 or N other entity )
        		linkIdCounter++;
            	GenericLink genericLink = convertAttributeLink( domainEntityField, genericModel );
                if ( genericLink != null ) {
                	genericEntity.getLinks().add(genericLink);
                }
            }
/*****
            // Is link ?
            if (!domainFieldType.isEntity() ) {

                GenericAttribute genericAttribute = new GenericAttribute();
                genericEntity.getAttributes().add(genericAttribute);
//                genericAttribute.setName(notNullOrVoidValue(domainEntityField.getName(), EMPTY_STRING));
                genericAttribute.setName( voidIfNull(domainEntityField.getName()) );

                // Java type
                if (domainFieldType.isNeutralType()) {
                    DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;
                    genericAttribute.setSimpleType(convertNeutralTypeToSimpleType(domainNeutralType, EMPTY_STRING));
                    genericAttribute.setFullType(convertNeutralTypeToFullType(domainNeutralType, EMPTY_STRING));
                }

//                // Enumeration
//                if (domainFieldType.isEnumeration()) {
//                    DomainEnumeration<?> domainEnumeration = (DomainEnumeration<?>) domainFieldType;
//                    // NOT YET AVAILABLE : Add Enumerations
//                }
//
                // Annotation
                if(domainEntityField.getAnnotations() != null) {
                    for(DomainEntityFieldAnnotation annotation : domainEntityField.getAnnotations().values()) {
                        if("@Id".equals(annotation.getName())) {
                            genericAttribute.setKeyElement(true);
                        }
                        if("@NotNull".equals(annotation.getName())) {
                            genericAttribute.setNotNull(true);
                        }
                        if("@Min".equals(annotation.getName())) {
                            Integer parameterValue = this.convertStringToInteger(annotation.getParameter(), null);
                            if(parameterValue != null) {
                                genericAttribute.setMinValue(parameterValue);
                            }
                        }
                        if("@Max".equals(annotation.getName())) {
                            Integer parameterValue = this.convertStringToInteger(annotation.getParameter(), null);
                            if(parameterValue != null) {
                                genericAttribute.setMaxValue(parameterValue);
                            }
                        }
                        if("@SizeMin".equals(annotation.getName())) {
                            Integer parameterValue = this.convertStringToInteger(annotation.getParameter(), null);
                            if(parameterValue != null) {
                                genericAttribute.setMinLength(parameterValue);
                            }
                        }
                        if("@SizeMax".equals(annotation.getName())) {
                            Integer parameterValue = this.convertStringToInteger(annotation.getParameter(), null);
                            if(parameterValue != null) {
                                genericAttribute.setMaxLength(parameterValue);
                            }
                        }
                        if("@Past".equals(annotation.getName())) {
                            genericAttribute.setDatePast(true);
                        }
                        if("@Future".equals(annotation.getName())) {
                            genericAttribute.setDateFuture(true);
                        }
                    }
                }

            } else {
                // Link

				DomainEntity domainEntityTarget = (DomainEntity) domainFieldType;
				GenericEntity genericEntityTarget =
						(GenericEntity) genericModel.getEntityByClassName(domainEntityTarget.getName());
				if(genericEntityTarget != null) {
					GenericLink genericLink = new GenericLink();
					genericEntity.getLinks().add(genericLink);
					genericLink.setTargetEntityClassName(domainEntityTarget.getName());
					Cardinality cardinality;
					if(domainEntityField.getCardinality() == 1) {
						cardinality = Cardinality.MANY_TO_ONE;
					} else {
						cardinality = Cardinality.ONE_TO_MANY;
					}
					genericLink.setCardinality(cardinality);
					genericLink.setFieldName(domainEntityField.getName());
					if(domainEntityField.getCardinality() == 1) {
						genericLink.setFieldType(domainEntityField.getType().getName());
					} else {
						genericLink.setFieldType("java.util.List");
					}
					genericLink.setTargetEntityClassName(domainEntityField.getType().getName());
					genericLink.setBasedOnForeignKey(false);
					genericLink.setBasedOnJoinTable(false);
					genericLink.setCascadeOptions(null);
					genericLink.setComparableString("");
					genericLink.setFetchType(FetchType.DEFAULT);
					genericLink.setForeignKeyName("");
					genericLink.setInverseSide(false);
					genericLink.setInverseSideLinkId(null);
					genericLink.setJoinColumns(null);
					genericLink.setJoinTable(null);
					genericLink.setJoinTableName(null);
					genericLink.setMappedBy(null);
					genericLink.setOptional(Optional.UNDEFINED);
					genericLink.setOwningSide(false);
					genericLink.setSelected(false);
					genericLink.setSourceTableName(null);
					genericLink.setTargetTableName(null);

                    // Annotation
                    if(domainEntityField.getAnnotations() != null) {
                        for(DomainEntityFieldAnnotation annotation : domainEntityField.getAnnotations().values()) {
                            if("@Embedded".equals(annotation.getName())) {
                                genericLink.setIsEmbedded(true);
                            }
                        }
                    }
				}
			}
*****/
		}
	}
	
	/**
	 * Converts a "neutral type" attribute <br>
	 * eg : id : integer {@Id}; <br>
	 * @param domainEntityField
	 * @return
	 */
	private GenericAttribute convertAttributeNeutralType( DomainEntityField domainEntityField ) {
		
		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isNeutralType(), "Invalid field type. Neutral type expected");
        DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;
		
        GenericAttribute genericAttribute = new GenericAttribute();
        genericAttribute.setName( voidIfNull(domainEntityField.getName()) );
        genericAttribute.setSimpleType(convertNeutralTypeToSimpleType(domainNeutralType, EMPTY_STRING));
        genericAttribute.setFullType(convertNeutralTypeToFullType(domainNeutralType, EMPTY_STRING));

        // Populate field from annotations if any
        if(domainEntityField.getAnnotations() != null) {
            for(DomainEntityFieldAnnotation annotation : domainEntityField.getAnnotations().values()) {
                if("@Id".equals(annotation.getName())) {
                    genericAttribute.setKeyElement(true);
                }
                if("@NotNull".equals(annotation.getName())) {
                    genericAttribute.setNotNull(true);
                }
                if("@Min".equals(annotation.getName())) {
                    Integer parameterValue = this.convertStringToInteger(annotation.getParameter(), null);
                    if(parameterValue != null) {
                        genericAttribute.setMinValue(parameterValue);
                    }
                }
                if("@Max".equals(annotation.getName())) {
                    Integer parameterValue = this.convertStringToInteger(annotation.getParameter(), null);
                    if(parameterValue != null) {
                        genericAttribute.setMaxValue(parameterValue);
                    }
                }
                if("@SizeMin".equals(annotation.getName())) {
                    Integer parameterValue = this.convertStringToInteger(annotation.getParameter(), null);
                    if(parameterValue != null) {
                        genericAttribute.setMinLength(parameterValue);
                    }
                }
                if("@SizeMax".equals(annotation.getName())) {
                    Integer parameterValue = this.convertStringToInteger(annotation.getParameter(), null);
                    if(parameterValue != null) {
                        genericAttribute.setMaxLength(parameterValue);
                    }
                }
                if("@Past".equals(annotation.getName())) {
                    genericAttribute.setDatePast(true);
                }
                if("@Future".equals(annotation.getName())) {
                    genericAttribute.setDateFuture(true);
                }
            }
        }
        return genericAttribute;
	}
	
	/**
	 * Converts a "LINK" attribute <br>
	 * eg : car : Car ; <br>
	 * @param domainEntityField
	 * @param genericModel
	 * @return
	 */
	private GenericLink convertAttributeLink( DomainEntityField domainEntityField, GenericModel genericModel ) {
		
		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isEntity(), "Invalid field type. Entity type expected");
//        DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;
//		
//        GenericAttribute genericAttribute = new GenericAttribute();
		
		DomainEntity domainEntityTarget = (DomainEntity) domainFieldType;
		
		// Check target existence
		GenericEntity genericEntityTarget =
				(GenericEntity) genericModel.getEntityByClassName(domainEntityTarget.getName());
		check( ( genericEntityTarget != null ), "No target entity for field '" + domainEntityField.getName() + "'. Cannot create Link");

		GenericLink genericLink = new GenericLink();

		genericLink.setId("Link"+linkIdCounter); // Link ID : generated (just to ensure not null )
		genericLink.setSelected(true); // nothing for link selection => selected by default
		
		//genericEntity.getLinks().add(genericLink);
		genericLink.setTargetEntityClassName(domainEntityTarget.getName());
		
		//--- Cardinality
		Cardinality cardinality;
		if(domainEntityField.getCardinality() == 1) {
			cardinality = Cardinality.MANY_TO_ONE;
		} else {
			cardinality = Cardinality.ONE_TO_MANY;
		}
		genericLink.setCardinality(cardinality);
		
		//--- Field info based on cardinality
		genericLink.setFieldName(domainEntityField.getName());
		if(domainEntityField.getCardinality() == 1) {
			// Reference to only ONE entity => MANY TO ONE
			genericLink.setFieldType(domainEntityField.getType().getName()); // use the Entity name
			genericLink.setOwningSide(true);
			genericLink.setInverseSide(false);
			genericLink.setInverseSideLinkId(null);
		} else {
			// Reference to only MANY entities => ONE TO MANY
			genericLink.setFieldType("java.util.List"); // use a COLLECTION type
			genericLink.setOwningSide(false);
			genericLink.setInverseSide(true);
			genericLink.setInverseSideLinkId(null);
		}
		
		genericLink.setTargetEntityClassName(domainEntityField.getType().getName());
		genericLink.setBasedOnForeignKey(false);
		genericLink.setBasedOnJoinTable(false);
		genericLink.setCascadeOptions(null);
		genericLink.setComparableString("");
		genericLink.setFetchType(FetchType.DEFAULT);
		genericLink.setForeignKeyName("");
		genericLink.setJoinColumns(null);
		genericLink.setJoinTable(null);
		genericLink.setJoinTableName(null);
		genericLink.setMappedBy(null);
		genericLink.setOptional(Optional.UNDEFINED);
		genericLink.setSourceTableName(null);
		genericLink.setTargetTableName(null);

        // Annotation
        if(domainEntityField.getAnnotations() != null) {
            for(DomainEntityFieldAnnotation annotation : domainEntityField.getAnnotations().values()) {
                if("@Embedded".equals(annotation.getName())) {
                    genericLink.setIsEmbedded(true);
                }
            }
        }

        return genericLink;
	}
	
	/**
	 * Conversion des types
	 */
	private static final Map<String, String> mapFullTypeConversion = new HashMap<String, String>();
	private static final Map<String, String> mapSimpleTypeConversion = new HashMap<String, String>();
	static {
		mapFullTypeConversion.put(DomainNeutralTypes.BOOLEAN,   Boolean.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.DATE,      Date.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.DECIMAL,   BigDecimal.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.INTEGER,   Integer.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.STRING,    String.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.TIME,      Date.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.TIMESTAMP, Date.class.getCanonicalName());
		
		mapSimpleTypeConversion.put(DomainNeutralTypes.BOOLEAN,   Boolean.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.DATE,      Date.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.DECIMAL,   BigDecimal.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.INTEGER,   Integer.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.STRING,    String.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.TIME,      Date.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.TIMESTAMP, Date.class.getSimpleName());
	}

	/**
	 * Convert DSL types (string, integer, etc.) to Generic model types.
	 * @param domainNeutralType DSL type
	 * @param defaultValue Default generic model type
	 * @return Generic model type
	 */
	private String convertNeutralTypeToSimpleType(DomainNeutralType domainNeutralType, String defaultValue) {
		if(domainNeutralType == null) {
			//return defaultValue;
			throw new IllegalStateException("Neutral type is null");
		}
		String genericModelType = mapSimpleTypeConversion.get(domainNeutralType.getName());
		return genericModelType;
	}

	/**
	 * Convert DSL types (string, integer, etc.) to Generic model types.
	 * @param domainNeutralType DSL type
	 * @param defaultValue Default generic model type
	 * @return Generic model type
	 */
	private String convertNeutralTypeToFullType(DomainNeutralType domainNeutralType, String defaultValue) {
		if(domainNeutralType == null) {
			//return defaultValue;
			throw new IllegalStateException("Neutral type is null");
		}
		String genericModelType = mapFullTypeConversion.get(domainNeutralType.getName());
		return genericModelType;
	}

//	/**
//	 * Convert String value
//	 * @param value String value
//	 * @param defaultValue Default value
//	 * @return Value for Generic Model
//	 */
//	private String notNullOrVoidValue(String value, String defaultValue) {
//		if(isDefined(value)) {
//			return value;
//		}
//		return defaultValue;
//	}

	private String notNull(String value) {
		if ( value == null ) {
			throw new IllegalStateException("Unexpected null value");
		}
		return value;
	}

	/**
	 * Returns a void string if the given value is null
	 * @param value
	 * @return
	 */
	private String voidIfNull(String value) {
		if ( value == null ) {
			return "";
		}
		return value;
	}

	/**
	 * Convert String value to Integer
	 * @param value String value
	 * @param defaultValue Default Integer value
	 * @return Integer value
	 */
	private Integer convertStringToInteger(String value, Integer defaultValue) {
		if(!isDefined(value)) {
			return defaultValue;
		}
		Integer integerValue;
		try {
			integerValue = Integer.valueOf(value);
		} catch(NumberFormatException e) {
			integerValue = defaultValue;
		}
		return integerValue;
	}
	
	/**
	 * Check if the String value is defined
	 * @param value String value
	 * @return is defined
	 */
	private boolean isDefined(String value) {
		if(value == null) {
			return false;
		}
		if(value.trim().equals(EMPTY_STRING)) {
			return false;
		}
		return true;
	}
	
}
