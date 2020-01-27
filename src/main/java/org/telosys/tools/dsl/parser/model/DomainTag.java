package org.telosys.tools.dsl.parser.model;

public class DomainTag extends DomainAnnotationOrTag {

    public DomainTag(String name) {
    	super(name);
    }
    
    public DomainTag(String name, String param) {
    	super(name,param);
    }
    
    @Override
    public String toString() {
    	return "#" + super.toString();
    }
}
