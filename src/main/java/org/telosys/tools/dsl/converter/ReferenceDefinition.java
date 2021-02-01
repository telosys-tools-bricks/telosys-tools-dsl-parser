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
package org.telosys.tools.dsl.converter;

import org.telosys.tools.commons.StrUtil;

public class ReferenceDefinition {

	private static final String EMPTY = "" ;
	
	private final String name ;
	private final String referencedName ;
	
	public ReferenceDefinition(String name, String referencedName) {
		super();
		if ( StrUtil.nullOrVoid(name) ) {
			throw new IllegalAccessError("name is null or void");
		}
		this.name = name;
		if ( StrUtil.nullOrVoid(referencedName) ) {
			this.referencedName = EMPTY;
		}
		else {
			this.referencedName = referencedName;
		}
	}
	
	public ReferenceDefinition(String name) {
		super();
		if ( StrUtil.nullOrVoid(name) ) {
			throw new IllegalAccessError("name is null or void");
		}
		this.name = name;
		this.referencedName = EMPTY;
	}
	
	public String getName() {
		return name;
	}

	public String getReferencedName() {
		return referencedName;
	}

	public boolean hasReferencedName() {
		return ! StrUtil.nullOrVoid(referencedName);
	}

}
