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
package org.telosys.tools.dsl.converter;

import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.BooleanValue;

public class Util {

	private Util() { 
	}
	
	/***
	public static BooleanValue getBooleanValue(String entityName, String fieldName, DomainAnnotation annotation) {
		String value = annotation.getParameterAsString();

		if ( ! StrUtil.nullOrVoid(value) ) {
			String v = value.trim().toUpperCase();
			if ("TRUE".equals(v)) {
				return BooleanValue.TRUE;
			}
			else if ("FALSE".equals(v)) {
				return BooleanValue.FALSE;
			}
		}
		throw new IllegalStateException( entityName
				+ "." + fieldName
				+ " : @"+ annotation.getName()
				+ " : " + "invalid boolean value " + value + " ('true' or 'false' expected)" );
	}
	***/
	
	public static BooleanValue getBooleanValue(String entityName, String fieldName, DomainAnnotation annotation) {
		if ( Boolean.TRUE.equals( annotation.getParameterAsBoolean() ) ) {
			return BooleanValue.TRUE;
		}
		else if ( Boolean.FALSE.equals( annotation.getParameterAsBoolean() ) ) {
			return BooleanValue.FALSE;
		}
		else {
			throw new IllegalStateException( entityName
					+ "." + fieldName
					+ " : @"+ annotation.getName()
					+ " : " + "invalid boolean value ('true' or 'false' expected)" );
		}
	}
}
