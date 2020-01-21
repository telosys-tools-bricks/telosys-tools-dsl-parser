package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.model.DomainEntity;

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
    public final DomainEntity parseEntity(File file) {
    	
    	// ParserUtil.checkModelFile(file);
    	
    	EntityFileParser entityFileParser = new EntityFileParser(file);
    	EntityFileParsingResult result = entityFileParser.parse();
    	String entityNameFromFileName = result.getEntityNameFromFileName();
    	for ( Field field : result.getFields() ) {
//    		FieldNameAndType fieldNameAndType = parser.parseFieldNameAndType(field);
    		parseField(entityNameFromFileName, field); 
    	}
    	return null;
    }
    
    public final void parseField(String entityNameFromFileName, Field field) {
		FieldNameAndTypeParser parser = new FieldNameAndTypeParser(entityNameFromFileName, entitiesNames);
		FieldNameAndType fieldNameAndType = parser.parseFieldNameAndType(field);
		System.out.println("Field : name '" + fieldNameAndType.getName() 
			+ "' type '" + fieldNameAndType.getType() 
			+ "' cardinality = " + fieldNameAndType.getCardinality() );
    }
}
