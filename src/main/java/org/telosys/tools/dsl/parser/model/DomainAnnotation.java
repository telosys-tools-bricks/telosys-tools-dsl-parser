package org.telosys.tools.dsl.parser.model;

public class DomainAnnotation extends DomainAnnotationOrTag {

    public DomainAnnotation(String name) {
    	super(name);
    }
    
    public DomainAnnotation(String name, String param) {
    	super(name,param);
    }
    
    public DomainAnnotation(String name, Number param) {
    	super(name,param);
    }
    
    @Override
    public String toString() {
    	return "@" + super.toString();
    }

}
