package org.telosys.tools.dsl;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKeyPart;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.LinkAttribute;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.TagContainer;
import org.telosys.tools.generic.model.enums.Cardinality;
import org.telosys.tools.generic.model.enums.FetchType;
import org.telosys.tools.generic.model.enums.Optional;
import org.telosys.tools.junit.utils.PrintUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DslModelManagerV33Test {
    
	private Model loadValidModel(String modelFileName) {
		System.out.println("----- " );
		System.out.println("Loading valid model : " + modelFileName );
        DslModelManager dslModelManager = new DslModelManager();
        Model model = dslModelManager.loadModel(modelFileName);
        PrintUtil.printErrors(dslModelManager.getErrors());
		if ( model == null ) {
			System.out.println("ERROR : cannot load model");
			System.out.println(dslModelManager.getErrorMessage());
			throw new RuntimeException("Cannot load model : " + dslModelManager.getErrorMessage());
		}
		// No error expected
		assertEquals(0, dslModelManager.getErrors().getNumberOfErrors());
		return model ;
    }
    
    @Test
    public void test1PeopleModel() {
        Model model = loadValidModel("src/test/resources/model_test/valid/PeopleModel");
        assertNotNull(model);
        //----- ENTITY "Country"
        Entity countryEntity = model.getEntityByClassName("Country");
        assertNotNull(countryEntity);
        //----- ENTITY "Gender"
        Entity genderEntity = model.getEntityByClassName("Gender");
        assertNotNull(genderEntity);        
        //----- ENTITY "Person"
        test1CheckPersonEntity((DslModelEntity) model.getEntityByClassName("Person") );        
        //----- ENTITY "Town"
        test1CheckTownEntity((DslModelEntity) model.getEntityByClassName("Town"));
        //----- ENTITY "Area"
        test1CheckAreaEntity((DslModelEntity) model.getEntityByClassName("Area"));
    }    
    private void test1CheckPersonEntity(DslModelEntity personEntity ) {
        Attribute attrib ;
    	Link link;
    	LinkAttribute joinAttribute ;

    	assertNotNull(personEntity);
        
        //--- 4 FK : 3 + 1 implicit FK
        PrintUtil.printForeignKeys(personEntity);
        assertEquals(4, personEntity.getForeignKeys().size());
        
        //--- FK referencing 'Gender'
        attrib = personEntity.getAttributeByName("genderId");
        assertEquals("genderId", attrib.getName() ) ;
        assertEquals("short", attrib.getNeutralType() ); 
        
        assertTrue(attrib.isFK());
        assertTrue(attrib.isFKSimple());
        assertFalse(attrib.isFKComposite());
        
        assertFalse(attrib.isKeyElement());
        
        //--- FK referencing 'Country'
        attrib = personEntity.getAttributeByName("countryCode");
        assertEquals("countryCode", attrib.getName() ) ;
        assertEquals("string", attrib.getNeutralType() ); 
        assertTrue(attrib.isFK());
        assertTrue(attrib.isFKSimple());
        assertFalse(attrib.isFKComposite());

		//----- LINKS
		assertNotNull(personEntity.getLinks() );
		assertEquals(4, personEntity.getLinks().size() );
		
        //--- Link 'country'  ( no annotation => FK inference )
        link = personEntity.getLinkByFieldName("country");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Country", link.getReferencedEntityName() );
        assertEquals(FetchType.LAZY, link.getFetchType() );
        assertEquals(Optional.TRUE, link.getOptional() );
        //assertEquals("foo", link.getMappedBy());
        // Join columns 
        assertEquals(1, link.getAttributes().size() );
        joinAttribute = link.getAttributes().get(0);
        assertEquals("countryCode", joinAttribute.getOriginAttributeName());        
        
        //--- Link 'gender'  ( no annotation => FK inference )
        link = personEntity.getLinkByFieldName("gender");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Gender", link.getReferencedEntityName() );
        assertTrue(link.isEmbedded());
        assertEquals(FetchType.DEFAULT, link.getFetchType() );
        assertEquals(Optional.UNDEFINED, link.getOptional() );
        // Join columns 
        assertEquals(1, link.getAttributes().size() );
        joinAttribute = link.getAttributes().get(0);
        assertEquals("genderId", joinAttribute.getOriginAttributeName());            	

        //--- Link 'Department' : @LinkByAttr(deptGroupCode > grpCode, deptDivisionCode>divCode)
        link = personEntity.getLinkByFieldName("department");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Department", link.getReferencedEntityName() );

        // Join columns 
        assertEquals(2, link.getAttributes().size() );
        joinAttribute = link.getAttributes().get(0);
        assertEquals("deptGroupCode", joinAttribute.getOriginAttributeName());
        assertEquals("grpCode",     joinAttribute.getReferencedAttributeName());
        joinAttribute = link.getAttributes().get(1);
        assertEquals("deptDivisionCode", joinAttribute.getOriginAttributeName());
        assertEquals("divCode",     joinAttribute.getReferencedAttributeName());
    }
    
    private void test1CheckTownEntity(DslModelEntity entity ) {
    	Link link;
    	assertNotNull(entity);
    	assertEquals(3, entity.getAttributes().size());
    	assertEquals(1, entity.getLinks().size());
        //--- Link 'country'
        link = entity.getLinkByFieldName("country");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Country", link.getReferencedEntityName() );
        
        //--- 1 FK : 1 implicit FK ('FK_IMPLICIT_1_Town_Country' : 'Town' --> 'Country' )
        PrintUtil.printForeignKeys(entity);
        assertEquals(1, entity.getForeignKeys().size());
        //--- is attribute aware of FK and FK parts ?
        Attribute countryCode = entity.getAttributeByName("countryCode");
        assertTrue(countryCode.isFK());
        assertTrue(countryCode.isFKSimple());
        assertFalse(countryCode.isFKComposite());
        List<ForeignKeyPart> fkParts = countryCode.getFKParts();
        assertEquals(1, fkParts.size());
        assertEquals("Country", fkParts.get(0).getReferencedEntityName());
        assertEquals("code", fkParts.get(0).getReferencedAttributeName());
        
        // Join attributes (v 3.4.0) 
        List<LinkAttribute> ja = link.getAttributes();
        assertNotNull(ja) ; 
        assertEquals(1, ja.size() );
        assertEquals("countryCode", ja.get(0).getOriginAttributeName());
        assertEquals("code",        ja.get(0).getReferencedAttributeName());

        // Link tags
        DslModelLink dslLink = (DslModelLink) link ;
        TagContainer tags = dslLink.getTagContainer();
        assertNotNull(tags);
        assertTrue(tags.containsTag("Foo"));
        assertEquals("", tags.getTagValue("Foo"));

        assertTrue(tags.containsTag("Bar"));
        assertEquals("xyz", tags.getTagValue("Bar"));
        assertEquals("xyz", tags.getTagValue("Bar", "aaaa"));

        assertFalse(tags.containsTag("Xyz"));
        assertEquals("", tags.getTagValue("Xyz"));
        assertEquals("myvalue", tags.getTagValue("Xyz", "myvalue"));
    }
    private void test1CheckAreaEntity(DslModelEntity entity ) {
    	Link link;
    	assertNotNull(entity);
    	assertEquals(1, entity.getLinks().size());
        //--- Link 'country'
        link = entity.getLinkByFieldName("country");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Country", link.getReferencedEntityName() );
        
        // Join attributes (v 3.4.0) 
        List<LinkAttribute> ja = link.getAttributes();
        assertNotNull(ja) ; 
        assertEquals(1, ja.size() );
        assertEquals("countryCode", ja.get(0).getOriginAttributeName());
        assertEquals("code",        ja.get(0).getReferencedAttributeName());
    }
    
    @Test
    public void test2SubGroupModel() {
        Model model = loadValidModel("src/test/resources/model_test/valid/SubGroupModel");
        
        assertNotNull(model);
        DslModelEntity entity ;
        Attribute attrib ;

        //----- SubGroup entity
        entity = (DslModelEntity) model.getEntityByClassName("SubGroup");
        assertEquals(1, entity.getForeignKeys().size());
        PrintUtil.printForeignKeys(entity);
        //--- Field with FK 
        attrib = entity.getAttributeByName("groupCode");
        assertEquals("groupCode", attrib.getName() ) ;
        assertEquals("string", attrib.getNeutralType() ); 
        assertTrue(attrib.isFK());
        assertTrue(attrib.isFKSimple());
        assertFalse(attrib.isFKComposite());

        //----- Person entity
        entity = (DslModelEntity) model.getEntityByClassName("Person");
        assertEquals(4, entity.getForeignKeys().size());
        PrintUtil.printForeignKeys(entity);
        //--- Field with FK 
        attrib = entity.getAttributeByName("groupCode");
        assertEquals("groupCode", attrib.getName() ) ;
        assertEquals("string", attrib.getNeutralType() ); 
        assertTrue(attrib.isFK());
        assertTrue(attrib.isFKSimple()); // in 2 FK : 1 FK is simple
        assertTrue(attrib.isFKComposite()); // in 2 FK : 1 FK is composite
        //--- Field with FK 
        attrib = entity.getAttributeByName("subgroupId");
        assertEquals("subgroupId", attrib.getName() ) ;
        assertEquals("int", attrib.getNeutralType() ); 
        assertTrue(attrib.isFK());
        assertFalse(attrib.isFKSimple()); 
        assertTrue(attrib.isFKComposite()); // only in 1 FK : composite
    }        

    @Test
    public void test3PointsModel() {
        Model model = loadValidModel("src/test/resources/model_test/valid/PointsModel");        
        assertNotNull(model);
        //----- 'Line' entity
        test3CheckLineEntity((DslModelEntity) model.getEntityByClassName("Line"));
    }
    private void test3CheckLineEntity(DslModelEntity entity ) {
    	Attribute attrib ;
    	
    	//----- Foreign Keys
        assertEquals(2, entity.getForeignKeys().size());
        PrintUtil.printForeignKeys(entity);
        
        //--- Field with FK 
        attrib = entity.getAttributeByName("point1X");
        assertEquals("int", attrib.getNeutralType() ); 
        assertTrue(attrib.isFK());
        assertFalse(attrib.isFKSimple()); 
        assertTrue(attrib.isFKComposite()); 
        
        //--- Field with FK 
        attrib = entity.getAttributeByName("point1Y");
        assertEquals("int", attrib.getNeutralType() ); 
        assertTrue(attrib.isFK());
        assertFalse(attrib.isFKSimple()); 
        assertTrue(attrib.isFKComposite()); 

        //--- Check Links
        assertEquals(2, entity.getLinks().size() );
        test3CheckLinePoint1Link(entity.getLinkByFieldName("point1"));
        test3CheckLinePoint2Link(entity.getLinkByFieldName("point2"));
    }
    private void test3CheckLinePoint1Link(Link link) {    	
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Point", link.getReferencedEntityName() );
//        assertNotNull(link.getId());
//        assertEquals("", link.getComparableString());

        CascadeOptions cascadeOptions = link.getCascadeOptions();
        assertFalse(cascadeOptions.isCascadeAll());
        assertFalse(cascadeOptions.isCascadeMerge());
        assertFalse(cascadeOptions.isCascadePersist());
        assertFalse(cascadeOptions.isCascadeRefresh());
        assertFalse(cascadeOptions.isCascadeRemove());
        
        assertTrue(link.isOwningSide());
        assertFalse(link.isInverseSide());
        
        assertTrue(link.isSelected()); // Always
        
        assertFalse(link.isEmbedded()); // @Embedded

        assertEquals(FetchType.DEFAULT, link.getFetchType()); // @FetchTypeLazy or @FetchTypeEager
        assertEquals(Optional.UNDEFINED, link.getOptional()); // @Optional
        
        assertNull( link.getMappedBy() ) ; // @MappedBy(xx)
        
        assertFalse(link.isBasedOnJoinEntity()); // only for MANY TO MANY
        assertNull( link.getJoinEntityName() ) ; // only for MANY TO MANY

        // Foreign Key : @LinkByFK(fk_line_point1)
        assertTrue(link.isBasedOnForeignKey()); 
        assertEquals("fk_line_point1", link.getForeignKeyName()) ; 

        // Join attributes (v 3.4.0) 
        List<LinkAttribute> ja = link.getAttributes();
        assertNotNull( ja ) ; 
        assertEquals(2, ja.size() );
        assertEquals("point1X", ja.get(0).getOriginAttributeName());
        assertEquals("x",       ja.get(0).getReferencedAttributeName());
        assertEquals("point1Y", ja.get(1).getOriginAttributeName());
        assertEquals("y",       ja.get(1).getReferencedAttributeName());
    }

    private void test3CheckLinePoint2Link(Link link) {
    	assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Point", link.getReferencedEntityName() );
//        assertNotNull(link.getId());
        
        assertTrue(link.isEmbedded()); // @Embedded 

        // Foreign Key : @LinkByFK(fk_line_point2)
        assertTrue(link.isBasedOnForeignKey()); 
        assertEquals("fk_line_point2", link.getForeignKeyName()) ; 

        // Join attributes (v 3.4.0) 
        List<LinkAttribute> ja = link.getAttributes();
        assertNotNull( ja ) ; 
        assertEquals(2, ja.size() );
        assertEquals("point2X", ja.get(0).getOriginAttributeName());
        assertEquals("x",       ja.get(0).getReferencedAttributeName());
        assertEquals("point2Y", ja.get(1).getOriginAttributeName());
        assertEquals("y",       ja.get(1).getReferencedAttributeName());
    }
}
