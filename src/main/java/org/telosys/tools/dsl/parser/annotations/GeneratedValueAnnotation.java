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

import java.util.Arrays;
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
 *  . GeneratedValue(AUTO)
 *  . GeneratedValue(IDENTITY)
 *  . GeneratedValue(SEQUENCE [, GeneratorName, SequenceName [, AllocationSize ] ])
 *  . GeneratedValue(TABLE [, GeneratorName, TableName [, PkColumnName, PkColumnValue, ValueColumnName [, AllocationSize ] ] ])
 *   
 * Added in v 3.4.0
 * 
 * @author Laurent Guerin
 *
 */
public class GeneratedValueAnnotation extends AnnotationDefinition {

	private static final String AUTO = "AUTO";
	private static final String IDENTITY = "IDENTITY";
	private static final String SEQUENCE = "SEQUENCE";
	private static final String TABLE = "TABLE";
	private static final List<String> stategies = Arrays.asList(AUTO, IDENTITY, SEQUENCE, TABLE) ;
	
	public GeneratedValueAnnotation() {
		super(AnnotationName.GENERATED_VALUE, AnnotationParamType.LIST, AnnotationScope.ATTRIBUTE);
	}
	
	@Override
	public void afterCreation(String entityName, String fieldName, 
			 DomainAnnotation annotation) throws ParamError {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) annotation.getParameterAsList();
		if ( list.isEmpty() ) {
			throw newParamError(entityName, fieldName, "invalid parameter ('strategy' is required)");
		}
		else {
			// Check strategy 
			String strategy = list.get(0);
			if ( ! stategies.contains(strategy) ) {
				throw newParamError(entityName, fieldName, "invalid strategy '" + strategy + "'");
			}
			// Check number of parameters 
			int n = list.size();
			if ( strategy.equals(SEQUENCE) && n != 3 && n != 4 ) {
				throw newParamError(entityName, fieldName, "invalid number of parameters for 'SEQUENCE'");
			}
			if ( strategy.equals(TABLE) && n != 3 && n != 6 && n != 7 ) {
				throw newParamError(entityName, fieldName, "invalid number of parameters for 'TABLE'");
			}
		}
	}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelAttribute attribute, 
			Object paramValue) throws ParamError {
		checkParamValue(entity, attribute, paramValue);
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>)paramValue;
		String strategy = list.get(0); // cannot be empty 
		switch(strategy) {
		case AUTO :
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.AUTO);
			break;
		case IDENTITY :
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.IDENTITY);
			break;
		case SEQUENCE :
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.SEQUENCE);
			if ( list.size() >= 3 ) {
				attribute.setGeneratedValueGeneratorName(stringValue(list.get(1)));
				attribute.setGeneratedValueSequenceName(stringValue(list.get(2)));
			}
			if ( list.size() >= 4 ) {
				attribute.setGeneratedValueAllocationSize(toInt(entity, attribute, list.get(3)));
			}
			break;
		case TABLE:
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.TABLE);
			if ( list.size() >= 3 ) {
				attribute.setGeneratedValueGeneratorName(stringValue(list.get(1)));
				attribute.setGeneratedValueTableName(stringValue(list.get(2)));
			}
			if ( list.size() >= 6 ) {
				attribute.setGeneratedValueTablePkColumnName(stringValue(list.get(3)));
				attribute.setGeneratedValueTablePkColumnValue(stringValue(list.get(4)));
				attribute.setGeneratedValueTableValueColumnName(stringValue(list.get(5)));
			}
			if ( list.size() >= 7 ) {
				attribute.setGeneratedValueAllocationSize(toInt(entity, attribute, list.get(6)));
			}
			break;
		default:
			throw newParamError(entity, attribute, "invalid strategy '" + strategy + "'");
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
