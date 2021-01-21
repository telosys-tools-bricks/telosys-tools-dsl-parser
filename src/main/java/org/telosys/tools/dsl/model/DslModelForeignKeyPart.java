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
package org.telosys.tools.dsl.model;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.generic.model.ForeignKeyPart;

public class DslModelForeignKeyPart implements ForeignKeyPart
{		
	private String fkName;
	private String referencedTable;
	private String referencedColumn;
	private String referencedEntity;
	private String referencedAttribute;
	
	/**
	 * Constructor
	 * @param fkName
	 * @param referencedTable
	 * @param referencedColumn
	 * @param referencedEntity
	 * @param referencedAttribute
	 */
	public DslModelForeignKeyPart(String fkName, 
			String referencedTable, String referencedColumn,
			String referencedEntity, String referencedAttribute) {
		super();
		if ( StrUtil.nullOrVoid(fkName) ) throw new IllegalStateException("invalid 'fkName'");
		if ( StrUtil.nullOrVoid(referencedTable) ) throw new IllegalStateException("invalid 'referencedTable'");
		if ( StrUtil.nullOrVoid(referencedColumn) ) throw new IllegalStateException("invalid 'referencedColumn'");
		if ( StrUtil.nullOrVoid(referencedEntity) ) throw new IllegalStateException("invalid 'referencedEntity'");
		if ( StrUtil.nullOrVoid(referencedAttribute) ) throw new IllegalStateException("invalid 'referencedAttribute'");
		this.fkName = fkName;
		this.referencedTable = referencedTable;
		this.referencedColumn = referencedColumn;
		this.referencedEntity = referencedEntity;
		this.referencedAttribute = referencedAttribute;
	}

	@Override
	public String getFkName() {
		return fkName;
	}

	@Override
	public String getReferencedTable() {
		return referencedTable;
	}

	@Override
	public String getReferencedColumn() {
		return referencedColumn;
	}

	@Override
	public String getReferencedEntity() {
		return referencedEntity;
	}

	@Override
	public String getReferencedAttribute() {
		return referencedAttribute;
	}
}