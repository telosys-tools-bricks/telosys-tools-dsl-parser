package org.telosys.tools.dsl.parser;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.parser.model.DomainModel;

public class EntityParserTest {
	@Before
	public void setUp() throws Exception {}

	@Test(expected = DslParserException.class)
	public void testParseFileWithAFileWhichDoesntExist() {
		File file = new File("entity_test/nul.entity");
		EntityParser parser = new EntityParser(new DomainModel("model"));
		parser.parse(file);
	}

    @Test(expected = DslParserException.class)
    public void testParseMissingBracket() {
        String testMissingBracket = "Entityid:integer;";

        EntityParser parser = new EntityParser(new DomainModel("model"));
//        parser.setFlattenContent(testMissingBracket);
//        parser.parseFlattenContent("Entity");
        parser.parseFlattenContent(testMissingBracket, "Entity");
    }

    @Test(expected = DslParserException.class)
    public void testParseMultipleEntities() {
        String testMultipleEntities = "Entity{id:integer;}Entity2{id:integer;}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testMultipleEntities, "Entity");
    }

    @Test(expected = DslParserException.class)
    public void testParseWithoutField() {
        String testEntityWithoutField = "Entity{}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
//        parser.setFlattenContent(testEntityWithoutField);
        parser.parseFlattenContent(testEntityWithoutField, "Entity");
    }

    @Test(expected = DslParserException.class)
    public void testParseEntityWithIllegalCharacters() {
        String testEntityNameIllegalCharacters = "E#n_tité{id:integer;}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testEntityNameIllegalCharacters, "E#n_tité");
    }

    @Test(expected = DslParserException.class)
    public void testParseFieldWithIllegalCharacters() {
        String testEntityFieldIllegalCharacters = "Entity{ié#_:integer;}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testEntityFieldIllegalCharacters, "Entity");
    }

    @Test(expected = DslParserException.class)
    public void testParseMissingSemiColumn() {
        String testMissingSemiColumn = "Entity{id:integer;name:string}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testMissingSemiColumn, "Entity");
    }

    @Test(expected = DslParserException.class)
    public void testParseMissingLastBracket() {
        String testMissingLastBracket = "Entity{id:integer;";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testMissingLastBracket, "Entity");
    }
    
    @Test(expected = DslParserException.class)
    public void testParseWithTwoIds() {
        String testWithTwoIds = "Entity{id:integer{@Id};idbis:integer{@Id};}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testWithTwoIds, "Entity");
    }
    
    @Test(expected = DslParserException.class)
    public void testParseWithAnIdInAnArray() {
        String testWithAnIdInAnArray = "Entity{id:integer[]{@Id};}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testWithAnIdInAnArray, "Entity");
    }
    
    @Test(expected = DslParserException.class)
    public void testParseWithAnIdInAClob() {
        String testWithAnIdInAClob = "Entity{id:clob{@Id};}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testWithAnIdInAClob, "Entity");
    }
    
    @Test(expected = DslParserException.class)
    public void testParseWithAnIdInABlob() {
        String testWithAnIdInABlob = "Entity{id:blob{@Id};}";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testWithAnIdInABlob, "Entity");
    }

    @Test(expected = DslParserException.class)
    public void testParseWithoutUcFirst() {
        String testMissingLastBracket = "entity{id:integer{@Id};};";

        EntityParser parser = new EntityParser(new DomainModel("model"));
        parser.parseFlattenContent(testMissingLastBracket, "entity");
    }
}
