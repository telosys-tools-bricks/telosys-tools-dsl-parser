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

import org.telosys.tools.dsl.commons.StringUtil;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.enums.GeneratedValueStrategy;

/**
 * "GeneratedValue" annotation 
 *  . GeneratedValue( AUTO )
 *  . GeneratedValue( IDENTITY )
 *  . GeneratedValue( SEQUENCE, sequenceName  [, allocationSize [, initialValue ] ] )
 *  . GeneratedValue( TABLE,    pkValue       [, allocationSize [, initialValue ] ] )
 *   
 * Added in v 3.4.0, changed in v 4.1.0
 * 
 * @author Laurent Guerin
 *
 */
public class GeneratedValueAnnotation extends AnnotationDefinition {

	private static final String AUTO = "AUTO";
	private static final String IDENTITY = "IDENTITY";
	private static final String SEQUENCE = "SEQUENCE";
	private static final String TABLE = "TABLE";
	
	public GeneratedValueAnnotation() {
		super(AnnotationName.GENERATED_VALUE, AnnotationParamType.LIST, AnnotationScope.ATTRIBUTE);
	}
	
	@Override
	public void afterCreation(String entityName, String fieldName, 
			 DomainAnnotation annotation) throws ParamError {
		List<String> list = annotation.getParameterAsList();
		if ( list.isEmpty() ) {
			throw new ParamError( "'strategy' parameter is required)");
		}
		else {
			//--- Check strategy 
			String strategy = list.get(0);
			switch (strategy) {
			case AUTO : 
				// GeneratedValue(AUTO) 
				checkNumberOfParameters(annotation, 1, 1, "'AUTO' is the only expected parameter");
				break;
			case IDENTITY : 
				// GeneratedValue(IDENTITY) 
				checkNumberOfParameters(annotation, 1, 1, "'IDENTITY' is the only expected parameter");
				break;
			case SEQUENCE : 
				// GeneratedValue(SEQUENCE, sequenceName [, allocationSize [, initialValue ] ] )
				checkNumberOfParameters(annotation, 2, 4, "2 to 4 parameters expected for 'SEQUENCE'");
				break;
			case TABLE : 
				// GeneratedValue(TABLE, pkValueId [, allocationSize [, initialValue ] ] ) 
				checkNumberOfParameters(annotation, 2, 4, "2 to 4 parameters expected for 'TABLE'");
				break;
			default :
				throw new ParamError("invalid strategy '" + strategy + "'");
			}
		}
	}
	
	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelAttribute attribute, 
			Object paramValue) throws ParamError {
		checkParamValue(entity, attribute, paramValue);
		String strategy = getParameter(paramValue, 0); // cannot be empty 
		switch(strategy) {
		case AUTO :
			// GeneratedValue(AUTO) 
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.AUTO);
			break;
		case IDENTITY :
			// GeneratedValue(IDENTITY) 
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.IDENTITY);
			break;
		case SEQUENCE :
			// GeneratedValue(SEQUENCE, sequenceName [, allocationSize [, initialValue ] ] ) 
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.SEQUENCE); // #0
			attribute.setGeneratedValueSequenceName(stringValue(getParameter(paramValue, 1))); // #1
			if ( hasParameter(paramValue, 2) ) {
				attribute.setGeneratedValueAllocationSize(toInt(getParameter(paramValue, 2))); // #2
			}
			if ( hasParameter(paramValue, 3) ) {
				attribute.setGeneratedValueInitialValue(toInt(getParameter(paramValue, 3))); // #3
			}
			break;
		case TABLE:
			// GeneratedValue(TABLE, pkValueId [, allocationSize [, initialValue ] ] ) 
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.TABLE); // #0
			attribute.setGeneratedValueTablePkColumnValue(stringValue(getParameter(paramValue, 1))); // #1
			if ( hasParameter(paramValue, 2) ) {
				attribute.setGeneratedValueAllocationSize(toInt(getParameter(paramValue, 2))); // #2
			}
			if ( hasParameter(paramValue, 3) ) {
				attribute.setGeneratedValueInitialValue(toInt(getParameter(paramValue, 3))); // #3
			}
			break;
		default:
			throw new ParamError("invalid strategy '" + strategy + "'");
		}
	}
	
	private String stringValue(String value) {
		// remove all void chars ( blank, tab, cr, lf, ...)
		String s = value.trim(); 
		// remove quotes if any
		if (s.startsWith("\"") && s.endsWith("\"")) {
			return StringUtil.unquote(s);
		} else {
			return s;
		}
	}
}
