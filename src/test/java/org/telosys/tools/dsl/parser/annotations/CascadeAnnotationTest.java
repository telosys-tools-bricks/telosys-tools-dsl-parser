package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotations.tools.AnnotationTool;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.CascadeOptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CascadeAnnotationTest {

	private static final String ANNOTATION_NAME = "Cascade";

	//===================================================================================================	
	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}
	//===================================================================================================	
	private CascadeOptions getLinkCascadeOptions(String paramValue) throws DslModelError, ParamError {
		
		DomainAnnotation da = buildAnnotationWithParam(paramValue);

		DslModel model =  new DslModel("MyTestModel");
		DslModelEntity bookEntity = new DslModelEntity("Book");
		DslModelLink linkToAuthor = new DslModelLink("author");
		da.applyToLink(model, bookEntity, linkToAuthor);

		return linkToAuthor.getCascadeOptions();
	}
	//===================================================================================================	
	
	//-----------------------------------------------------------------------------
	// TESTS WITH ERRORS 
	//-----------------------------------------------------------------------------
	@Test (expected=DslModelError.class)
	public void testErrorNoParam() throws DslModelError {
		buildAnnotation(); // ERR : parameter required
	}

	@Test (expected=DslModelError.class)
	public void testError00() throws DslModelError {
		buildAnnotationWithParam(""); // ERR : parameter required
	}
	
	@Test (expected=ParamError.class)
	public void testError01() throws DslModelError, ParamError {
		getLinkCascadeOptions((String)null);
	}

	@Test (expected=DslModelError.class)
	public void testError02() throws DslModelError, ParamError {
		getLinkCascadeOptions("");
	}

	@Test (expected=DslModelError.class)
	public void testError03() throws DslModelError, ParamError {
		getLinkCascadeOptions("   ");
	}

	@Test (expected=ParamError.class)
	public void testError04() throws DslModelError, ParamError {
		getLinkCascadeOptions(" , ,  ");
	}

	@Test  (expected=ParamError.class)
	public void testError05() throws DslModelError, ParamError {
		getLinkCascadeOptions("ALL,,");
	}

	//-----------------------------------------------------------------------------
	// TESTS OK
	//-----------------------------------------------------------------------------
	@Test
	public void testAnnotationDefinition() {
		AnnotationDefinition ad = new CascadeAnnotation();
		assertEquals( ANNOTATION_NAME, ad.getName() );
		assertEquals( AnnotationParamType.LIST, ad.getParamType() );
		// Check scope
		assertFalse( ad.hasEntityScope() );
		assertFalse( ad.hasAttributeScope() );
		assertTrue( ad.hasLinkScope() );
	}

	@Test
	public void testAll() throws DslModelError, ParamError {
		CascadeOptions co ;
		// @Cascade(ALL)
		co = getLinkCascadeOptions("ALL");
		assertEquals(1, co.size());
		assertTrue(co.isCascadeAll());
		co = getLinkCascadeOptions("all");
		assertEquals(1, co.size());
		assertTrue(co.isCascadeAll());
		// @Cascade(A)
		co = getLinkCascadeOptions("A");
		assertEquals(1, co.size());
		assertTrue(co.isCascadeAll());
	}

	@Test
	public void testRemove() throws DslModelError, ParamError {
		CascadeOptions co ;
		// @Cascade(REMOVE)
		co = getLinkCascadeOptions("REMOVE");
		assertEquals(1, co.size());
		assertTrue(co.isCascadeRemove());
		// 
		co = getLinkCascadeOptions("ReMoVe");
		assertEquals(1, co.size());
		assertTrue(co.isCascadeRemove());
		// 
		co = getLinkCascadeOptions("REM");
		assertEquals(1, co.size());
		assertTrue(co.isCascadeRemove());
		// 
		co = getLinkCascadeOptions("rem");
		assertEquals(1, co.size());
		assertTrue(co.isCascadeRemove());
	}

	@Test
	public void test4Options() throws DslModelError, ParamError {
		CascadeOptions co ;
		// @Cascade(REMOVE)
		co = getLinkCascadeOptions("REM, P  , M,   Ref");
		assertEquals(4, co.size());
		assertTrue(co.isCascadeRemove());
		assertTrue(co.isCascadePersist());
		assertTrue(co.isCascadeMerge());
		assertTrue(co.isCascadeRefresh());
		assertFalse(co.isCascadeAll());
	}
}
