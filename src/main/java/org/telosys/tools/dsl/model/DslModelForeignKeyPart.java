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
	private String referencedEntityName;
	private String referencedAttributeName;
	
	private IllegalArgumentException newNullOrVoidArg(String argumentName) {
		return new IllegalArgumentException("DslModelForeignKeyPart constructor : " 
				+ "argument '" + argumentName + "' is null or void");
	}
	
	/**
	 * Constructor
	 * @param fkName
	 * @param referencedEntityName
	 * @param referencedAttributeName
	 */
	public DslModelForeignKeyPart(String fkName, 
			String referencedEntityName, String referencedAttributeName) {
		super();
		if ( StrUtil.nullOrVoid(fkName) ) throw newNullOrVoidArg("fkName");
		if ( StrUtil.nullOrVoid(referencedEntityName) ) throw newNullOrVoidArg("referencedEntityName");
		if ( StrUtil.nullOrVoid(referencedAttributeName) ) throw newNullOrVoidArg("referencedAttributeName");
		this.fkName = fkName;
		this.referencedEntityName = referencedEntityName;
		this.referencedAttributeName = referencedAttributeName;
	}

	@Override
	public String getFkName() {
		return fkName;
	}

	@Override
	public String getReferencedEntityName() {
		return referencedEntityName;
	}

	@Override
	public String getReferencedAttributeName() {
		return referencedAttributeName;
	}
}