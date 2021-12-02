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

import org.telosys.tools.dsl.parser.commnos.ParamValue;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainTag;

/**
 * Annotation parsing 
 * 
 * @author Laurent GUERIN
 */
public class TagParser extends AnnotationAndTagParser {

	/**
	 * Constructor
	 * 
	 * @param entityName
	 * @param fieldName
	 */
	protected TagParser(String entityName, String fieldName) {
		super(entityName, fieldName);
	}
	
	/**
	 * Constructor
	 * 
	 * @param entityName
	 */
	protected TagParser(String entityName) {
		super(entityName);
	}
	
	/**
	 * Parse the given raw tag, e.g. "#Foo" or "#Bar('abcd')"
	 * @param tagString
	 * @return
	 * @throws ParsingError
	 */
	public DomainTag parseTag(String tagString) throws ParsingError  {
		// get the tag name 
		String tagName = getName(tagString);

		// get the raw parameter value if any
		String rawParameterValue = getParameterValue(tagString);
		
		if ( rawParameterValue != null ) {
			ParamValue paramValue = buildTagParamValue(tagName, rawParameterValue);
			return new DomainTag(tagName, paramValue.getAsString());
		}
		else {
			return new DomainTag(tagName);
		}
	}	
}