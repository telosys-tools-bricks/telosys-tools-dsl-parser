package org.telosys.tools.dsl.generic.converter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.telosys.tools.dsl.generic.model.GenericAttribute;
import org.telosys.tools.dsl.generic.model.GenericEntity;
import org.telosys.tools.dsl.generic.model.GenericModel;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEnumeration;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainType;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

public class Converter {
	
	private static String EMPTY_STRING = ""; 

	/**
	 * Convert DSL model to Generic model
	 * @param domainModel DSL model
	 * @return Generic model
	 */
	public Model convertToGenericModel(DomainModel domainModel) {
		GenericModel genericModel = new GenericModel();
		genericModel.setName(convert(domainModel.getName(), EMPTY_STRING));
		
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
			genericEntity.setName(convert(domainEntity.getName(), EMPTY_STRING));
		}
		// Define all attributes with links resolution between entities
		for(DomainEntity domainEntity : domainModel.getEntities()) {
			GenericEntity genericEntity = (GenericEntity) genericModel.getEntityByName(domainEntity.getName());
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

			GenericAttribute genericAttribute = new GenericAttribute();
			genericEntity.getAttributes().add(genericAttribute);
			genericAttribute.setName(convert(domainEntityField.getName(), EMPTY_STRING));
		
			DomainType domainType = domainEntityField.getType();
			if(domainType instanceof DomainNeutralType) {
				DomainNeutralType domainNeutralType = (DomainNeutralType) domainType;
				genericAttribute.setType(convertNeutralType(domainNeutralType, EMPTY_STRING));
			}
			if(domainType instanceof DomainEnumeration) {
				DomainEnumeration<?> domainEnumeration = (DomainEnumeration<?>) domainType;
				// TODO Add Enumerations
			}
			if(domainType instanceof DomainEntity) {
				DomainEntity domainEntityTarget = (DomainEntity) domainType;
				GenericEntity genericEntityTarget = null; 
				for(Entity entity : genericModel.getEntities()) {
					if(domainEntityTarget.getName().equals(entity)) {
						genericEntityTarget = (GenericEntity) entity;
					}
				}
				// TODO Add Foreign Key
			}
		}
	}
	
	private static final Map<String, String> mapTypeConversion = new HashMap<String, String>();
	static {
		mapTypeConversion.put(DomainNeutralTypes.BOOLEAN, Boolean.class.getName());
		mapTypeConversion.put(DomainNeutralTypes.DATE, Date.class.getName());
		mapTypeConversion.put(DomainNeutralTypes.DECIMAL, BigDecimal.class.getName());
		mapTypeConversion.put(DomainNeutralTypes.INTEGER, Integer.class.getName());
		mapTypeConversion.put(DomainNeutralTypes.STRING, String.class.getName());
		mapTypeConversion.put(DomainNeutralTypes.TIME, Date.class.getName());
		mapTypeConversion.put(DomainNeutralTypes.TIMESTAMP, Date.class.getName());
	}

	/**
	 * Convert DSL types (string, integer, etc.) to Generic model types.
	 * @param domainNeutralType DSL type
	 * @param defaultValue Default generic model type
	 * @return Generic model type
	 */
	private String convertNeutralType(DomainNeutralType domainNeutralType, String defaultValue) {
		if(domainNeutralType == null) {
			return defaultValue;
		}
		String genericModelType = mapTypeConversion.get(domainNeutralType.getName());
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
