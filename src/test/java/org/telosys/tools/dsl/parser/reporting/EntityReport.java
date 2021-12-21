package org.telosys.tools.dsl.parser.reporting;

import java.util.List;

import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;

public class EntityReport {

	public static void print(DomainEntity entity) {
		System.out.println("");
		System.out.println("----------");
		System.out.println("ENTITY PARSING RESULT :");
		System.out.println(" Entity name : " + entity.getName() );
		System.out.println(" Entity fields : ");
		for ( DomainField field : entity.getFields() ) {
			System.out.println(" . " + field);
		}
		System.out.println(" Entity hasError() ? : " + entity.hasError());
		System.out.println(" Entity errors :");
//		List<FieldParsingError> errors = entity.getErrors();
		List<ParsingError> errors = entity.getErrors();
		for ( Exception e : errors ) {
			System.out.println(" . " + e.getClass().getSimpleName() + " : " + e.getMessage());
		}
		
	}

}
