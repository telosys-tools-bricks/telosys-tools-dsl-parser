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
import org.telosys.tools.generic.model.ForeignKeyAttribute;

public class DslModelForeignKeyAttribute implements ForeignKeyAttribute {
	
	private final int    ordinal ;

	private final String originAttributeName ;
	
	private final String referencedAttributeName ;

	/**
	 * Constructor
	 * @param ordinal
	 * @param originAttributeName
	 * @param referencedAttributeName
	 */
	public DslModelForeignKeyAttribute(int ordinal, String originAttributeName, String referencedAttributeName) {
		super();
		if (ordinal < 0 ) {
			throw new IllegalArgumentException("invalid FK attribute : ordinal < 0");
		}
		if (StrUtil.nullOrVoid(originAttributeName) ) {
			throw new IllegalArgumentException("invalid FK attribute : origin attribute is null or empty");
		}
		if (StrUtil.nullOrVoid(referencedAttributeName) ) {
			throw new IllegalArgumentException("invalid FK attribute : referenced attribute is null or empty");
		}
		this.ordinal = ordinal;
		this.originAttributeName = originAttributeName;
		this.referencedAttributeName = referencedAttributeName;
	}
	
	@Override
	public int getOrdinal() {
		return ordinal;
	}
	
	@Override
	public String getOriginAttributeName() {
		return originAttributeName;
	}

	@Override
	public String getReferencedAttributeName() {
		return referencedAttributeName;
	}

}
