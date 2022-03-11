package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotations.tools.AnnotationTool;
import org.telosys.tools.dsl.parser.annotations.tools.FakeModelStudent;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MappedByAnnotationTest {

	private static final String ANNOTATION_NAME = "MappedBy";

	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}
	
	@Test
	public void test1() {
		AnnotationDefinition a = new MappedByAnnotation();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.STRING, a.getParamType() );
		// Check scope
		assertFalse( a.hasAttributeScope() );
		assertTrue( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws DslModelError {
		DomainAnnotation da = buildAnnotationWithParam("teacher"); 
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertEquals( "teacher", da.getParameter()); 
	}

	@Test (expected=DslModelError.class)
	public void test3() throws DslModelError {
		buildAnnotation(); 
		// Error : parameter required
	}
	@Test (expected=DslModelError.class)
	public void test4() throws DslModelError {
		buildAnnotationWithParam(""); 
		// Error : parameter required
	}
	@Test (expected=DslModelError.class)
	public void test5() throws DslModelError {
		buildAnnotationWithParam("   "); 
		// Error : invalid entity name (blank)
	}
	
	@Test 
	public void test6() throws DslModelError, ParamError {
		DomainAnnotation da = buildAnnotationWithParam("teacher"); 
		DslModel model = FakeModelStudent.buildModel();
		DslModelEntity entity = (DslModelEntity) model.getEntityByClassName("Teacher");
		DslModelLink link = (DslModelLink) entity.getLinkByFieldName("students");
		da.applyToLink(model, entity, link);
		assertEquals("teacher", link.getMappedBy());
	}
	
	@Test 
	public void test7() throws DslModelError, ParamError {
		DomainAnnotation da = buildAnnotationWithParam("unknown");  // Not checked at this step
		DslModel model = FakeModelStudent.buildModel();
		DslModelEntity entity = (DslModelEntity) model.getEntityByClassName("Teacher");
		DslModelLink link = (DslModelLink) entity.getLinkByFieldName("students");
		da.applyToLink(model, entity, link); // Not checked at this step
		assertEquals("unknown", link.getMappedBy());		
	}
}
