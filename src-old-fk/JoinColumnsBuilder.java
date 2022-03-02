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
import org.telosys.tools.dsl.model.DslModelForeignKey;
import org.telosys.tools.dsl.model.DslModelJoinColumn;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyColumn;
import org.telosys.tools.generic.model.JoinColumn;

public class JoinColumnsBuilder {

	private final String messagePrefix ;
	
	public JoinColumnsBuilder(String messagePrefix) {
		this.messagePrefix = messagePrefix;
	}
	
	private DslModelJoinColumn buildJoinColumn(String columnName, String referencedColumnName) {
	    if ( StrUtil.nullOrVoid(columnName) ) {
	    	throw new IllegalStateException(messagePrefix + " : no column name ");
	    }
	    if ( referencedColumnName.isEmpty() ) {
		    return new DslModelJoinColumn(columnName);
	    }
	    else {
		    return new DslModelJoinColumn(columnName, referencedColumnName);
	    }
	}

	public List<JoinColumn> buildJoinColumnsFromRefDef(ReferenceDefinitions columnsRefdef) {
		List<ReferenceDefinition> refDefList = columnsRefdef.getList();
	    if ( refDefList.isEmpty() ) {
	    	throw new IllegalStateException(messagePrefix + " : no join column (empty list)");
	    }
		List<JoinColumn> joinColumns = new LinkedList<>();
		for ( ReferenceDefinition rd : refDefList) {
			DslModelJoinColumn jc = buildJoinColumn(rd.getName(), rd.getReferencedName());
			
			// TODO : or do it in LINK for all Join Col ?
//			jc.setInsertable(insertable);
//			jc.setUpdatable(updatable);
//			jc.setNullable(nullable);
			
			joinColumns.add(jc);
		}
		return joinColumns;
	}

	
	/**
	 * Build join columns list from the given Foreign Key
	 * @param fk
	 * @return
	 */
//	public List<JoinColumn> buildJoinColumnsFromForeignKey(ForeignKey fk) {
	public List<JoinColumn> buildJoinColumnsFromForeignKey(DslModelForeignKey fk) {
		List<JoinColumn> joinColumns = new LinkedList<>();
		if ( fk != null ) {
//			for ( ForeignKeyColumn fkCol : fk.getColumns() ) {
//				DslModelJoinColumn jc = buildJoinColumn(fkCol.getColumnName(), 
//						fkCol.getReferencedColumnName());
//				joinColumns.add(jc);
//			}
		}
		return joinColumns;
	}

	/**
	 * Try to infer join columns from the given referenced table <br>
	 * by searching in the entity a unique FK targeting this table 
	 * @param entity
	 * @param referencedTableName
	 * @return
	 */
	public List<JoinColumn> tryToInferJoinColumns(DslModelEntity entity, String referencedTableName) {
		ForeignKey fk = findUniqueFKByReferencedTableName(entity, referencedTableName);
		if ( fk != null ) {
//			return buildJoinColumnsFromForeignKey(fk);
		}
		return null;
	}
	
	private ForeignKey findUniqueFKByReferencedTableName(DslModelEntity entity, String refTableName) {
		ForeignKey fkFound = null ;
		int count = 0 ;
		for(ForeignKey fk : entity.getDatabaseForeignKeys() ) {
			if(refTableName.equals(fk.getReferencedTableName())) {
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
