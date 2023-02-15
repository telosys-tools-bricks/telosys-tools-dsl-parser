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
package org.telosys.tools.dsl.parser.annotations;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.model.DslModelLinkAttribute;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.LinkAttribute;


/**
 * LinkByAttr annotation 
 * Examples :
 *   LinkByAttr("empId")   // to reference a basic PK (with a single attribute)
 *   LinkByAttr("x", "y")  // to reference a composite PK (keep PK attributes order)
 *   
 * @author Laurent Guerin
 */
public class LinkByAttrAnnotation extends AnnotationDefinition {

	/**
	 * Constructor
	 */
	public LinkByAttrAnnotation() {
		super(AnnotationName.LINK_BY_ATTR, AnnotationParamType.LIST, AnnotationScope.LINK);
	}
	
	@Override
	public void afterCreation(String entityName, String fieldName, DomainAnnotation annotation) throws ParamError {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) annotation.getParameterAsList();
		if ( list.isEmpty() ) {
			throw newParamError(entityName, fieldName, "at least 1 attribute required");
		}
	}
	
	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) throws ParamError {
		checkParamValue(entity, link, paramValue);
		List<String> attributeNames = (List<String>) paramValue;
		List<Attribute> originAttributes = getOriginAttributes(entity, link, attributeNames);
		DslModelEntity referencedEntity = getReferencedEntity(model, link);
		List<Attribute> referencedAttributes = referencedEntity.getKeyAttributes();
		List<LinkAttribute> joinAttributes = buildJoinAttributes(entity, link, originAttributes, referencedAttributes);
		
		link.setAttributes(joinAttributes);
		link.setBasedOnAttributes(true);
	}
	
	private List<Attribute> getOriginAttributes(DslModelEntity entity, DslModelLink link, List<String> attributes) throws ParamError {
		List<Attribute> originAttributes = new LinkedList<>();
		for ( String rawAttributeName : attributes ) {
			String attributeName = rawAttributeName.trim();
			if ( attributeName.isEmpty() ) {
				throw newParamError(entity.getClassName(), link.getFieldName(), "invalid attribute (name is void)");
			}
			Attribute attribute = entity.getAttributeByName(attributeName);
			if ( attribute == null ) {
				throw newParamError(entity.getClassName(), link.getFieldName(), "unknown attribute '" + attributeName + "'");
			}
			originAttributes.add(attribute);
		}
		return originAttributes;
	}
	
	private DslModelEntity getReferencedEntity (DslModel model, DslModelLink link) {
		String referencedEntityName = link.getReferencedEntityName();
		DslModelEntity e = (DslModelEntity) model.getEntityByClassName(referencedEntityName);
		if ( e == null ) {
			throw new IllegalStateException("Invalid Link : unknown entity '" + referencedEntityName + "'");
		}
		return e;
	}
	
	private List<LinkAttribute> buildJoinAttributes(DslModelEntity entity, DslModelLink link, List<Attribute> originAttributes, 
			List<Attribute> referencedAttributes) throws ParamError {
		// check number of attributes = number of PK attributes 
		if ( originAttributes.size() != referencedAttributes.size() ) {
			throw newParamError(entity.getClassName(), link.getFieldName(), 
					referencedAttributes.size() + " attribute(s) expected to match the referenced entity id");
		}
		List<LinkAttribute> joinAttributes = new LinkedList<>();
		for ( int i = 0 ; i < originAttributes.size() ; i++ ) {
			Attribute originAttribute = originAttributes.get(i);
			Attribute referencedAttribute = referencedAttributes.get(i);
			// Check attribute types compatibility (to be sure to conform to PK attributes types)
			checkAttributesType(entity, link, originAttribute, referencedAttribute);
			// If OK : create a JoinAttribute
			LinkAttribute linkAttribute = new DslModelLinkAttribute(originAttribute.getName(), referencedAttribute.getName());
			joinAttributes.add(linkAttribute);
		}
		return joinAttributes;
	}
	
	private void checkAttributesType(DslModelEntity entity, DslModelLink link, Attribute originAttribute, 
			Attribute referencedAttribute) throws ParamError {
		String referencedType = referencedAttribute.getNeutralType();
		String originType = originAttribute.getNeutralType();
		if ( ! compatibleTypes(referencedType, originType) ) {
			throw newParamError(entity.getClassName(), link.getFieldName(), 
					"attribute type '" + originType +"' incompatible with referenced type '" + referencedType + "'");
		}
	}
	private boolean compatibleTypes(String type1, String type2) {
		if ( type1.equals(type2) ) {
			return true ;
		}
		else {
			// tolerance: acceptable if both are integers
			if ( isIntegerType(type1) && isIntegerType(type2) ) {
				return true;
			}
		}
		return false;
	}
	private boolean isIntegerType(String type) {
		return DomainNeutralTypes.LONG.equals(type) ||
				DomainNeutralTypes.INTEGER.equals(type) ||
				DomainNeutralTypes.SHORT.equals(type) ;
	}

}
