package org.telosys.tools.dsl.parser.annotations;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotations.tools.AnnotationTool;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.LinkAttribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LinkByAttrAnnotationTest {

	private static final String ANNOTATION_NAME = "LinkByAttr";

	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}
	
	@Test
	public void test001() {
		AnnotationDefinition ad = new LinkByAttrAnnotation();
		assertEquals( ANNOTATION_NAME, ad.getName() );
		assertEquals( AnnotationParamType.LIST, ad.getParamType() );
		// Check scope
		assertFalse( ad.hasAttributeScope() );
		assertTrue( ad.hasLinkScope() );
		assertFalse( ad.hasEntityScope() );
	}

	@Test (expected=DslModelError.class)
	public void test002() throws DslModelError {
		buildAnnotation(); 
		// ERR : parameter required
	}
	
	private DslModelAttribute buildAttribute(String name, boolean id, String databaseName) {
		DslModelAttribute attribute = new DslModelAttribute(name, "fake-type");
		attribute.setKeyElement(id);
		attribute.setDatabaseName(databaseName);
		return attribute;
	}
	private DslModelLink buildLink(String fieldName, String targetEntityName) {
		DslModelLink link = new DslModelLink(fieldName);
		link.setReferencedEntityName(targetEntityName);
		return link;
	}
	
//	private List<JoinAttribute> getJoinAttributesForLinkToAuthor(String paramValue) throws DslModelError {
//		DomainAnnotation da = buildAnnotationWithParam(paramValue);
//		da.applyToLink(model, entity, link);
//	}

	//===================================================================================================
	
	private List<LinkAttribute> getLinkAttributesForLinkToAuthor(String paramValue) throws DslModelError, ParamError {
		List<Attribute> attributes;
		
		DslModelEntity authorEntity = new DslModelEntity("Author");
		attributes = new LinkedList<>();
		attributes.add( buildAttribute("id", true, "ID") ) ;
		attributes.add( buildAttribute("firstName", false, "FIRST_NAME") ) ;
		attributes.add( buildAttribute("lastName", false, "LAST_NAME") ) ;
		authorEntity.setAttributes(attributes);
		
		DslModelEntity bookEntity = new DslModelEntity("Book"); // Book link --> Author
		attributes = new LinkedList<>();
		attributes.add( buildAttribute("isbn", true, "ISBN") ) ;
		attributes.add( buildAttribute("title", false, "TITLE") ) ;
		attributes.add( buildAttribute("authorId", false, "AUTHOR_ID") ) ; // FK --> Author
		bookEntity.setAttributes(attributes);
		
		DslModel model =  new DslModel("MyTestModel");
		model.addEntity(bookEntity);
		model.addEntity(authorEntity);
		
		DslModelLink linkToAuthor = new DslModelLink("author");
		linkToAuthor.setReferencedEntityName("Author");
		
//		LinkByAttrAnnotation a = new LinkByAttrAnnotation();
////		return a.getJoinAttributes(model, bookEntity, linkToAuthor, paramValue);
//		a.apply(model, bookEntity, linkToAuthor, paramValue);
		DomainAnnotation da = buildAnnotationWithParam(paramValue);
		da.applyToLink(model, bookEntity, linkToAuthor);

		return linkToAuthor.getAttributes();
	}

	//===================================================================================================

	private DslModelEntity buildPoint() {
		DslModelEntity entity = new DslModelEntity("Point");
		List<Attribute> attributes = new LinkedList<>();
		attributes.add( buildAttribute("x",    true,  "X") ) ; // PK
		attributes.add( buildAttribute("y",    true,  "Y") ) ; // PK
		attributes.add( buildAttribute("name", false, "NAME") ) ;
		entity.setAttributes(attributes);
		return entity;
	}
	private DslModelEntity buildLine() {
		DslModelEntity entity = new DslModelEntity("Line"); // Line link --> Point
		List<Attribute> attributes = new LinkedList<>();
		attributes.add( buildAttribute("id",     true,  "ID") ) ;
		attributes.add( buildAttribute("color",  false, "COLOR") ) ;
		attributes.add( buildAttribute("pointX", false, "POINT_X") ) ; // FK --> Point
		attributes.add( buildAttribute("pointY", false, "POINT_Y") ) ; // FK --> Point
		entity.setAttributes(attributes);
		return entity;
	}
	private DslModel buildLineAndPointModel() {
		DslModel model =  new DslModel("LineAndPointModel");
		model.addEntity(buildPoint());
		model.addEntity(buildLine());
		return model;
	}
	private List<LinkAttribute> getLinkAttributesForLinkToPoint(String paramValue) throws DslModelError, ParamError {
		DslModel model = buildLineAndPointModel();
		DslModelLink link = buildLink("point", "Point" );
		DslModelEntity lineEntity = (DslModelEntity) model.getEntityByClassName("Line");
		assertNotNull(lineEntity);
		
		LinkByAttrAnnotation a = new LinkByAttrAnnotation();
		// get Join attributes
		DomainAnnotation da = buildAnnotationWithParam(paramValue);
		da.applyToLink(model, lineEntity, link);
		return link.getAttributes();
	}
//	private ReferenceDefinitions buildReferenceDefinitions(String s) {
//		LinkByAttrAnnotation a = new LinkByAttrAnnotation();
//		return a.buildReferenceDefinitions(s);
//	}

	//===================================================================================================

	//-----------------------------------------------------------------------------
	// TESTS 
	//-----------------------------------------------------------------------------

//	@Test
//	public void test00() {
//		ReferenceDefinitions rd = buildReferenceDefinitions("a > refA  ,  b > refB");
//		assertEquals(2, rd.count());
//		assertEquals("a",    rd.get(0).getName());
//		assertEquals("refA", rd.get(0).getReferencedName());
//		assertEquals("b",    rd.get(1).getName());
//		assertEquals("refB", rd.get(1).getReferencedName());
//	}

	//-----------------------------------------------------------------------------
	@Test (expected=ParamError.class)
	public void test01Err() throws DslModelError, ParamError {
		getLinkAttributesForLinkToAuthor((String)null) ;
	}

	@Test (expected=DslModelError.class)
	public void test02Err() throws DslModelError, ParamError {
		getLinkAttributesForLinkToAuthor("") ; // ERR : parameter required
	}

	@Test (expected=DslModelError.class)
	public void test03Err() throws DslModelError, ParamError {
		getLinkAttributesForLinkToAuthor("   ") ; // ERR : parameter required
	}

	@Test (expected=ParamError.class)
	public void test04Err() throws DslModelError, ParamError {
		getLinkAttributesForLinkToAuthor(" , ,  ") ; // ERR : invalid attribute (name is void)
	}

	@Test
	public void test05() throws DslModelError, ParamError {
		// @LinkByAttr(authorId)
		List<LinkAttribute> jc = getLinkAttributesForLinkToAuthor(" authorId  ") ;
		
		// Book.authorId -> Author.id
		assertEquals(1, jc.size());
		assertEquals("authorId", jc.get(0).getOriginAttributeName()); 
		assertEquals("id", jc.get(0).getReferencedAttributeName());
	}

	@Test  (expected=ParamError.class)
	public void test06Err() throws DslModelError, ParamError {
		// @LinkByAttr(...)
		getLinkAttributesForLinkToAuthor(" authorId, ") ; 
		// ERR : invalid attribute (name is void)
	}

	@Test (expected=ParamError.class)
	public void test07Err() throws DslModelError, ParamError {
		// @LinkByAttr(...)
		getLinkAttributesForLinkToAuthor(" ,, authorId ") ;
		// ERR : invalid attribute (name is void)
	}

	@Test (expected=ParamError.class)
	public void test08Err() throws DslModelError, ParamError {
		// @LinkByAttr(...)
		getLinkAttributesForLinkToAuthor(" authorIdxx ") ; // unknown attribute 'authorIdxx'
	}

//	@Test
//	public void test08() throws DslModelError, ParamError {
//		// @LinkByAttr(...)
//		List<JoinAttribute> jc = getJoinAttributesForLinkToAuthor("authorId > id ,, ") ;
//		assertEquals(1, jc.size());
//		assertEquals("AUTHOR_ID", jc.get(0).getOriginAttributeName());
//		assertEquals("ID", jc.get(0).getReferencedAttributeName());
//	}

//	@Test (expected=ParamError.class)
//	public void test09Err() throws DslModelError, ParamError {
//		// @LinkByAttr(...)
//		getJoinAttributesForLinkToAuthor("authorId > idxx") ; // unknown attribute 'idxx'
//	}
	
	//-----------------------------------------------------------------------------

	@Test 
	public void test21() throws DslModelError, ParamError {
		List<LinkAttribute> jc = getLinkAttributesForLinkToPoint(" pointX  , pointY ") ; 
		assertEquals(2, jc.size());
		assertEquals("pointX", jc.get(0).getOriginAttributeName());
		assertEquals("x",       jc.get(0).getReferencedAttributeName());
		assertEquals("pointY", jc.get(1).getOriginAttributeName());
		assertEquals("y",       jc.get(1).getReferencedAttributeName());
	}

	@Test // (expected=ParamError.class)
	public void test22Err() throws DslModelError, ParamError {
		List<LinkAttribute> jc = getLinkAttributesForLinkToPoint("pointX , pointY") ; 
		assertEquals(2, jc.size());
		assertEquals("pointX", jc.get(0).getOriginAttributeName());
		assertEquals("x",      jc.get(0).getReferencedAttributeName());
		assertEquals("pointY", jc.get(1).getOriginAttributeName());
		assertEquals("y",      jc.get(1).getReferencedAttributeName());
	}

	@Test (expected=ParamError.class)
	public void test23Err() throws DslModelError, ParamError {
		getLinkAttributesForLinkToPoint("pointA , pointY") ; 
		// unknown attribute 'pointA' 
	}

	@Test (expected=ParamError.class)
	public void test24Err() throws DslModelError, ParamError {
		getLinkAttributesForLinkToPoint("pointX  , pointB ") ; 
		// unknown attribute 'pointB' 
	}

	@Test (expected=ParamError.class)
	public void test25Err() throws DslModelError, ParamError {
		getLinkAttributesForLinkToPoint("pointX") ; 
		// 2 attributes expected
	}

	@Test (expected=ParamError.class)
	public void test26Err() throws DslModelError, ParamError {
		getLinkAttributesForLinkToPoint("pointX,  pointY, color") ;  // color exists
		// 2 attributes expected
	}

}
