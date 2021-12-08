package org.telosys.tools.dsl.parser.annotations;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.commons.ReferenceDefinitions;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.JoinColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LinkByAttrAnnotationTest {

	private DslModelAttribute buildAttribute(String name, boolean id, String databaseName) {
		DslModelAttribute attribute = new DslModelAttribute(name);
		attribute.setKeyElement(id);
		attribute.setDatabaseName(databaseName);
		return attribute;
	}
	private DslModelLink buildLink(String fieldName, String targetEntityName) {
		DslModelLink link = new DslModelLink(fieldName);
		link.setTargetEntityClassName(targetEntityName);
		return link;
	}
	
	private List<JoinColumn> getJoinColumnsForLinkToAuthor(String paramValue) {
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
		linkToAuthor.setTargetEntityClassName("Author");
		
		LinkByAttrAnnotation a = new LinkByAttrAnnotation();
		return a.getJoinColumns(model, bookEntity, linkToAuthor, paramValue);
	}

	private DslModelEntity buildPoint() {
		DslModelEntity entity = new DslModelEntity("Point");
		List<Attribute> attributes = new LinkedList<>();
		attributes.add( buildAttribute("x",    true,  "X") ) ;
		attributes.add( buildAttribute("y",    true,  "Y") ) ;
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
	private List<JoinColumn> getJoinColumnsForLinkToPoint(String paramValue) {
		DslModel model = buildLineAndPointModel();
		DslModelLink link = buildLink("point", "Point" );
		DslModelEntity lineEntity = (DslModelEntity) model.getEntityByClassName("Line");
		assertNotNull(lineEntity);
		
		LinkByAttrAnnotation a = new LinkByAttrAnnotation();
//		// check ref def
//		ReferenceDefinitions rd = a.buildReferenceDefinitions(paramValue);
//		assertEquals(2, rd.count());
//		assertEquals("pointX", rd.get(0).getName());
//		assertEquals("x", rd.get(0).getReferencedName());
//		assertEquals("pointY", rd.get(1).getName());
//		assertEquals("y", rd.get(1).getReferencedName());
		
		// get Join columns
		return a.getJoinColumns(model, lineEntity, link, paramValue);
	}
	private ReferenceDefinitions buildReferenceDefinitions(String s) {
		LinkByAttrAnnotation a = new LinkByAttrAnnotation();
		return a.buildReferenceDefinitions(s);
	}

	//-----------------------------------------------------------------------------
	// TESTS 
	//-----------------------------------------------------------------------------

	@Test
	public void test00() {
		ReferenceDefinitions rd = buildReferenceDefinitions("a > refA  ,  b > refB");
		assertEquals(2, rd.count());
		assertEquals("a",    rd.get(0).getName());
		assertEquals("refA", rd.get(0).getReferencedName());
		assertEquals("b",    rd.get(1).getName());
		assertEquals("refB", rd.get(1).getReferencedName());
	}

	//-----------------------------------------------------------------------------
	@Test (expected=RuntimeException.class)
	public void test01Err() {
		getJoinColumnsForLinkToAuthor((String)null) ;
	}

	@Test (expected=RuntimeException.class)
	public void test02Err() {
		getJoinColumnsForLinkToAuthor("") ;
	}

	@Test (expected=RuntimeException.class)
	public void test03Err() {
		getJoinColumnsForLinkToAuthor("   ") ;
	}

	@Test (expected=RuntimeException.class)
	public void test04Err() {
		getJoinColumnsForLinkToAuthor(" , ,  ") ;
	}

	@Test
	public void test05() {
		// @LinkByAttr(authorId)
		List<JoinColumn> jc = getJoinColumnsForLinkToAuthor(" authorId  ") ;
		assertEquals(1, jc.size());
		assertEquals("AUTHOR_ID", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
	}

	@Test
	public void test06() {
		// @LinkByAttr(...)
		List<JoinColumn> jc = getJoinColumnsForLinkToAuthor(" authorId, ") ;
		assertEquals(1, jc.size());
		assertEquals("AUTHOR_ID", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
	}

	@Test
	public void test07() {
		// @LinkByAttr(...)
		List<JoinColumn> jc = getJoinColumnsForLinkToAuthor(" ,, authorId ") ;
		assertEquals(1, jc.size());
		assertEquals("AUTHOR_ID", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
	}

	@Test (expected=RuntimeException.class)
	public void test08Err() {
		// @LinkByAttr(...)
		getJoinColumnsForLinkToAuthor(" authorIdxx ") ; // unknown attribute 'authorIdxx'
	}

	@Test
	public void test08() {
		// @LinkByAttr(...)
		List<JoinColumn> jc = getJoinColumnsForLinkToAuthor("authorId > id ,, ") ;
		assertEquals(1, jc.size());
		assertEquals("AUTHOR_ID", jc.get(0).getName());
		assertEquals("ID", jc.get(0).getReferencedColumnName());
	}

	@Test (expected=RuntimeException.class)
	public void test09Err() {
		// @LinkByAttr(...)
		getJoinColumnsForLinkToAuthor("authorId > idxx") ; // unknown attribute 'idxx'
	}
	
	//-----------------------------------------------------------------------------

	@Test 
	public void test21() {
		List<JoinColumn> jc = getJoinColumnsForLinkToPoint("pointX > x, pointY > y") ; 
		assertEquals(2, jc.size());
		assertEquals("POINT_X", jc.get(0).getName());
		assertEquals("X",       jc.get(0).getReferencedColumnName());
		assertEquals("POINT_Y", jc.get(1).getName());
		assertEquals("Y",       jc.get(1).getReferencedColumnName());
	}

	@Test (expected=RuntimeException.class)
	public void test22Err() {
		getJoinColumnsForLinkToPoint("pointX , pointY") ; 
		// missing referenced names
	}

	@Test (expected=RuntimeException.class)
	public void test23Err() {
		getJoinColumnsForLinkToPoint("pointX > a , pointY > b") ; 
		// unknown attribute 'a' in 'Point'
	}

	@Test (expected=RuntimeException.class)
	public void test24Err() {
		getJoinColumnsForLinkToPoint("pointX > x , pointY > b") ; 
		// unknown attribute 'b' in 'Point'
	}

	@Test (expected=RuntimeException.class)
	public void test25Err() {
		getJoinColumnsForLinkToPoint("px > x , py > y") ; 
		// unknown attribute 'px' in 'Line'
	}

}
