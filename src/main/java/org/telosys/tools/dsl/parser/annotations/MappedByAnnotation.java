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

import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;

public class MappedByAnnotation extends AnnotationDefinition {

	public MappedByAnnotation() {
		super(AnnotationName.MAPPED_BY, AnnotationParamType.STRING, AnnotationScope.LINK);
	}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) {
		checkParamValue(paramValue);
		link.setMappedBy((String)paramValue);
		// has MappedBy => inverse side
		link.setInverseSide(true);
		link.setOwningSide(false);
		
		// TODO : Check MappedBy validity AFTER all "apply"  (next step)
		// check existence :
		// . referenced entity
		//     dslModel.getEntityByClassName(targetEntityName);
		// . owning side link existence in the target entity
		//     targetEntity.getLinkByFieldName(attributeName) 
	}

}
