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
package org.telosys.tools.dsl.parser;

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.dsl.parser.commons.ParamValue;
import org.telosys.tools.dsl.parser.model.DomainTag;

/**
 * Annotation parsing 
 * 
 * @author Laurent GUERIN
 */
public class TagProcessor extends AnnotationAndTagProcessor {

	/**
	 * Constructor
	 * 
	 * @param entityName
	 * @param fieldName
	 */
	public TagProcessor(String entityName, String fieldName) {
		super(entityName, fieldName);
	}
	
	/**
	 * Constructor
	 * 
	 * @param entityName
	 */
	public TagProcessor(String entityName) {
		super(entityName);
	}
	
	/**
	 * Parse the given tag element, e.g. "#Foo" or "#Bar('abcd')"
	 * @param element
	 * @return
	 * @throws DslModelError
	 */
	public DomainTag parseTag(Element element) throws DslModelError  {
		// get the tag name 
		String tagName = getName(element);

		// get the raw parameter value if any
		String rawParameterValue = getParameterValue(element);
		
		if ( rawParameterValue != null ) {
//			ParamValue paramValue = buildTagParamValue(tagName, rawParameterValue);
			ParamValue paramValue = new ParamValue(getEntityName(), rawParameterValue); 
			try {
				return new DomainTag(tagName, paramValue.getAsString());
			} catch (ParamError e) {
				throw newError(element.getLineNumber(), "'" + element.getContent() + "' : " + e.getMessage() );
			}
		}
		else {
			return new DomainTag(tagName);
		}
	}
	
//	protected ParamValue buildTagParamValue(String tagName, String rawParameterValue) {
//		String entityName = getEntityName();
//		String fieldName = getFieldName();
//		if ( fieldName != null ) {
//			// Tag defined at FIELD level
//			return new ParamValue(entityName, fieldName, tagName, rawParameterValue, 
//					ParamValueOrigin.FIELD_TAG);
//		}
//		else {
//			// Tag defined at ENTITY level
//			return new ParamValue(entityName, "", tagName, rawParameterValue, 
//					ParamValueOrigin.ENTITY_TAG);
//		}
//	}	
}