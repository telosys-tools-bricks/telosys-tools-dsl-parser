package org.telosys.tools.dsl.parser.annotations.tools;

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.AnnotationProcessor;
import org.telosys.tools.dsl.parser.Element;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainCardinality;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityType;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;

public class AnnotationTool {
	
	private static final String ENTITY_NAME = "MyEntity";
	
	private AnnotationTool() {}
	
	public static DomainAnnotation parseAnnotationInEntity(String annotation) throws DslModelError {
		Element element = new Element(2, annotation);
		// Entity level
		DomainEntity entity = new DomainEntity(ENTITY_NAME);
		AnnotationProcessor annotationProcessor =  new AnnotationProcessor(entity);
		// Parse
		return annotationProcessor.parseAnnotation(element);
	}
	
	public static DomainAnnotation parseAnnotationInAttribute(String annotation) throws DslModelError {
		Element element = new Element(2, annotation);
		// Field : Attribute
		DomainField field = new DomainField(12, "name", new DomainNeutralType("string") );
		AnnotationProcessor annotationProcessor =  new AnnotationProcessor(ENTITY_NAME, field);
		// Parse
		return annotationProcessor.parseAnnotation(element);
	}
	
	public static DomainAnnotation parseAnnotationInLink(String annotation) throws DslModelError {
		Element element = new Element(2, annotation);
		// Field : Link
		DomainField field = new DomainField(12, "country", new DomainEntityType("Country", DomainCardinality.ONE ) );
		AnnotationProcessor annotationProcessor =  new AnnotationProcessor(ENTITY_NAME, field);
		// Parse
		return annotationProcessor.parseAnnotation(element);
	}

}
