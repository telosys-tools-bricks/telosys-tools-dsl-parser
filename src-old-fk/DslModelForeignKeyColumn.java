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

import org.telosys.tools.generic.model.ForeignKeyColumn;

public class DslModelForeignKeyColumn implements ForeignKeyColumn {
	
	private final int    sequence ;

	private final String columnName ;
	
	private final String referencedColumnName ;

	
	public DslModelForeignKeyColumn(int sequence, String columnName, String referencedColumnName) {
		super();
		this.sequence = sequence;
		this.columnName = columnName;
		this.referencedColumnName = referencedColumnName;
	}
	
	@Override
	public String getColumnName() {
		return columnName;
	}
//	public void setColumnName(String columnName) {
//		this.columnName = columnName;
//	}

	@Override
	public String getReferencedColumnName() {
		return referencedColumnName;
	}
//	public void setReferencedColumnName(String referencedColumnName) {
//		this.referencedColumnName = referencedColumnName;
//	}

	@Override
	public int getSequence() {
		return sequence;
	}
//	public void setSequence(int sequence) {
//		this.sequence = sequence;
//	}

}
