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
package org.telosys.tools.dsl.commons;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelForeignKeyPart;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyAttribute;
import org.telosys.tools.generic.model.ForeignKeyPart;

public class AttributeFKUtil {

	private AttributeFKUtil() {
	}

	/**
	 * Apply the given Foreign Key to all attributes involved in it <br>
	 * Set FKPart, isFKSimple, isFKComposite, referencedEntityClassName 
	 * @param fk
	 * @param model
	 */
	public static void applyFKToAttributes(ForeignKey fk, DslModel model) {
		String originEntityName = fk.getOriginEntityName();
		DslModelEntity originEntity = (DslModelEntity) model.getEntityByClassName(originEntityName);
		checkNotFound(originEntity, "origin entity", originEntityName);
		
		String referencedEntityName = fk.getReferencedEntityName();
		DslModelEntity referencedEntity = (DslModelEntity) model.getEntityByClassName(referencedEntityName);
		checkNotFound(referencedEntity, "referenced entity", referencedEntityName);
		
		for ( ForeignKeyAttribute fkAttrib : fk.getAttributes() ) {
			// get referenced attribute
			String referencedAttributeName = fkAttrib.getReferencedAttributeName();
			DslModelAttribute referencedAttribute = (DslModelAttribute) referencedEntity.getAttributeByName(referencedAttributeName);
			checkNotFound(referencedAttribute, "referenced attribute", referencedAttributeName);

			// Build FK part
			ForeignKeyPart fkPart = new DslModelForeignKeyPart(
					fk.getName(),
					referencedEntity.getClassName(), 
					referencedAttribute.getName());

			// get attribute
			String originAttributeName = fkAttrib.getOriginAttributeName();
			DslModelAttribute attribute = (DslModelAttribute) originEntity.getAttributeByName(originAttributeName);
			checkNotFound(attribute, "origin attribute", originAttributeName);
			
			// Add FK part
			attribute.addFKPart(fkPart);
			
			// Set : isFKSimple, isFKComposite, referencedEntityClassName 
			if ( fk.getAttributes().size() > 1 ) {
				// this attribute is a part of a composite FK
				attribute.setFKComposite(true);
				// if attribute is involved in multiple FK : 
				// set it only if not already set (to preserve potential Simple FK priority)
				if ( StrUtil.nullOrVoid( attribute.getReferencedEntityClassName() ) ) {
					attribute.setReferencedEntityClassName(referencedEntityName);
				}				
			}
			else {
				// this attribute is the single attribute of a simple FK
				attribute.setFKSimple(true);
				// if attribute is involved in multiple FK : 
				// simple FK is priority => always set it
				attribute.setReferencedEntityClassName(referencedEntityName);
			}
		}
	}
	
	private static void checkNotFound(Object o, String what, String id) {
		if ( o == null ) {
			throw new IllegalStateException("FK error : cannot found " + what + " : '" + id + "'");
		}
	}	
}
