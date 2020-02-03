package org.telosys.tools.dsl.parser;

import org.telosys.tools.dsl.parser.model.DomainType;

/**
 * This class contains the name and type of a field <br>
 * e.g. "firstName : string" or "books : Book[]" <br>
 * 
 * @author Laurent GUERIN
 *
 */
public class FieldNameAndType {

	private final String name ;
	private final String type ;
	private final int    cardinality ;
	private final DomainType domainType;
	
	public FieldNameAndType(String name, String type, int cardinality, DomainType domainType) {
		super();
		this.name = name;
		this.type = type;
		this.cardinality = cardinality;
		this.domainType = domainType;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}

	public int getCardinality() {
		return cardinality;
	}

	public DomainType getDomainType() {
		return domainType;
	}

}
