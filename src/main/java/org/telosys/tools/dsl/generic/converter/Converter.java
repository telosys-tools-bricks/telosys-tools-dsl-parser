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
import java.util.Map;

import org.telosys.tools.dsl.generic.model.*;
import org.telosys.tools.dsl.parser.model.*;
import org.telosys.tools.generic.model.*;

public class Converter {
	
	private static String EMPTY_STRING = ""; 

	/**
	 * Convert DSL model to Generic model
	 * @param domainModel DSL model
	 * @return Generic model
	 */
	public Model convertToGenericModel(DomainModel domainModel) {
		GenericModel genericModel = new GenericModel();
		genericModel.setType( ModelType.DOMAIN_SPECIFIC_LANGUAGE );
		genericModel.setName( convert(domainModel.getName(), EMPTY_STRING) );
		genericModel.setVersion( GenericModelVersion.VERSION );
		genericModel.setDescription( convert(domainModel.getDescription(), EMPTY_STRING) );

		// convert all entities
		convertEntities(domainModel, genericModel);
		
		return genericModel;
	}

	/**
	 * Define all entities and attributes
	 * @param domainModel DSL model
	 * @param genericModel Generic model
	 */
	private void convertEntities(DomainModel domainModel, GenericModel genericModel) {
		if(domainModel.getEntities() == null) {
			return;
		}
		// Define all entites
		for(DomainEntity domainEntity : domainModel.getEntities()) {
			GenericEntity genericEntity = new GenericEntity();
			genericModel.getEntities().add(genericEntity);
			genericEntity.setClassName(convert(domainEntity.getName(), EMPTY_STRING));
			genericEntity.setFullName(convert(domainEntity.getName(), EMPTY_STRING));
		}
		// Links resolution
		for(DomainEntity domainEntity : domainModel.getEntities()) {
			GenericEntity genericEntity = (GenericEntity) genericModel.getEntityByClassName(domainEntity.getName());
			convertAttributes(domainEntity, genericEntity, genericModel);
		}
	}

	/**
	 * Define all attributes - this method needs all entities defined in the generic model for links resolution
	 * @param domainEntity DSL entity
	 * @param genericEntity Generic entity
	 * @param genericModel Generic model
	 */
	private void convertAttributes(DomainEntity domainEntity, GenericEntity genericEntity, GenericModel genericModel) {
		if(domainEntity.getFields() == null) {
			return;
		}
		for(DomainEntityField domainEntityField : domainEntity.getFields()) {

            DomainType domainFieldType = domainEntityField.getType();

            // Is link ?
            if(!domainFieldType.isEntity()) {

                GenericAttribute genericAttribute = new GenericAttribute();
                genericEntity.getAttributes().add(genericAttribute);
                genericAttribute.setName(convert(domainEntityField.getName(), EMPTY_STRING));

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

		}
	}

	/**
	 * Conversion des types
	 */
	private static final Map<String, String> mapFullTypeConversion = new HashMap<String, String>();
	private static final Map<String, String> mapSimpleTypeConversion = new HashMap<String, String>();
	static {
		mapFullTypeConversion.put(DomainNeutralTypes.BOOLEAN, Boolean.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.DATE, Date.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.DECIMAL, BigDecimal.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.INTEGER, Integer.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.STRING, String.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.TIME, Date.class.getCanonicalName());
		mapFullTypeConversion.put(DomainNeutralTypes.TIMESTAMP, Date.class.getCanonicalName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.BOOLEAN, Boolean.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.DATE, Date.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.DECIMAL, BigDecimal.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.INTEGER, Integer.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.STRING, String.class.getSimpleName());
		mapSimpleTypeConversion.put(DomainNeutralTypes.TIME, Date.class.getSimpleName());
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
			return defaultValue;
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
			return defaultValue;
		}
		String genericModelType = mapFullTypeConversion.get(domainNeutralType.getName());
		return genericModelType;
	}

	/**
	 * Convert String value
	 * @param value String value
	 * @param defaultValue Default value
	 * @return Value for Generic Model
	 */
	private String convert(String value, String defaultValue) {
		if(isDefined(value)) {
			return value;
		}
		return defaultValue;
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
