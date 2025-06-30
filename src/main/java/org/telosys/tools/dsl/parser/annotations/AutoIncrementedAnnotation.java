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

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.generic.model.enums.GeneratedValueStrategy;

/**
 * 'AutoIncremented' annotation
 * Since ver 4.1 this annotation is just a shortcut for 'GeneratedValue(IDENTITY)'
 * 
 * @author Laurent Guerin
 *
 */
public class AutoIncrementedAnnotation extends AnnotationDefinition {

	public AutoIncrementedAnnotation() {
		super(AnnotationName.AUTO_INCREMENTED, AnnotationParamType.NONE, AnnotationScope.ATTRIBUTE);
	}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelAttribute attribute, 
					  Object paramValue) throws ParamError {
		checkParamValue(entity, attribute, paramValue);
		// same as 'GeneratedValue(IDENTITY)'
		attribute.setGeneratedValueStrategy(GeneratedValueStrategy.IDENTITY); // v 4.1.0
	}
}
