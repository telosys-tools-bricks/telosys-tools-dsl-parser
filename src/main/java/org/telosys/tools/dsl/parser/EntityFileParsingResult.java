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

import java.util.List;

public class EntityFileParsingResult {

	private final String entityNameFromFileName;
	private final String entityNameParsed;
	private final List<FieldParts> fields;

	public EntityFileParsingResult(String entityNameFromFileName, String entityNameParsed, List<FieldParts> fields) {
		super();
		this.entityNameFromFileName = entityNameFromFileName;
		this.entityNameParsed = entityNameParsed;
		this.fields = fields;
	}

	public String getEntityNameFromFileName() {
		return entityNameFromFileName;
	}

	public String getEntityNameParsed() {
		return entityNameParsed;
	}

	public List<FieldParts> getFields() {
		return fields;
	}

}
