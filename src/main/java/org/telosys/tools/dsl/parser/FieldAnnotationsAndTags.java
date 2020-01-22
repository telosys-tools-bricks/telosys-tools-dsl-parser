package org.telosys.tools.dsl.parser;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;

public class FieldAnnotationsAndTags {

	private final List<DomainEntityFieldAnnotation>  annotations ;
	// TODO : tags 
	
	public FieldAnnotationsAndTags() {
		super();
		this.annotations = new LinkedList<>();
	}

	public void addAnnotation(DomainEntityFieldAnnotation annotation) {
		annotations.add(annotation);
	}

	public List<DomainEntityFieldAnnotation> getAnnotations() {
		return annotations;
	}
	
}
