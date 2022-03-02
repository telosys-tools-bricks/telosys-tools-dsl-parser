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

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelForeignKey;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.model.DslModelLinkAttribute;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyAttribute;
import org.telosys.tools.generic.model.LinkAttribute;

public class LinkByFKAnnotation extends AnnotationDefinition { // extends LinkByAnnotation {

	public LinkByFKAnnotation() {
		//super(AnnotationName.LINK_BY_FK, AnnotationParamType.STRING);
		super(AnnotationName.LINK_BY_FK, AnnotationParamType.STRING, AnnotationScope.LINK);
}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) throws ParamError {
		checkParamValue(entity, link, paramValue);
		String fkName = (String)paramValue;
//		ForeignKey fk = getForeignKeyByName(entity, fkName);
//		
//		JoinColumnsBuilder jcb = getJoinColumnsBuilder();
//		List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromForeignKey(fk);
		
		// Try to get and check FK
		DslModelForeignKey fk = getForeignKeyByName(entity, link, fkName);
		checkIfLinkIsCompatibleWithForeignKey(entity, link, fk);

		
//////		List<JoinColumn> joinColumns = getJoinColumns(entity, link, fkName);
////		List<JoinColumn> joinColumns = buildJoinColumns(fk);
//////		
//////		// Apply to link
//////		link.setJoinColumns(joinColumns);
//		// Join Attributes instead of Join Columns
//		List<JoinAttribute> joinAttributes = buildJoinAttributes(entity, link, fk);
//		link.setJoinAttributes(joinAttributes);
		
		// new in 3.4.0
//		link.setJoinAttributes(fk.getJoinAttributes());
		link.setAttributes(fk.getLinkAttributes());
		
		// other flags 
		link.setBasedOnForeignKey(true);
		link.setForeignKeyName(fkName);
	}
	
	private DslModelForeignKey getForeignKeyByName(DslModelEntity entity, DslModelLink link, String fkName) throws ParamError {
		if ( ! StrUtil.nullOrVoid(fkName) ) {
//			ForeignKey fk = entity.getDatabaseForeignKeyByName(fkName);
			ForeignKey fk = entity.getForeignKeyByName(fkName);
			if ( fk != null ) {
				return (DslModelForeignKey) fk;
			}
			else {
				// FK not found => ERROR
//				throw new IllegalStateException("@LinkByFK : cannot found Foreign Key '" + fkName + "' in entity" );
				throw newParamError(entity.getClassName(), link.getFieldName(), 
						"cannot found Foreign Key '" + fkName + "' in entity");
			}
		}
		else {
			// no FK name => ERROR
//			throw new IllegalStateException("@LinkByFK : no Foreign Key name");
			throw newParamError(entity.getClassName(), link.getFieldName(), "no Foreign Key name");
		}
	}

	private void checkIfLinkIsCompatibleWithForeignKey(DslModelEntity entity, DslModelLink link, DslModelForeignKey fk) throws ParamError  {
		String linkReferencedEntityName = link.getReferencedEntityName();
		String fkReferencedEntityName = fk.getReferencedEntityName();
		if ( linkReferencedEntityName != null ) {
			if ( ! linkReferencedEntityName.equals(fkReferencedEntityName) ) {
				//throw new IllegalStateException("@LinkByFK : invalid Foreign Key : ref table '" + fkTable + "' != '" + linkTable + "'");
				throw newParamError(entity.getClassName(), link.getFieldName(), 
						"invalid Foreign Key : reference '" + fkReferencedEntityName + "' != '" + linkReferencedEntityName + "'");
			}
		}
		else {
			//throw new IllegalStateException("@LinkByFK : cannot check reference because link table is null");
			throw newParamError(entity.getClassName(), link.getFieldName(), 
					"cannot check reference because link target is null");
		}
	}

	//	private List<JoinColumn> getJoinColumns(DslModelEntity entity, DslModelLink link, String fkName) throws ParamError {
	//	try {
	//		DslModelForeignKey fk = getForeignKeyByName(entity, fkName);
	//		checkIfForeignKeyIsCompatible(link, fk);
	//		JoinColumnsBuilder jcb = getJoinColumnsBuilder();
	//		return jcb.buildJoinColumnsFromForeignKey(fk);
	//	} catch (Exception e) {
	//		throw newParamError(entity, link, e.getMessage());
	//	}
	//}
	
	//private List<JoinColumn> buildJoinColumns(DslModelForeignKey fk) { //throws ParamError {
//	private List<JoinAttribute> buildJoinAttributes(DslModelEntity entity, DslModelLink link, DslModelForeignKey fk) throws ParamError {
	private List<LinkAttribute> buildJoinAttributes(DslModelEntity entity, DslModelLink link, DslModelForeignKey fk) throws ParamError {
//	//	JoinColumnsBuilder jcb = getJoinColumnsBuilder();
//		JoinAttributesBuilder jcb = getJoinAttributesBuilder();
//	//	return jcb.buildJoinColumnsFromForeignKey(fk);
//		return jcb.buildJoinAttributesFromForeignKey(fk);
		
//		List<JoinAttribute> joinAttributes = new LinkedList<>();
		List<LinkAttribute> joinAttributes = new LinkedList<>();
		if ( fk != null ) {
//			for ( ForeignKeyColumn fkCol : fk.getColumns() ) {
//				DslModelJoinColumn jc = buildJoinColumn(fkCol.getColumnName(), 
//						fkCol.getReferencedColumnName());
//				joinColumns.add(jc);
//			}
			for ( ForeignKeyAttribute fkAttribute : fk.getAttributes() ) {
				DslModelLinkAttribute jc = buildJoinAttribute(entity, link,
						fkAttribute.getOriginAttributeName(), 
						fkAttribute.getReferencedAttributeName());
				joinAttributes.add(jc);
			}
		}
		return joinAttributes;
	}
	private DslModelLinkAttribute buildJoinAttribute(DslModelEntity entity, DslModelLink link, 
			String attributeName, String referencedAttributeName) throws ParamError {
	    if ( StrUtil.nullOrVoid(attributeName) ) {
	    	//throw new IllegalArgumentException(messagePrefix + " : no origin name ");
			throw newParamError(entity.getClassName(), link.getFieldName(), "no origin attribute" );
	    }
	    if ( StrUtil.nullOrVoid(referencedAttributeName) ) {
		    //return new DslModelJoinAttribute(attributeName);
	    	throw newParamError(entity.getClassName(), link.getFieldName(),  "no referenced attribute");
	    }
	    else {
		    return new DslModelLinkAttribute(attributeName, referencedAttributeName);
	    }
	}


//	private void checkIfForeignKeyIsCompatible(DslModelLink link, DslModelForeignKey fk) throws Exception {
//		String linkTable = link.getTargetTableName();
//		String fkTable = fk.getReferencedTableName();
//		if ( linkTable != null ) {
//			if ( ! linkTable.equals(fkTable) ) {
//				//throw new IllegalStateException("@LinkByFK : invalid Foreign Key : ref table '" + fkTable + "' != '" + linkTable + "'");
//				throw new Exception("invalid Foreign Key : ref table '" + fkTable + "' != '" + linkTable + "'");
//			}
//		}
//		else {
//			//throw new IllegalStateException("@LinkByFK : cannot check reference because link table is null");
//			throw new Exception("cannot check reference because link table is null");
//		}
//	}

}
