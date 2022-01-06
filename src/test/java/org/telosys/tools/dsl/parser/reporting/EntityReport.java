package org.telosys.tools.dsl.parser.reporting;

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;

public class EntityReport {

	public static void print(DomainEntity entity, DslModelErrors errors) {
		System.out.println("");
		System.out.println("----------");
		System.out.println("ENTITY PARSING RESULT :");
		System.out.println(" Entity name : " + entity.getName() );
		System.out.println(" Entity fields : ");
		for ( DomainField field : entity.getFields() ) {
			System.out.println(" . " + field);
		}
//		System.out.println(" Entity hasError() ? : " + entity.hasError());
//		System.out.println(" Entity errors :");
////		List<FieldParsingError> errors = entity.getErrors();
//		List<ParsingError> errors = entity.getErrors();
//		for ( Exception e : errors ) {
//			System.out.println(" . " + e.getClass().getSimpleName() + " : " + e.getMessage());
//		}
		System.out.println(" Entity errors :");
		for ( DslModelError err : errors.getErrors() ) {
			System.out.println(" . " + err);
		}
	}

}
 