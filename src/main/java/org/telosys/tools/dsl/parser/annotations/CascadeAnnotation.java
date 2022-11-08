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

import java.util.List;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.enums.CascadeOption;

/**
 * 'Cascade' annotation
 *  
 * @author Laurent Guerin
 *
 */
public class CascadeAnnotation extends AnnotationDefinition {

	public CascadeAnnotation() {
		super(AnnotationName.CASCADE, AnnotationParamType.LIST, AnnotationScope.LINK);
	}
	
	@Override
	public void afterCreation(String entityName, String fieldName, DomainAnnotation annotation) throws ParamError {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) annotation.getParameterAsList();
		if ( list.isEmpty() ) {
			throw newParamError(entityName, fieldName, "at least 1 cascade option required");
		}
	}
	
	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) throws ParamError {
		checkParamValue(entity, link, paramValue);
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) paramValue;
		CascadeOptions cascadeOptions = new CascadeOptions();
		for (String s : list ) {
			cascadeOptions.add(getCascadeOption(entity, link, s));
		}
		link.setCascadeOptions(cascadeOptions);
	}

	private CascadeOption getCascadeOption(DslModelEntity entity, DslModelLink link, String s) throws ParamError {
		if ( CascadeOption.ALL.getLongText().equalsIgnoreCase(s) 
			|| CascadeOption.ALL.getShortText().equalsIgnoreCase(s) ) {
			return CascadeOption.ALL;
		}
		if ( CascadeOption.MERGE.getLongText().equalsIgnoreCase(s) 
			|| CascadeOption.MERGE.getShortText().equalsIgnoreCase(s) ) {
			return CascadeOption.MERGE;
		}
		if (CascadeOption.PERSIST.getLongText().equalsIgnoreCase(s)
			|| CascadeOption.PERSIST.getShortText().equalsIgnoreCase(s) ) {
			return CascadeOption.PERSIST;
		}
		if (CascadeOption.REFRESH.getLongText().equalsIgnoreCase(s)
			|| CascadeOption.REFRESH.getShortText().equalsIgnoreCase(s) ) {
			return CascadeOption.REFRESH;
		}
		if (CascadeOption.REMOVE.getLongText().equalsIgnoreCase(s)
			|| CascadeOption.REMOVE.getShortText().equalsIgnoreCase(s) ) {
			return CascadeOption.REMOVE;
		}
		throw newParamError(entity, link, "invalid cascade option '" + s + "'");
	}
}
