package org.telosys.tools.dsl.parser.annotations.tools;

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.AnnotationProcessor;
import org.telosys.tools.dsl.parser.Element;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

public class AnnotationTool {
	
	private AnnotationTool() {}
	
	public static DomainAnnotation parseAnnotation(String annotation) throws DslModelError {
		Element element = new Element(2, annotation);
		AnnotationProcessor annotationProcessor = new AnnotationProcessor("MyEntity", "myField");
		return annotationProcessor.parseAnnotation(element);
	}

}
