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

public class DbTableAnnotationTest {

	private static final String ANNOTATION_NAME = "DbTable";
	
	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInEntity("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInEntity("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}
	
	@Test
	public void test1() {
		AnnotationDefinition ad = new DbTableAnnotation();
		assertEquals( ANNOTATION_NAME, ad.getName() );
		assertEquals( AnnotationParamType.STRING, ad.getParamType() );
		// Check scope
		assertFalse( ad.hasAttributeScope() );
		assertFalse( ad.hasLinkScope() );
		assertTrue( ad.hasEntityScope() );
	}

	@Test 
	public void test2() throws DslModelError {
		DomainAnnotation annotation = buildAnnotationWithParam("EMPLOYEE"); // table name
		assertEquals( ANNOTATION_NAME, annotation.getName() );
		assertEquals( "EMPLOYEE", annotation.getParameter()); 
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
	
	@Test (expected=Exception.class)
	public void test6() throws DslModelError, ParamError {
		DomainAnnotation da = buildAnnotationWithParam("teacher"); 
		DslModel model = FakeModelStudent.buildModel();
		DslModelEntity entity = (DslModelEntity) model.getEntityByClassName("Teacher");
		DslModelLink link = (DslModelLink) entity.getLinkByFieldName("students");
		da.applyToLink(model, entity, link);
		// error : not applicable on link
	}
	
	@Test 
	public void test7() throws DslModelError, ParamError {
		DomainAnnotation da = buildAnnotationWithParam("TEACHER"); 
		DslModel model = FakeModelStudent.buildModel();
		DslModelEntity entity = (DslModelEntity) model.getEntityByClassName("Teacher");
		da.applyToEntity(model, entity);
		
		assertEquals( "TEACHER", entity.getDatabaseTable()); 
	}
}
