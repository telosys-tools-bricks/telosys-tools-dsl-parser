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

import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.enums.GeneratedValueStrategy;

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
	protected void afterCreation(DomainAnnotation annotation) {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) annotation.getParameterAsList();
		if ( list.isEmpty() ) {
			throw newException("invalid parameter ('strategy' is required)");
		}
		else {
			// Check strategy 
			String strategy = list.get(0);
			if ( ! stategies.contains(strategy) ) {
				throw newException("invalid strategy '" + strategy + "'");
			}
		}
	}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelAttribute attribute, Object paramValue) {
		checkParamValue(paramValue);
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
				attribute.setGeneratedValueGeneratorName(list.get(1));
				attribute.setGeneratedValueSequenceName(list.get(2));
			}
			if ( list.size() >= 4 ) {
				attribute.setGeneratedValueAllocationSize(toInt(list.get(3)));
			}
			break;
		case TABLE:
			attribute.setGeneratedValueStrategy(GeneratedValueStrategy.TABLE);
			if ( list.size() >= 3 ) {
				attribute.setGeneratedValueGeneratorName(list.get(1));
				attribute.setGeneratedValueTableName(list.get(2));
			}
			if ( list.size() >= 6 ) {
				attribute.setGeneratedValueTablePkColumnName(list.get(3));
				attribute.setGeneratedValueTablePkColumnValue(list.get(4));
				attribute.setGeneratedValueTableValueColumnName(list.get(5));
			}
			if ( list.size() >= 7 ) {
				attribute.setGeneratedValueAllocationSize(toInt(list.get(6)));
			}
			break;
		default:
			throw newException("invalid strategy '" + strategy + "'");
		}
	}
}
