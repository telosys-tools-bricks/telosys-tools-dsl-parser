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
package org.telosys.tools.dsl.model.dbmodel;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.db.model.DatabaseForeignKey;
import org.telosys.tools.db.model.DatabaseForeignKeyColumn;
import org.telosys.tools.dsl.model.DslModelForeignKey;
import org.telosys.tools.dsl.model.DslModelForeignKeyAttribute;

public class DbToForeignKeyConverter
{
	/**
	 * Constructor
	 */
	public DbToForeignKeyConverter() {
		super();
	}

	public DslModelForeignKey createForeignKey( DatabaseForeignKey dbFK )  {	
		// ForeignKeyInDbModel foreignKey = new ForeignKeyInDbModel(dbFK.getForeignKeyName());	
		
//		String originTableName = "";
//		String referencedTableName = "";
//		for ( DatabaseForeignKeyColumn dbFkCol : dbFK.getForeignKeyColumns() ) {
//			// keep table names for FK
//			originTableName = dbFkCol.getFkTableName();
//			referencedTableName = dbFkCol.getPkTableName();
//
//			int ordinal = dbFkCol.getFkSequence();
//			String originAttributeName = repositoryRules.getAttributeName(dbFkCol.getFkColumnName()) ;
//			String referencedAttributeName = repositoryRules.getAttributeName(dbFkCol.getPkColumnName());
//			ForeignKeyAttributeInDbModel foreignKeyColumn = new ForeignKeyAttributeInDbModel(
//					ordinal, originAttributeName, referencedAttributeName);
//					
//			foreignKeyColumn.setOriginAttributeName(repositoryRules.getAttributeName(dbFkCol.getFkColumnName()));
//			foreignKeyColumn.setReferencedAttributeName(referencedAttributeName);
//			
//			foreignKeyColumn.setUpdateRuleCode( dbFkCol.getUpdateRule() );
//			foreignKeyColumn.setDeleteRuleCode( dbFkCol.getDeleteRule() );
//			foreignKeyColumn.setDeferrableCode( dbFkCol.getDeferrability() );
//			
//			//foreignKey.storeForeignKeyColumn(foreignKeyColumn);
//			foreignKey.addAttribute(foreignKeyColumn);
//		}
//		
//		String originEntityName = repositoryRules.getEntityClassName(originTableName);
//		String referencedEntityName = repositoryRules.getEntityClassName(referencedTableName);
//		foreignKey.setOriginEntityName(originEntityName);
//		foreignKey.setReferencedEntityName(referencedEntityName);

		//------------------------------------------------------------------
		String originTableName = "";
		String referencedTableName = "";
		//--- Create all FK attributes
		List<DslModelForeignKeyAttribute> fkAttributes = new LinkedList<>();
		for ( DatabaseForeignKeyColumn dbFkCol : dbFK.getForeignKeyColumns() ) {
			// keep table names for FK level
			originTableName = dbFkCol.getFkTableName();
			referencedTableName = dbFkCol.getPkTableName();
			// build FK attribute
			String originAttributeName     = NameConverter.columnNameToAttributeName(dbFkCol.getFkColumnName());
			String referencedAttributeName = NameConverter.columnNameToAttributeName(dbFkCol.getPkColumnName());
			int ordinal = dbFkCol.getFkSequence();
			DslModelForeignKeyAttribute fkAttribute = new DslModelForeignKeyAttribute(
					ordinal, originAttributeName, referencedAttributeName);
			fkAttributes.add(fkAttribute);
		}
		
		//--- Create FK
		String fkName = dbFK.getForeignKeyName();
		String originEntityName     = NameConverter.tableNameToEntityName(originTableName);
		String referencedEntityName = NameConverter.tableNameToEntityName(referencedTableName);
		DslModelForeignKey foreignKey = new DslModelForeignKey(fkName, originEntityName, referencedEntityName);
		
		//--- Add all FK attributes in FK
		for ( DslModelForeignKeyAttribute fkAttribute : fkAttributes ) {
			foreignKey.addAttribute(fkAttribute);
		}
		
		return foreignKey ;
	}
	
}
