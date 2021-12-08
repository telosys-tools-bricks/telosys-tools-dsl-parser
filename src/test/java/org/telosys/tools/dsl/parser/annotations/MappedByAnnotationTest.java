package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.exceptions.AnnotationParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MappedByAnnotationTest {

	private static final String MAPPED_BY = "MappedBy";
	
	private AnnotationDefinition getAnnotationDefinition() {
		return new MappedByAnnotation() ;
	}
	private DomainAnnotation buildAnnotation(String referencedEntityName) throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		return a.buildAnnotation("Student", "teacher", referencedEntityName); 
	}
	
	@Test
	public void test1() {
		AnnotationDefinition a = getAnnotationDefinition();
		assertEquals( MAPPED_BY, a.getName() );
		assertEquals( AnnotationParamType.STRING, a.getParamType() );
		// Check scope
		assertFalse( a.hasAttributeScope() );
		assertTrue( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws ParsingError {
		DomainAnnotation da = buildAnnotation("Teacher"); 
		assertEquals( MAPPED_BY, da.getName() );
		assertEquals( "Teacher", da.getParameter()); 
	}

	@Test (expected=AnnotationParsingError.class)
	public void test3() throws ParsingError {
		buildAnnotation(null); 
		// Error : parameter required
	}
	@Test (expected=AnnotationParsingError.class)
	public void test4() throws ParsingError {
		buildAnnotation(""); 
		// Error : parameter required
	}
	@Test (expected=Exception.class)
	public void test5() throws ParsingError {
		buildAnnotation("   "); 
		// Error : invalid entity name (blank)
	}
	
	@Test 
	public void test6() throws ParsingError {
		DomainAnnotation da = buildAnnotation("Teacher"); 
		DslModel model = buildModel();
		DslModelEntity entity = (DslModelEntity) model.getEntityByClassName("Student");
		DslModelLink link = (DslModelLink) entity.getLinkByFieldName("teacher");
		da.applyToLink(model, entity, link);
	}
	
	@Test (expected=Exception.class)
	public void test7() throws ParsingError {
		DomainAnnotation da = buildAnnotation("FooBar"); 
		DslModel model = buildModel();
		DslModelEntity entity = (DslModelEntity) model.getEntityByClassName("Student");
		DslModelLink link = (DslModelLink) entity.getLinkByFieldName("teacher");
		da.applyToLink(model, entity, link);
		// Error : unknown entity 'FooBar'
	}
	
	//------------------------------------------------------------------------
	// Build fake model
	//------------------------------------------------------------------------

	private DslModelEntity buildStudentEntity() {
		DslModelEntity e = new DslModelEntity("Student");
		e.addAttribute(new DslModelAttribute("id"));
		e.addAttribute(new DslModelAttribute("lastName"));
		e.addLink(new DslModelLink("teacher"));
		return e;
	}
	private DslModelEntity buildTeacherEntity() {
		DslModelEntity e = new DslModelEntity("Teacher");
		e.addAttribute(new DslModelAttribute("code"));
		e.addAttribute(new DslModelAttribute("lastName"));
		return e;
	}
	private DslModel buildModel() {
		DslModel model = new DslModel("MyModel");
		model.addEntity(buildStudentEntity());
		model.addEntity(buildTeacherEntity());
		return model;
	}
}
