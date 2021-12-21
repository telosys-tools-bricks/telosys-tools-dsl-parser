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
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.JoinColumn;

public class LinkByFKAnnotation extends LinkByAnnotation {

	public LinkByFKAnnotation() {
		super(AnnotationName.LINK_BY_FK, AnnotationParamType.STRING);
	}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) throws ParsingError {
		checkParamValue(entity, link, paramValue);
		String fkName = (String)paramValue;
//		ForeignKey fk = getForeignKeyByName(entity, fkName);
//		
//		JoinColumnsBuilder jcb = getJoinColumnsBuilder();
//		List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromForeignKey(fk);
		
		List<JoinColumn> joinColumns = getJoinColumns(entity, link, fkName);
		
		// Apply to link
		link.setJoinColumns(joinColumns);
		
		// other flags 
		link.setBasedOnForeignKey(true);
		link.setForeignKeyName(fkName);
		
	}
	
	private List<JoinColumn> getJoinColumns(DslModelEntity entity, DslModelLink link, String fkName) throws ParsingError {
		try {
			ForeignKey fk = getForeignKeyByName(entity, fkName);
			checkIfForeignKeyIsCompatible(link, fk);
			JoinColumnsBuilder jcb = getJoinColumnsBuilder();
			return jcb.buildJoinColumnsFromForeignKey(fk);
		} catch (Exception e) {
			throw newParamError(entity, link, e.getMessage());
		}
	}
	
	private ForeignKey getForeignKeyByName(DslModelEntity entity, String fkName) throws Exception {
		if ( ! StrUtil.nullOrVoid(fkName) ) {
			ForeignKey fk = entity.getDatabaseForeignKeyByName(fkName);
			if ( fk != null ) {
				return fk;
			}
			else {
				// FK not found => ERROR
//				throw new IllegalStateException("@LinkByFK : cannot found Foreign Key '" + fkName + "' in entity" );
				throw new Exception("cannot found Foreign Key '" + fkName + "' in entity");
			}
		}
		else {
			// no FK name => ERROR
//			throw new IllegalStateException("@LinkByFK : no Foreign Key name");
			throw new Exception("no Foreign Key name");
		}
	}

	private void checkIfForeignKeyIsCompatible(DslModelLink link, ForeignKey fk) throws Exception {
		String linkTable = link.getTargetTableName();
		String fkTable = fk.getReferencedTableName();
		if ( linkTable != null ) {
			if ( ! linkTable.equals(fkTable) ) {
				//throw new IllegalStateException("@LinkByFK : invalid Foreign Key : ref table '" + fkTable + "' != '" + linkTable + "'");
				throw new Exception("invalid Foreign Key : ref table '" + fkTable + "' != '" + linkTable + "'");
			}
		}
		else {
			//throw new IllegalStateException("@LinkByFK : cannot check reference because link table is null");
			throw new Exception("cannot check reference because link table is null");
		}
	}

}
