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
package org.telosys.tools.dsl.converter.link;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelJoinColumn;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyColumn;
import org.telosys.tools.generic.model.JoinColumn;

public class JoinColumnsUtil {

	
	private JoinColumnsUtil() {
		super();
	}
	
//	public static DslModelJoinColumn buildJoinColumn(String columnName, String referencedColumnName) {
//	    if ( StrUtil.nullOrVoid(columnName) ) {
//	    	throw new IllegalStateException("Cannot build JoinColumn : column name is null or void");
//	    }
//	    if ( StrUtil.nullOrVoid(referencedColumnName) ) {
//	    	throw new IllegalStateException("Cannot build JoinColumn : referenced column name is null or void");
//	    }
//	    return new DslModelJoinColumn(columnName, referencedColumnName);
//	}
//	
//	/**
//	 * Build join columns list from the given Foreign Key
//	 * @param fk
//	 * @return
//	 */
//	public static List<JoinColumn> buildJoinColumnsFromForeignKey(ForeignKey fk) {
//		List<JoinColumn> joinColumns = new LinkedList<>();
//		if ( fk != null ) {
//			for ( ForeignKeyColumn fkCol : fk.getColumns() ) {
////				DslModelJoinColumn jc = new DslModelJoinColumn(fkCol.getColumnName());
////				jc.setReferencedColumnName(fkCol.getReferencedColumnName());
//				DslModelJoinColumn jc = buildJoinColumn(fkCol.getColumnName(), 
//						fkCol.getReferencedColumnName());
//				joinColumns.add(jc);
//			}
//		}
//		return joinColumns;
//	}

//	/**
//	 * Try to infer join columns from the given referenced table <br>
//	 * by searching in the entity a unique FK targeting this table 
//	 * @param entity
//	 * @param referencedTableName
//	 * @return
//	 */
//	public static List<JoinColumn> tryToInferJoinColumns(DslModelEntity entity, String referencedTableName) {
//		ForeignKey fk = findUniqueFKByReferencedTableName(entity, referencedTableName);
//		if ( fk != null ) {
//			return buildJoinColumnsFromForeignKey(fk);
//		}
//		return null;
//	}
//	
//	private static ForeignKey findUniqueFKByReferencedTableName(DslModelEntity entity, String refTableName) {
//		ForeignKey fkFound = null ;
//		int count = 0 ;
//		for(ForeignKey fk : entity.getDatabaseForeignKeys() ) {
//			if(refTableName.equals(fk.getReferencedTableName())) {
//				fkFound = fk;
//				count++;
//			}
//		}
//		if ( fkFound != null && count == 1 ) {
//			return fkFound ;
//		}
//		return null;
//	}
	
	public static int numberOfDuplicates(List<JoinColumn> list) {
		List<String> jcNames = new LinkedList<>();
	    for(JoinColumn jc : list) {
	    	jcNames.add(jc.getName());
	    }
	    return numberOfStringDuplicates(jcNames) ;
	}
	private static int numberOfStringDuplicates(List<String> list) {
		int n = 0 ;
		Set<String> uniques = new HashSet<>();
	    for(String s : list) {
	        if(!uniques.add(s)) {
	            n++;
	        }
	    }
	    return n;
	}
}
