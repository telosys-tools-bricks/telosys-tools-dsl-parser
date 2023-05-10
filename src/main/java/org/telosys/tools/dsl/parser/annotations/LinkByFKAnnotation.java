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
package org.telosys.tools.dsl.parser.annotations;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelForeignKey;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.generic.model.ForeignKey;

/**
 * LinkByFK annotation 
 * Example :
 *   LinkByFK("ForeignKeyName")  
 *   
 * @author Laurent Guerin
 */
public class LinkByFKAnnotation extends AnnotationDefinition {

	/**
	 * Constructor
	 */
	public LinkByFKAnnotation() {
		super(AnnotationName.LINK_BY_FK, AnnotationParamType.STRING, AnnotationScope.LINK);
	}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) throws ParamError {
		checkParamValue(entity, link, paramValue);
		String fkName = (String)paramValue;
		
		// Try to get and check FK
		DslModelForeignKey fk = getForeignKeyByName(entity, fkName);
		checkIfLinkIsCompatibleWithForeignKey(link, fk);

		link.setAttributes(fk.getLinkAttributes()); // new in 3.4.0
		
		// other flags 
		link.setBasedOnForeignKey(true);
		link.setForeignKeyName(fkName);
	}
	
	private DslModelForeignKey getForeignKeyByName(DslModelEntity entity, String fkName) throws ParamError {
		if ( ! StrUtil.nullOrVoid(fkName) ) {
			ForeignKey fk = entity.getForeignKeyByName(fkName);
			if ( fk != null ) {
				return (DslModelForeignKey) fk;
			}
			else {
				// FK not found => ERROR
				throw new ParamError("cannot found Foreign Key '" + fkName + "' in entity");
			}
		}
		else {
			// no FK name => ERROR
			throw new ParamError("no Foreign Key name");
		}
	}

	private void checkIfLinkIsCompatibleWithForeignKey(DslModelLink link, DslModelForeignKey fk) throws ParamError  {
		String linkReferencedEntityName = link.getReferencedEntityName();
		String fkReferencedEntityName = fk.getReferencedEntityName();
		if ( linkReferencedEntityName != null ) {
			if ( ! linkReferencedEntityName.equals(fkReferencedEntityName) ) {
				throw new ParamError("invalid Foreign Key : reference '" + fkReferencedEntityName + "' != '" + linkReferencedEntityName + "'");
			}
		}
		else {
			throw new ParamError("cannot check reference because link target is null");
		}
	}

}
