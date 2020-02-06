package org.telosys.tools.dsl.parser;

import java.util.List;

import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;

public class FieldAnnotationsAndTagsParser {

	private final String entityNameFromFileName;

	public FieldAnnotationsAndTagsParser(String entityNameFromFileName) {
		this.entityNameFromFileName = entityNameFromFileName;
	}
	
	public FieldAnnotationsAndTags parse(String fieldName, FieldParts field) throws AnnotationOrTagError {
		return parse( fieldName, field.getAnnotationsPart());
	}

	public FieldAnnotationsAndTags parse(String fieldName, String annotationsAndTags) throws AnnotationOrTagError {
		FieldAnnotationsAndTags result = new FieldAnnotationsAndTags();

    	if ( annotationsAndTags == null || "".equals(annotationsAndTags) ) {
    		return result; // void
    	}
    	FieldAnnotationsAndTagsSplitter splitter = new FieldAnnotationsAndTagsSplitter(entityNameFromFileName, fieldName);
		List<String> elements = splitter.split(annotationsAndTags);
		for ( String element : elements ) {
			ParserLogger.log(" . '" + element + "'");
			FieldAnnotationOrTagParser annotationOrTagParser = new FieldAnnotationOrTagParser(entityNameFromFileName, fieldName);
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
