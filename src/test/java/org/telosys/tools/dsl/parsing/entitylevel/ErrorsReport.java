package org.telosys.tools.dsl.parsing.entitylevel;

import java.util.List;

import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;

public class ErrorsReport {

	public static void print(EntityParsingError exception) {
		System.out.println("");
		System.out.println("----------");
		System.out.println("ENTITY PARSING EXCEPTION :");
		System.out.println(" Entity name : " + exception.getEntityName() );
		System.out.println(" Message     : " + exception.getMessage() );
		System.out.println(" Line number : " + exception.getLineNumber() );
		System.out.println(" Fields errors :");
		List<FieldParsingError> fieldsErrors = exception.getFieldsErrors() ;
		for ( Exception e : fieldsErrors ) {
			System.out.println(" . " + e.getClass().getSimpleName() + " : " + e.getMessage());
		}
		
	}

}
