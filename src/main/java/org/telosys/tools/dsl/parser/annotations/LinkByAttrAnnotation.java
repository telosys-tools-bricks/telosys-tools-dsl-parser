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
 *   @LinkByAttr("empId")   // to reference a basic PK (with a single attribute)
 *   @LinkByAttr("x", "y")  // to reference a composite PK (keep PK attributes order)
 * @author Laurent Guerin
 *
 */
public class LinkByAttrAnnotation extends AnnotationDefinition {

//	private final String messagePrefix ;
	
	public LinkByAttrAnnotation() {
		super(AnnotationName.LINK_BY_ATTR, AnnotationParamType.LIST, AnnotationScope.LINK);
//		this.messagePrefix = "@"+AnnotationName.LINK_BY_ATTR;
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
//		List<JoinAttribute> joinAttributes = buildJoinAttributes(entity, link, originAttributes, referencedAttributes);
		List<LinkAttribute> joinAttributes = buildJoinAttributes(entity, link, originAttributes, referencedAttributes);
		
////		List<JoinColumn> joinColumns = getJoinColumns(model, entity, link, (String)paramValue);
//		List<JoinAttribute> joinAttributes = getJoinAttributes(model, entity, link, (String)paramValue);
////		link.setJoinColumns(joinColumns);
//		link.setJoinAttributes(joinAttributes);
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
	
//	private List<JoinAttribute> buildJoinAttributes(DslModelEntity entity, DslModelLink link, List<Attribute> originAttributes, 
	private List<LinkAttribute> buildJoinAttributes(DslModelEntity entity, DslModelLink link, List<Attribute> originAttributes, 
			List<Attribute> referencedAttributes) throws ParamError {
		// check number of attributes = number of PK attributes 
		if ( originAttributes.size() != referencedAttributes.size() ) {
			throw newParamError(entity.getClassName(), link.getFieldName(), 
					referencedAttributes.size() + " attribute(s) expected to match the referenced entity id");
		}
//		List<JoinAttribute> joinAttributes = new LinkedList<>();
		List<LinkAttribute> joinAttributes = new LinkedList<>();
		for ( int i = 0 ; i < originAttributes.size() ; i++ ) {
			Attribute originAttribute = originAttributes.get(i);
			Attribute referencedAttribute = referencedAttributes.get(i);
			// Check attribute types compatibility (to be sure to conform to PK attributes types)
			checkAttributesType(entity, link, originAttribute, referencedAttribute);
			// If OK : create a JoinAttribute
//			JoinAttribute joinAttribute = new DslModelLinkAttribute(originAttribute.getName(), referencedAttribute.getName());
			LinkAttribute linkAttribute = new DslModelLinkAttribute(originAttribute.getName(), referencedAttribute.getName());
			joinAttributes.add(linkAttribute);
		}
		return joinAttributes;
	}
	
	private void checkAttributesType(DslModelEntity entity, DslModelLink link, Attribute originAttribute, 
			Attribute referencedAttribute) throws ParamError {
		String referencedType = referencedAttribute.getNeutralType();
		String originType = originAttribute.getNeutralType();
		if ( referencedType.equals(originType) ) {
			return ;
		}
		else {
			// tolerance: acceptable if both are integers
			if ( isIntegerType(referencedType) && isIntegerType(originType) ) {
				return ;
			}
			else {
				throw newParamError(entity.getClassName(), link.getFieldName(), 
						"attribute type '" + originType +"' incompatible with referenced type");
			}
		}
	}
	
	private boolean isIntegerType(String type) {
		if ( 	DomainNeutralTypes.LONG.equals(type) ||
				DomainNeutralTypes.INTEGER.equals(type) ||
				DomainNeutralTypes.SHORT.equals(type) ) {
			return true;
		}
		else {
			return false;
		}
	}

	//============================================================================================================
	//============================================================================================================
	
	
//	protected List<JoinAttribute> getJoinAttributes(DslModel model, DslModelEntity entity, DslModelLink link, String paramValue) throws ParamError {
//		ReferenceDefinitions attributesRefDef = getReferenceDefinitions(entity, link, paramValue);
//		
//		DslModelEntity referencedEntity = getReferencedEntity(model, entity, link);
//		
////		ReferenceDefinitions columnsRefDef;
//		try {
////			columnsRefDef = convertAttribRefToColRef(entity, referencedEntity, attributesRefDef);
////			JoinColumnsBuilder jcb = getJoinColumnsBuilder();
//			JoinAttributesBuilder jab = getJoinAttributesBuilder();
////			List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromRefDef(columnsRefDef); 
//			return jab.buildJoinAttributesFromRefDef(attributesRefDef); 
////			link.setJoinColumns(joinColumns);
////			return jcb.buildJoinColumnsFromRefDef(columnsRefDef);		
////			return jcb.buildJoinAttributesFromRefDef(columnsRefDef);		
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			throw newParamError(entity, link, e.getMessage());
//		}
//	}
//
//	private ReferenceDefinitions getReferenceDefinitions(DslModelEntity entity, DslModelLink link, String paramValue) throws ParamError {
//		ReferenceDefinitions referenceDefinitions = buildReferenceDefinitions(paramValue);
//		checkNotVoid(entity, link, referenceDefinitions);
//		checkReferencedNames(entity, link, referenceDefinitions);
//		return referenceDefinitions;
//	}

	
//	private DslModelEntity getReferencedEntity(DslModel model, DslModelEntity entity, DslModelLink link) throws ParamError {
//		String referencedEntityName = link.getTargetEntityClassName();
//		if ( referencedEntityName == null ) {
//			throw newParamError(entity, link, "invalid link : target entity name is null");
//		}
//		DslModelEntity referencedEntity = (DslModelEntity) model.getEntityByClassName(referencedEntityName);
//		if ( referencedEntity == null ) {
//			throw newParamError(entity, link, "unknown referenced entity '"+ referencedEntityName + "'");
//		}
//		return referencedEntity;
//	}
	
/***
	protected ReferenceDefinitions getReferenceDefinitions(String paramValue) {
		ReferenceDefinitions rd = buildReferenceDefinitions(paramValue);
		checkReferenceDefinitions(rd);
		return rd;
	}

	protected ReferenceDefinitions buildReferenceDefinitions(String s) {
		ReferenceDefinitions refDefinitions = new ReferenceDefinitions();
		if ( ! StrUtil.nullOrVoid(s) ) {
			String[] parts = StrUtil.split(s, ',');
			for ( String part : parts ) {
				if ( ! StrUtil.nullOrVoid(part) ) {
					if ( part.contains(">") ) {
						// "name > referencedName "
						String[] pair = StrUtil.split(part, '>');
						String name = "" ;
						String referencedName = "";
						if ( pair.length > 0 ) {
							name = pair[0].trim();
						}
						if ( pair.length > 1 ) {
							referencedName = pair[1].trim();
						}
						if ( ! name.isEmpty() ) {
							refDefinitions.add( new ReferenceDefinition(name, referencedName));
						}
					}
					else {
						// "name" only (without referencedName)
						String name = part.trim();
						refDefinitions.add( new ReferenceDefinition(name));
					}
				}
			}
		}
		return refDefinitions;
	}
	
	private void checkReferenceDefinitions(ReferenceDefinitions referenceDefinitions ) {
		if ( referenceDefinitions.count() == 0 ) {
			throwException("no reference definition");
		}
		else {
			if ( referenceDefinitions.count() > 1 ) {
				int referencedCount = 0;
				for ( ReferenceDefinition rd : referenceDefinitions.getList() ) {
					if ( rd.hasReferencedName() ) {
						referencedCount++;
					}
				}
				if ( referencedCount < referenceDefinitions.count() ) {
					throwException("missing referenced name(s)");
				}
			}
		}
	}
**/

//	/**
//	 * Converts attributes references definitions ( "attr", "attr > referencedAttr" ) <br>
//	 * to columns references definitions ( "col", "col > referencedCol" ) <br>
//	 * @param entity
//	 * @param referencedEntity
//	 * @param referenceDefinitions
//	 * @return
//	 */
//	private ReferenceDefinitions convertAttribRefToColRef(DslModelEntity entity, 
//			DslModelEntity referencedEntity, ReferenceDefinitions referenceDefinitions) throws Exception {
//		ReferenceDefinitions referenceDefinitionsForColumns = new ReferenceDefinitions();
//		for ( ReferenceDefinition rd : referenceDefinitions.getList()) {
//			String columnName = getAttribColumnName(entity, rd.getName());
//			if ( rd.getReferencedName().isEmpty() ) {
//				referenceDefinitionsForColumns.add(new ReferenceDefinition(columnName));
//			}
//			else {
//				String referencedColumnName = getAttribColumnName(referencedEntity, rd.getReferencedName());
//				referenceDefinitionsForColumns.add(new ReferenceDefinition(columnName,referencedColumnName));
//			}
//		}
//		return referenceDefinitionsForColumns;
//	}

//	/**
//	 * Returns the database column name associated with the given attribute name
//	 * @param entity
//	 * @param attribName
//	 * @return
//	 */
//	private String getAttribColumnName(DslModelEntity entity, String attribName) throws Exception {
//		if ( ! StrUtil.nullOrVoid(attribName) ) {
//			Attribute attrib = entity.getAttributeByName(attribName);
//			if ( attrib != null ) {
//				String columnName = attrib.getDatabaseName();
//				if ( ! StrUtil.nullOrVoid(columnName) ) {
//					return columnName;
//				}
//				else {
//					throw new Exception("no database column for attribute '" + attribName 
//							+ "' in '" + entity.getClassName() + "'");
//				}
//			}
//			else {
//				throw new Exception("unknown attribute '" + attribName 
//							+ "' in '" + entity.getClassName() + "'");
//			}
//		}
//		return "";
//	}
	
//	private List<JoinAttribute> buildJoinAttributesFromRefDef(ReferenceDefinitions referenceDefinitions) {
//		List<ReferenceDefinition> refDefList = referenceDefinitions.getList();
//	    if ( refDefList.isEmpty() ) {
//	    	throw new IllegalStateException(messagePrefix + " : no join attribute (empty list)");
//	    }
//		List<JoinAttribute> joinAttributes = new LinkedList<>();
//		int position = 0;
//		for ( ReferenceDefinition rd : refDefList) {
//			DslModelJoinAttribute jc = buildJoinAttribute(position, rd.getName(), rd.getReferencedName());
//			joinAttributes.add(jc);
//			position++;
//		}
//		return joinAttributes;
//	}

	
	
//	private DslModelJoinAttribute buildJoinAttribute(int position, String attributeName, String referencedAttributeName) {
//	    if ( StrUtil.nullOrVoid(attributeName) ) {
//	    	throw new IllegalArgumentException(messagePrefix + " : no origin name ");
//	    }
//	    if ( referencedAttributeName.isEmpty() ) {
//	    	throw new IllegalArgumentException(messagePrefix + " : no referenced name ");
//	    	// TODO : try to infer referenced attribute name
//	    }
//	    else {
//		    return new DslModelJoinAttribute(attributeName, referencedAttributeName);
//	    }
//	}

}
