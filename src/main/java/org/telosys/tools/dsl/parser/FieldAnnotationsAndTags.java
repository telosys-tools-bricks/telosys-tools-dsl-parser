package org.telosys.tools.dsl.parser;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;

public class FieldAnnotationsAndTags {

	private final List<DomainAnnotationOrTag>  annotations ;
	// TODO : tags 
	
	public FieldAnnotationsAndTags() {
		super();
		this.annotations = new LinkedList<>();
	}

	public void addAnnotation(DomainAnnotationOrTag annotation) {
		annotations.add(annotation);
	}

	public List<DomainAnnotationOrTag> getAnnotations() {
		return annotations;
	}
	
}
