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
package org.telosys.tools.dsl.parser;

import org.telosys.tools.dsl.parser.model.DomainType;

/**
 * This class contains the name and type of a field <br>
 * e.g. "firstName : string" or "books : Book[]" <br>
 * 
 * @author Laurent GUERIN
 *
 */
public class FieldNameAndType {

	private final String name ;
	private final String type ;
	private final int    cardinality ;
	private final DomainType domainType;
	
	public FieldNameAndType(String name, String type, int cardinality, DomainType domainType) {
		super();
		this.name = name;
		this.type = type;
		this.cardinality = cardinality;
		this.domainType = domainType;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}

	public int getCardinality() {
		return cardinality;
	}

	public DomainType getDomainType() {
		return domainType;
	}

}
