package org.telosys.tools.dsl.parser;

import java.util.List;

import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;

public class FieldAnnotationsAndTagsParser {

	private final String entityNameFromFileName;

	public FieldAnnotationsAndTagsParser(String entityNameFromFileName) {
		this.entityNameFromFileName = entityNameFromFileName;
	}
	
	public FieldAnnotationsAndTags parse(String fieldName, FieldParts field) {
		return parse( fieldName, field.getAnnotationsPart());
	}

	public FieldAnnotationsAndTags parse(String fieldName, String annotationsAndTags) {
		FieldAnnotationsAndTags result = new FieldAnnotationsAndTags();

    	if ( annotationsAndTags == null || "".equals(annotationsAndTags) ) {
    		return result; // void
    	}
    	Splitter splitter = new Splitter();
		List<String> elements = splitter.split(annotationsAndTags);
		for ( String element : elements ) {
			ParserLogger.log(" . '" + element + "'");
			AnnotationOrTagParser annotationOrTagParser = new AnnotationOrTagParser(entityNameFromFileName, fieldName);
			DomainAnnotationOrTag annotationOrTag;
			try {
				annotationOrTag = annotationOrTagParser.parse(element);
				result.addAnnotationOrTag(annotationOrTag);
			} catch (AnnotationOrTagError e) {
				// invalid annotation or tag 
				result.addError(e);
			}
		}
		return result;
	}

}
