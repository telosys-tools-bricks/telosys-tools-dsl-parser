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
package org.telosys.tools.dsl.commons;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelJoinAttribute;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyAttribute;
import org.telosys.tools.generic.model.JoinAttribute;

public class JoinAttributesBuilder {

	private final String messagePrefix ;
	
	public JoinAttributesBuilder(String messagePrefix) {
		this.messagePrefix = messagePrefix;
	}
	
	private DslModelJoinAttribute buildJoinAttribute(String attributeName, String referencedAttributeName) {
	    if ( StrUtil.nullOrVoid(attributeName) ) {
	    	throw new IllegalArgumentException(messagePrefix + " : no origin name ");
	    }
	    if ( referencedAttributeName.isEmpty() ) {
		    //return new DslModelJoinAttribute(attributeName);
	    	throw new IllegalArgumentException(messagePrefix + " : no referenced name ");
	    }
	    else {
		    return new DslModelJoinAttribute(attributeName, referencedAttributeName);
	    }
	}

//	public List<JoinAttribute> buildJoinAttributesFromRefDef(ReferenceDefinitions referenceDefinitions) {
//		List<ReferenceDefinition> refDefList = referenceDefinitions.getList();
//	    if ( refDefList.isEmpty() ) {
//	    	throw new IllegalStateException(messagePrefix + " : no join attribute (empty list)");
//	    }
//		List<JoinAttribute> joinAttributes = new LinkedList<>();
//		int position = 0;
//		for ( ReferenceDefinition rd : refDefList) {
//			DslModelJoinAttribute jc = buildJoinAttribute(rd.getName(), rd.getReferencedName());
//			joinAttributes.add(jc);
//			position++;
//		}
//		return joinAttributes;
//	}

	
	/**
	 * Build join attributes list from the given Foreign Key
	 * @param fk
	 * @return
	 */
	public List<JoinAttribute> buildJoinAttributesFromForeignKey(ForeignKey fk) {
		List<JoinAttribute> joinAttributes = new LinkedList<>();
		if ( fk != null ) {
//			for ( ForeignKeyColumn fkCol : fk.getColumns() ) {
//				DslModelJoinColumn jc = buildJoinColumn(fkCol.getColumnName(), 
//						fkCol.getReferencedColumnName());
//				joinColumns.add(jc);
//			}
			for ( ForeignKeyAttribute fkAttribute : fk.getAttributes() ) {
				DslModelJoinAttribute jc = buildJoinAttribute(
						fkAttribute.getOriginAttributeName(), 
						fkAttribute.getReferencedAttributeName());
				joinAttributes.add(jc);
			}
		}
		return joinAttributes;
	}

	/**
	 * Try to infer join attributes from the given referenced entity <br>
	 * by searching in the entity a unique FK targeting this table 
	 * @param entity
	 * @param referencedTableName
	 * @return
	 */
	public List<JoinAttribute> tryToInferJoinAttributes(DslModelEntity entity, String referencedEntityName) {
		ForeignKey fk = findUniqueFKByReferencedEntityName(entity, referencedEntityName);
		if ( fk != null ) {
			return buildJoinAttributesFromForeignKey(fk);
		}
		return null;
	}
	
	private ForeignKey findUniqueFKByReferencedEntityName(DslModelEntity entity, String referencedEntityName) {
		ForeignKey fkFound = null ;
		int count = 0 ;
		for (ForeignKey fk : entity.getForeignKeys() ) {
			if (referencedEntityName.equals(fk.getReferencedEntityName())) {
				fkFound = fk;
				count++;
			}
		}
		if ( fkFound != null && count == 1 ) {
			return fkFound ;
		}
		return null;
	}

}
