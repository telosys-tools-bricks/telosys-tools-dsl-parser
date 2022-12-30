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

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;

/**
 * DB-MODEL to DSL-MODEL : Links builder
 * 
 * @author Laurent GUERIN
 * 
 */

public class JoinEntityDetector {
	
	/**
	 * Constructor
	 */
	public JoinEntityDetector() {
		super();
	}

	/**
	 * Detect all "join entities" present in the model and mark them as "JoinEntity"
	 * @param model
	 */
	public void detectJoinEntities(DslModel model) {
		for ( Entity entity : model.getEntities() ) {
			if ( isJoinEntity(entity) ) {
				((DslModelEntity)entity).setJoinEntity(true);
			}
		}
	}
	
	private boolean isJoinEntity(Entity entity) {
		//--- Check if entity has 2 Foreign Keys
		if ( entity.getForeignKeys().size() != 2 ) {
			return false;
		}
		//--- Check if all attributes are in the PK and in a FK
		for ( Attribute attribute : entity.getAttributes() ) {
			if ( ! attribute.isKeyElement() ) { 
				return false ; // at least one attribute is not in PK 
			}
			if ( ! attribute.isFK() ) {
				return false ; // at least one attribute is not in FK
			}
		}
		//--- all conditions are met : this is a "Join Entity"
		return true ;
	}
}
