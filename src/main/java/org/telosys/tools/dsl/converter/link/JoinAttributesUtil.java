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

import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelForeignKey;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.LinkAttribute;

public class JoinAttributesUtil {

	private JoinAttributesUtil() {
		super();
	}
	
	/**
	 * Try to infer join attributes from the given referenced entity <br>
	 * by searching in the origin entity a unique FK targeting the referenced entity 
	 * @param entity
	 * @param referencedTableName
	 * @return
	 */
	public static List<LinkAttribute> tryToInferJoinAttributes(DslModelEntity entity, String referencedEntityName) {
		DslModelForeignKey fk = findUniqueFKForReferencedEntityName(entity, referencedEntityName);
		if ( fk != null ) {
			return fk.getLinkAttributes();
		}
		return null;
	}
	
	private static DslModelForeignKey findUniqueFKForReferencedEntityName(DslModelEntity entity, String referencedEntityName) {
		ForeignKey fkFound = null ;
		int count = 0 ;
		for (ForeignKey fk : entity.getForeignKeys() ) {
			if (referencedEntityName.equals(fk.getReferencedEntityName())) {
				fkFound = fk;
				count++;
			}
		}
		if ( fkFound != null && count == 1 ) {
			return (DslModelForeignKey) fkFound ;
		}
		return null;
	}

	/**
	 * Returns count of duplicate JoinAttribute (based on origin attribute name)
	 * @param list
	 * @return
	 */
	public static int numberOfDuplicates(List<LinkAttribute> list) {
		List<String> names = new LinkedList<>();
	    for(LinkAttribute jc : list) {
	    	names.add(jc.getOriginAttributeName());
	    }
	    return numberOfStringDuplicates(names) ;
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
