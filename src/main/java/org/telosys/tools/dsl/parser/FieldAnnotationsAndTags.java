package org.telosys.tools.dsl.parser;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;
import org.telosys.tools.dsl.parser.model.DomainTag;

public class FieldAnnotationsAndTags {

	private final List<DomainAnnotation>  annotations ;
	
	private final List<DomainTag>  tags ;
	
	public FieldAnnotationsAndTags() {
		super();
		this.annotations = new LinkedList<>();
		this.tags = new LinkedList<>();
	}

	public void addAnnotationOrTag(DomainAnnotationOrTag annotationOrTag) {
		if ( annotationOrTag instanceof DomainAnnotation) {
			annotations.add((DomainAnnotation)annotationOrTag);
		}
		else if ( annotationOrTag instanceof DomainTag) {
			tags.add((DomainTag)annotationOrTag);
		}
	}

	public List<DomainAnnotation> getAnnotations() {
		return annotations;
	}
	
//	public void addTag(DomainTag tag) {
//		tags.add(tag);
//	}
//
	public List<DomainTag> getTags() {
		return tags;
	}
	
}
