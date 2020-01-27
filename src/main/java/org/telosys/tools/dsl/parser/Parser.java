package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainTag;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;

public class Parser {

    /**
     * Curent Model
     */
//    private final DomainModel model;
	private final List<String> entitiesNames;
	
    public Parser() {
		super();
		this.entitiesNames = getEntitiesNames();
	}

    private List<String> getEntitiesNames() {
    	List<String> list = new LinkedList<>();
    	list.add("Car");  // TODO : get entities names from model files 
    	return list;
    }
    
	/**
     * Parse the given model file
     *
     * @param file the ".model" file 
     * @return
     */
    /**
     * @param file
     * @return
     */
    public final DomainEntity parseEntity(File file) {
    	
    	// ParserUtil.checkModelFile(file);
    	EntityFileParser entityFileParser = new EntityFileParser(file);
    	EntityFileParsingResult result = entityFileParser.parse();
    	String entityNameFromFileName = result.getEntityNameFromFileName();
    	System.out.println("\n----------");
    	for ( FieldBuilder field : result.getFields() ) {
//    		FieldNameAndType fieldNameAndType = parser.parseFieldNameAndType(field);
    		parseField(entityNameFromFileName, field); 
    	}
    	return null;
    }
    
    public final void parseField(String entityNameFromFileName, FieldBuilder field) {
    	
    	// 1) Parse the field NAME and TYPE
		FieldNameAndTypeParser parser = new FieldNameAndTypeParser(entityNameFromFileName, entitiesNames);
		FieldNameAndType fieldNameAndType = parser.parseFieldNameAndType(field);
		System.out.println("Field : name '" + fieldNameAndType.getName() 
			+ "' type '" + fieldNameAndType.getType() 
			+ "' cardinality = " + fieldNameAndType.getCardinality() );
		
		String fieldName = fieldNameAndType.getName();
		
		// 2) Parse field ANNOTATIONS and TAGS
		FieldAnnotationsAndTagsParser fieldAnnotationsAndTagsParser = new FieldAnnotationsAndTagsParser(entityNameFromFileName);
		FieldAnnotationsAndTags fieldAnnotationsAndTags = fieldAnnotationsAndTagsParser.parse(fieldName, field);
		List<DomainAnnotationOrTag> annotationsAndTagsList = fieldAnnotationsAndTags.getAnnotations();
		System.out.println("\n--- ANNOTATIONS and TAGS : " + annotationsAndTagsList.size() );
		for ( DomainAnnotationOrTag annotationOrTag : annotationsAndTagsList ) {
			if ( annotationOrTag instanceof DomainTag ) {
				System.out.println(" . TAG : " + annotationOrTag );
			}
			else if ( annotationOrTag instanceof DomainAnnotation ) {
				System.out.println(" . ANNOTATION : " + annotationOrTag );
			}
		}
		System.out.println("--- ");
    }
}
