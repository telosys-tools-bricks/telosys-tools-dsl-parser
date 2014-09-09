/**
 * Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.dsl.parser.model;

import java.math.BigDecimal;

/**
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre
 * @version 1.0
 * @date 2014-05-13
 */
public class DomainEnumerationForDecimal extends DomainEnumeration<BigDecimal> {

    /**
     * Constructor 
     * @param name enumeration name
     */
    public DomainEnumerationForDecimal(String name) {
        super(name, DomainEnumerationType.DECIMAL);
    }

	@Override
	public String toString() {
		return "Enumeration '" + getName() + "' (type = decimal) "+ super.toString();
	}
    
}
