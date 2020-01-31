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
