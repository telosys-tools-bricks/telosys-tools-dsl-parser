package org.telosys.tools.dsl.parser;

import org.telosys.tools.dsl.parser.model.DomainType;

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
