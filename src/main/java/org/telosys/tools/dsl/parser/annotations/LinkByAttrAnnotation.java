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

import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.commons.JoinColumnsBuilder;
import org.telosys.tools.dsl.commons.ReferenceDefinition;
import org.telosys.tools.dsl.commons.ReferenceDefinitions;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.JoinColumn;

public class LinkByAttrAnnotation extends LinkByAnnotation {

	public LinkByAttrAnnotation() {
		super(AnnotationName.LINK_BY_ATTR, AnnotationParamType.STRING);
	}
	
	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) throws ParsingError {
		// Example : @LinkByAttr(attr1)   @LinkByAttr(attr1 > ref1 , attr2 > ref2 )
		checkParamValue(entity, link, paramValue);
		List<JoinColumn> joinColumns = getJoinColumns(model, entity, link, (String)paramValue);
		link.setJoinColumns(joinColumns);
	}
	
	private ReferenceDefinitions getReferenceDefinitions(DslModelEntity entity, DslModelLink link, String paramValue) throws ParsingError {
		ReferenceDefinitions referenceDefinitions = buildReferenceDefinitions(paramValue);
		checkNotVoid(entity, link, referenceDefinitions);
		checkReferencedNames(entity, link, referenceDefinitions);
		return referenceDefinitions;
	}

	protected List<JoinColumn> getJoinColumns(DslModel model, DslModelEntity entity, DslModelLink link, String paramValue) throws ParsingError {
		ReferenceDefinitions attributesRefDef = getReferenceDefinitions(entity, link, paramValue);
		
//		String referencedEntityName = link.getTargetEntityClassName();
//		DslModelEntity referencedEntity = (DslModelEntity) model.getEntityByClassName(referencedEntityName);
//		if ( referencedEntity == null ) {
//			throw new IllegalStateException("@"+ AnnotationName.LINK_BY_ATTR 
//					+ " : cannot found referenced entity '"+ referencedEntityName + "'");
//		}
		DslModelEntity referencedEntity = getReferencedEntity(model, entity, link);
		
		ReferenceDefinitions columnsRefDef;
		try {
			columnsRefDef = convertAttribRefToColRef(entity, referencedEntity, attributesRefDef);
//		JoinColumnsBuilder jcb = new JoinColumnsBuilder("@"+AnnotationName.LINK_BY_ATTR) ;
//		JoinColumnsBuilder jcb = new JoinColumnsBuilder("@"+this.getName()) ;
			JoinColumnsBuilder jcb = getJoinColumnsBuilder();
			List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromRefDef(columnsRefDef); 
			link.setJoinColumns(joinColumns);
			return jcb.buildJoinColumnsFromRefDef(columnsRefDef);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw newParamError(entity, link, e.getMessage());
		}
	}
	
	private DslModelEntity getReferencedEntity(DslModel model, DslModelEntity entity, DslModelLink link) throws ParsingError {
		String referencedEntityName = link.getTargetEntityClassName();
		if ( referencedEntityName == null ) {
			throw newParamError(entity, link, "invalid link : target entity name is null");
		}
		DslModelEntity referencedEntity = (DslModelEntity) model.getEntityByClassName(referencedEntityName);
		if ( referencedEntity == null ) {
			throw newParamError(entity, link, "unknown referenced entity '"+ referencedEntityName + "'");
		}
		return referencedEntity;
	}
	
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

	/**
	 * Converts attributes references definitions ( "attr", "attr > referencedAttr" ) <br>
	 * to columns references definitions ( "col", "col > referencedCol" ) <br>
	 * @param entity
	 * @param referencedEntity
	 * @param referenceDefinitions
	 * @return
	 */
	private ReferenceDefinitions convertAttribRefToColRef(DslModelEntity entity, 
			DslModelEntity referencedEntity, ReferenceDefinitions referenceDefinitions) throws Exception {
		ReferenceDefinitions referenceDefinitionsForColumns = new ReferenceDefinitions();
		for ( ReferenceDefinition rd : referenceDefinitions.getList()) {
			String columnName = getAttribColumnName(entity, rd.getName());
			if ( rd.getReferencedName().isEmpty() ) {
				referenceDefinitionsForColumns.add(new ReferenceDefinition(columnName));
			}
			else {
				String referencedColumnName = getAttribColumnName(referencedEntity, rd.getReferencedName());
				referenceDefinitionsForColumns.add(new ReferenceDefinition(columnName,referencedColumnName));
			}
		}
		return referenceDefinitionsForColumns;
	}

	/**
	 * Returns the database column name associated with the given attribute name
	 * @param entity
	 * @param attribName
	 * @return
	 */
	private String getAttribColumnName(DslModelEntity entity, String attribName) throws Exception {
		if ( ! StrUtil.nullOrVoid(attribName) ) {
			Attribute attrib = entity.getAttributeByName(attribName);
			if ( attrib != null ) {
				String columnName = attrib.getDatabaseName();
				if ( ! StrUtil.nullOrVoid(columnName) ) {
					return columnName;
				}
				else {
					throw new Exception("no database column for attribute '" + attribName 
							+ "' in '" + entity.getClassName() + "'");
				}
			}
			else {
				throw new Exception("unknown attribute '" + attribName 
							+ "' in '" + entity.getClassName() + "'");
			}
		}
		return "";
	}

}
