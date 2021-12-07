package org.telosys.tools.dsl;

import java.util.Map;

import org.junit.Test;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.Optional;
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
		PrintUtil.printErrors(dslModelManager);
		if ( model == null ) {
			System.out.println("ERROR : cannot load model");
			System.out.println(dslModelManager.getErrorMessage());
//			for ( Map.Entry<String, String> entry : modelLoader.getParsingErrors().entrySet() ) {
//				System.out.println(" . " + entry.getKey() + " : " + entry.getValue() );
//			}
			throw new RuntimeException("Cannot load model : " + dslModelManager.getErrorMessage());
		}
		// No error expected
		assertEquals(0, dslModelManager.getErrorMessage().length() );
		assertEquals(0, dslModelManager.getErrors().getAllErrorsCount() );
		return model ;
    }
    
//    private void printErrors(DslModelManager dslModelManager) {
//    	System.out.println("DslModelManager errors : " );
//    	System.out.println(" Error message : " + dslModelManager.getErrorMessage() );
//    	DslModelErrors errors = dslModelManager.getErrors();
//    	if ( errors != null ) {
//        	System.out.println(" All Errors Count : " + errors.getAllErrorsCount() );
//        	if ( ! errors.getAllErrorsList().isEmpty() ) {
//            	System.out.println(" All Errors : " );
//        		for ( String err : errors.getAllErrorsList() ) {
//                	System.out.println(" . " + err );
//        		}
//            	System.out.println(" Errors for each entity : " );
//        		for ( String entityName : errors.getEntities() ) {
//        			System.out.println(" --> Entity '" + entityName + "' : " );
//        			for ( String err : errors.getErrors(entityName) ) {
//                    	System.out.println(" . " + err );
//        			}
//        		}
//        	}
//    	}
//    }
    
//    private void printForeignKeys(Entity entity) {
//    	System.out.println("Foreign Keys for entity '" + entity.getClassName() + "' : " );
//        List<ForeignKey> fkList = entity.getDatabaseForeignKeys();
//        for ( ForeignKey fk : fkList ) {
//        	System.out.println(" . '" + fk.getName()  + "' : table '" + fk.getTableName() + "' --> table '" + fk.getReferencedTableName() +"'" );
//            for ( ForeignKeyColumn fkCol : fk.getColumns() ) {
//            	System.out.println("   - col '" + fkCol.getColumnName() + "' --> col '" + fkCol.getReferencedColumnName() + "'" );
//            }
//        }
//    }

    @Test
    public void test1_PeopleModel() throws EntityParsingError {
        Model model = loadValidModel("src/test/resources/model_test_v_3_3/People.model");
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
    	JoinColumn jc ;

    	assertNotNull(personEntity);
        
        //--- 
        PrintUtil.printForeignKeys(personEntity);
        assertEquals(2, personEntity.getDatabaseForeignKeys().size());
        
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
		
        //--- Link 'country'
        link = personEntity.getLinkByFieldName("country");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Person", link.getSourceTableName() ); 
        assertEquals("Country", link.getTargetEntityClassName() );
        assertEquals("Country", link.getTargetTableName() );
        assertEquals(FetchType.LAZY, link.getFetchType() );
        assertEquals(Optional.TRUE, link.getOptional() );
        //assertEquals("foo", link.getMappedBy());
        // Join columns 
        assertEquals(1, link.getJoinColumns().size() );
        jc = link.getJoinColumns().get(0);
        assertEquals("countryCode", jc.getName());        
        
        //--- Link 'gender'
        link = personEntity.getLinkByFieldName("gender");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Person", link.getSourceTableName() ); 
        assertEquals("Gender", link.getTargetEntityClassName() );
        assertEquals("Gender", link.getTargetTableName() );
        assertTrue(link.isEmbedded());
        assertEquals(FetchType.DEFAULT, link.getFetchType() );
        assertEquals(Optional.UNDEFINED, link.getOptional() );
        // Join columns 
        assertEquals(1, link.getJoinColumns().size() );
        jc = link.getJoinColumns().get(0);
        assertEquals("GENDER_ID", jc.getName());            	

        //--- Link 'Department'
        link = personEntity.getLinkByFieldName("department");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Person", link.getSourceTableName() ); 
        assertEquals("Department", link.getTargetEntityClassName() );
        assertEquals("Department", link.getTargetTableName() );
        // Join columns 
        assertEquals(2, link.getJoinColumns().size() );
        jc = link.getJoinColumns().get(0);
        assertEquals("DEP_GROUP_CODE", jc.getName());
        assertEquals("GROUP_CODE",     jc.getReferencedColumnName());
        jc = link.getJoinColumns().get(1);
        assertEquals("DEP_DIVISION_CODE", jc.getName());
        assertEquals("DIVISION_CODE",     jc.getReferencedColumnName());

        //--- Link 'Town'
        link = personEntity.getLinkByFieldName("town");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Person", link.getSourceTableName() ); 
        assertEquals("Town", link.getTargetEntityClassName() );
        assertEquals("Town", link.getTargetTableName() );
        // Join columns 
        assertEquals(1, link.getJoinColumns().size() );
        jc = link.getJoinColumns().get(0);
        assertEquals("TOWN_ID", jc.getName());
        //assertEquals("ID",      jc.getReferencedColumnName());
    }
    
    private void test1CheckTownEntity(DslModelEntity entity ) {
    	Link link;
    	JoinColumn jc ;
    	assertNotNull(entity);
    	assertEquals(2, entity.getAttributes().size());
    	assertEquals(1, entity.getLinks().size());
        //--- Link 'country'
        link = entity.getLinkByFieldName("country");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Town", link.getSourceTableName() ); 
        assertEquals("Country", link.getTargetEntityClassName() );
        assertEquals("Country", link.getTargetTableName() );
        assertNotNull(link.getJoinColumns());
//        // Case 1 Join Column
//        assertEquals(1, link.getJoinColumns().size() );
//        jc = link.getJoinColumns().get(0);
//        assertEquals("COUNTRY_CODE", jc.getName());
//        assertEquals("", jc.getReferencedColumnName() );
        // Case 2 Join Columns
        assertEquals(1, link.getJoinColumns().size() );
        jc = link.getJoinColumns().get(0);
        assertEquals("COUNTRY_CODE", jc.getName());
//        jc = link.getJoinColumns().get(1);
//        assertEquals("COUNTRY_CODE2", jc.getName());
        
        DslModelLink dslLink = (DslModelLink) link ;
        Map<String,String> tags = dslLink.getTagsMap();
        assertNotNull(tags);
        assertNotNull(tags.get("Foo"));
        assertEquals("",tags.get("Foo"));
        assertNotNull(tags.get("Bar"));
        assertEquals("xyz",tags.get("Bar"));
    }
    private void test1CheckAreaEntity(DslModelEntity entity ) {
    	Link link;
    	JoinColumn jc ;
    	assertNotNull(entity);
    	//assertEquals(3, entity.getAttributes().size());
    	assertEquals(1, entity.getLinks().size());
        //--- Link 'country'
        link = entity.getLinkByFieldName("country");
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Area", link.getSourceTableName() ); 
        assertEquals("Country", link.getTargetEntityClassName() );
        assertEquals("Country", link.getTargetTableName() );
        assertNotNull(link.getJoinColumns());
        // 1 Join Column
        assertEquals(1, link.getJoinColumns().size() );
        jc = link.getJoinColumns().get(0);
        assertEquals("COUNTRY_CODE", jc.getName());
        assertEquals("code", jc.getReferencedColumnName() );
    }
    
    @Test
    public void test2_SubGroupModel() throws EntityParsingError {
        Model model = loadValidModel("src/test/resources/model_test_v_3_3/SubGroup.model");
        
        assertNotNull(model);
        DslModelEntity entity ;
        Attribute attrib ;

        //----- SubGroup entity
        entity = (DslModelEntity) model.getEntityByClassName("SubGroup");
        assertEquals(1, entity.getDatabaseForeignKeys().size());
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
        assertEquals(4, entity.getDatabaseForeignKeys().size());
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
    public void test3_PointsModel() throws EntityParsingError {
        Model model = loadValidModel("src/test/resources/model_test_v_3_3/Points.model");        
        assertNotNull(model);
        //----- 'Line' entity
//        entity = (DslModelEntity) model.getEntityByClassName("Line");
        test3CheckLineEntity((DslModelEntity) model.getEntityByClassName("Line"));
        
//        assertEquals(2, entity.getDatabaseForeignKeys().size());
//        printForeignKeys(entity);
//        //--- Field with FK 
//        attrib = entity.getAttributeByName("point1X");
//        assertEquals("int", attrib.getNeutralType() ); 
//        assertTrue(attrib.isFK());
//        assertFalse(attrib.isFKSimple()); 
//        assertTrue(attrib.isFKComposite()); 
//        //--- Field with FK 
//        attrib = entity.getAttributeByName("point1Y");
//        assertEquals("int", attrib.getNeutralType() ); 
//        assertTrue(attrib.isFK());
//        assertFalse(attrib.isFKSimple()); 
//        assertTrue(attrib.isFKComposite()); 
        
    }
    private void test3CheckLineEntity(DslModelEntity entity ) {
    	Attribute attrib ;
    	
    	//----- Foreign Keys
        assertEquals(2, entity.getDatabaseForeignKeys().size());
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
    	JoinColumn jc ;
    	
        assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Line", link.getSourceTableName() ); 
        assertEquals("Point", link.getTargetEntityClassName() );
        assertEquals("Point", link.getTargetTableName() );
        assertNotNull(link.getId());
        assertEquals("", link.getComparableString());

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
        
        assertFalse(link.isBasedOnJoinTable()); // only for MANY TO MANY
        assertNull( link.getJoinTableName() ) ; // only for MANY TO MANY
        assertNull( link.getJoinTable() ) ; // only for MANY TO MANY        

        // Foreign Key : @LinkByFK(fk_line_point1)
        assertTrue(link.isBasedOnForeignKey()); 
        assertEquals("fk_line_point1", link.getForeignKeyName()) ; 
        // Join columns 
        assertNotNull( link.getJoinColumns() ) ; 
        assertEquals(2, link.getJoinColumns().size() );
        jc = link.getJoinColumns().get(0);
        assertEquals("X1", jc.getName());
        jc = link.getJoinColumns().get(1);
        assertEquals("Y1", jc.getName());
    }
    private void test3CheckLinePoint2Link(Link link) {
    	JoinColumn jc ;
    	assertEquals(Cardinality.MANY_TO_ONE, link.getCardinality());
        assertEquals("Line", link.getSourceTableName() ); 
        assertEquals("Point", link.getTargetEntityClassName() );
        assertEquals("Point", link.getTargetTableName() );
        assertNotNull(link.getId());
        
        assertTrue(link.isEmbedded()); // @Embedded 

        // Foreign Key : @LinkByFK(fk_line_point2)
        assertTrue(link.isBasedOnForeignKey()); 
        assertEquals("fk_line_point2", link.getForeignKeyName()) ; 
        // Join columns 
        assertNotNull( link.getJoinColumns() ) ; 
        assertEquals(2, link.getJoinColumns().size() );
        jc = link.getJoinColumns().get(0);
        assertEquals("X2", jc.getName());
        jc = link.getJoinColumns().get(1);
        assertEquals("Y2", jc.getName());
    }
}
