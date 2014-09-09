/**
 * Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.dsl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;

public class KeyWords {

	/**
	 * String to start a single line comment
	 */
	private final static String SINGLE_LINE_COMMENT = "//" ;
	
	/**
	 * List annoations names (# means 1 parameter)
	 */
	private final static String[] annotations = { 
		"Id",
		"NotNull",
		"Min#",
		"Max#",
		"SizeMin#",
		"SizeMax#",
		"Past",
		"Future" 
	} ;
	
	/**
	 * Return the single line comment string (ie '//' )  <br>
	 * @return
	 */
	public final static String getSingleLineComment() {
		return SINGLE_LINE_COMMENT ;
	}
	
	/**
	 * Return the list of neutral types keywords <br>
	 * e.g. 'string', 'integer', 'decimal', etc
	 * @return
	 */
	public final static List<String> getNeutralTypes() {
		return DomainNeutralTypes.getNames();
	}
	
	/**
	 * Return the list of annotations keywords <br>
	 * e.g. 'Id', 'NotNull', 'Min#', 'Max#', etc
	 * @return
	 */
	public final static List<String> getAnnotations() {
		return new LinkedList<String>(Arrays.asList(annotations));
	}
	
}