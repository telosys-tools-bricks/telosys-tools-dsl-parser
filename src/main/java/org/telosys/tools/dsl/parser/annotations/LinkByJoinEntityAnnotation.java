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

import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.commons.JoinColumnsBuilder;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelJoinTable;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.JoinTable;

public class LinkByJoinEntityAnnotation extends LinkByAnnotation {

	public LinkByJoinEntityAnnotation() {
		super(AnnotationName.LINK_BY_JOIN_ENTITY, AnnotationParamType.STRING);
	}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) {
		checkParamValue(paramValue);
		String joinEntityName = (String) paramValue ;
		
		// Try to get the join table
		JoinTable joinTable = getJoinTable(model, link, joinEntityName);
		
		// Apply join table to link 
		link.setJoinTable(joinTable);
		
// Moved in  link converter (finalize)
//		// has Join Table => owning side
//		link.setOwningSide(true);
//		link.setInverseSide(false);
	}
	
	protected JoinTable getJoinTable(DslModel model, DslModelLink link, String joinEntityName) {
		DslModelEntity joinEntity = getJoinEntity( model, joinEntityName);
		List<ForeignKey> foreignKeys = joinEntity.getDatabaseForeignKeys();
		checkForeignKeys(foreignKeys, joinEntityName);
		return buildJoinTable(joinEntity, link, foreignKeys);
	}
	
	private DslModelEntity getJoinEntity(DslModel model, String joinEntityName) {
		DslModelEntity joinEntity = (DslModelEntity) model.getEntityByClassName(joinEntityName);
		if ( joinEntity == null ) {
			throw newException("unknown join entity '"+ joinEntityName + "'");
		}
		return joinEntity;
	}	
	
	private void checkForeignKeys(List<ForeignKey> foreignKeys, String joinEntityName) {
		if ( foreignKeys == null ) {
			throw newException("no foreign key in entity '"+ joinEntityName + "'");
		}
		if ( foreignKeys.size() != 2 ) {
			throw newException( "2 foreign keys expected in join entity '"+ joinEntityName + "' (" + foreignKeys.size() + " found)");
		}
	}
	
	private JoinTable buildJoinTable(Entity joinEntity, DslModelLink link, List<ForeignKey> foreignKeys) {
		ForeignKey fk1 = foreignKeys.get(0);
		ForeignKey fk2 = foreignKeys.get(1);
		ForeignKey joinColumnsFK = getFKReferencingTable(link.getSourceTableName(), fk1, fk2 );
		ForeignKey inverseJoinColumnsFK = getFKReferencingTable(link.getTargetTableName(), fk1, fk2 );
		if ( joinColumnsFK.getName().equals(inverseJoinColumnsFK.getName()) ) {
			throw newException("the 2 foreign keys are identical");
		}
//		JoinColumnsBuilder jcb = new JoinColumnsBuilder("@"+ AnnotationName.LINK_BY_JOIN_ENTITY ) ;
		JoinColumnsBuilder jcb = getJoinColumnsBuilder();

		List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromForeignKey(joinColumnsFK);

		List<JoinColumn> inverseJoinColumns = jcb.buildJoinColumnsFromForeignKey(inverseJoinColumnsFK);
		
		return new DslModelJoinTable(joinEntity, joinColumns, inverseJoinColumns);
	}
	
	private ForeignKey getFKReferencingTable(String tableName, ForeignKey fk1, ForeignKey fk2 ) {
		if ( tableName.equals(fk1.getReferencedTableName()) ) {
			return fk1 ;
		}
		else if ( tableName.equals(fk2.getReferencedTableName()) ) {
			return fk2 ;
		}
		else {
			throw newException("join entity does not have FK referencing table '" + tableName + "'");
		}
	}
	
}
