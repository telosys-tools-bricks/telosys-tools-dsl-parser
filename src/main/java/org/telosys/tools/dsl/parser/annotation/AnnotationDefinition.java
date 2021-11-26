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
package org.telosys.tools.dsl.parser.annotation;

public class AnnotationDefinition {

	private final String name;
	private final AnnotationParamType  type;

	public AnnotationDefinition(String name) {
		this.name = name ;
		this.type = AnnotationParamType.NONE ;
	}

	public AnnotationDefinition(String name, AnnotationParamType type) {
		this.name = name ;
		this.type = type ;
	}

	protected String nameWithoutSuffix(String str) {
		return str.substring(0, str.length() - 1);
	}

	public String getName() {
		return name;
	}

	public AnnotationParamType getParamType() {
		return type;
	}
	
}
