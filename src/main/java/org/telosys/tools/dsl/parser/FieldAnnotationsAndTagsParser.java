package org.telosys.tools.dsl.parser;

import java.util.List;

public class FieldAnnotationsAndTagsParser {

	private final String entityNameFromFileName;

	public FieldAnnotationsAndTagsParser(String entityNameFromFileName) {
		this.entityNameFromFileName = entityNameFromFileName;
	}
	
	public FieldAnnotationsAndTags parse(FieldBuilder field) {
		return parse( field.getAnnotationsPart());
	}

	public FieldAnnotationsAndTags parse(String annotationsAndTags) {
		FieldAnnotationsAndTags result = new FieldAnnotationsAndTags();

    	if ( annotationsAndTags == null || "".equals(annotationsAndTags) ) {
    		return result; // void
    	}
    	Splitter splitter = new Splitter();
		List<String> elements = splitter.split(annotationsAndTags);
		return result;
	}

}
