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
